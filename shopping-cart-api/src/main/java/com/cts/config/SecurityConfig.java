package com.cts.config;

import com.cts.security.JwtAuthenticationFilter;
import com.cts.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize on controller methods
public class SecurityConfig {

    private final JwtTokenService jwtTokenService;
    
    // Pulls from application.properties, defaults to localhost:3000 for React
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    public SecurityConfig(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery) because we are using stateless JWTs
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Enable CORS using the configuration source bean below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 3. Set Session Management to Stateless (Spring won't create JSESSIONID cookies)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Configure Route Authorizations based on your API Contract
            .authorizeHttpRequests(auth -> auth
                // Public Authentication Endpoints
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                
                // Public Product Catalog Browsing
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                
                // Public Swagger/OpenAPI Documentation Endpoints
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Everything else requires the user to be authenticated
                .anyRequest().authenticated()
            )
            
            // 5. Insert our custom JWT filter BEFORE the standard Spring Username/Password filter
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allows requests from our frontend
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        // Allows standard HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Allows the frontend to send the Authorization header
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // CRITICAL: Allows the browser to send/receive the HttpOnly Refresh Token cookie
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}