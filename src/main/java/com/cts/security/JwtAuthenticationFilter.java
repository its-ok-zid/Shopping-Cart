package com.cts.security;

import com.cts.serviceImpl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Value("${security.jwt.cookie-name:JWT_TOKEN}")
    private String jwtCookieName;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromCookies(request);
        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    // Load user details to get authorities (ensures user still exists)
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    // Alternatively you can extract roles directly from token:
                    List<SimpleGrantedAuthority> authorities = jwtUtil.getRolesFromToken(token)
                            .stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                    // create auth token and set context
                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities() != null ? userDetails.getAuthorities() : authorities);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (JwtException | IllegalArgumentException ex) {
                // Invalid/expired token — do not throw; clear context and continue filter chain.
                SecurityContextHolder.clearContext();
                // optionally log at debug level: logger.debug("Invalid JWT: {}", ex.getMessage());
            } catch (org.springframework.security.core.userdetails.UsernameNotFoundException unfe) {
                // user deleted after token issued — clear context and continue
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (jwtCookieName.equals(cookie.getName())) {
                String val = cookie.getValue();
                if (val == null || val.isBlank()) return null;
                return val;
            }
        }
        return null;
    }
}
