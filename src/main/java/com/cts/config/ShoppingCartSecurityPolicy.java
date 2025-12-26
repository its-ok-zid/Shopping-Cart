package com.cts.config;

import com.zidtech.common.security.service.SecurityPolicyCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class ShoppingCartSecurityPolicy implements SecurityPolicyCustomizer {

    @Override
    public void customize(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasRole("USER")
                .anyRequest().authenticated()
        );
    }
}
