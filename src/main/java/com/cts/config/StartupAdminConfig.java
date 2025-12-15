package com.cts.config;

import com.cts.model.Role;
import com.cts.model.User;
import com.cts.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class StartupAdminConfig {

    @Bean
    public ApplicationRunner createAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            String adminUsername = "admin";
            if (!repo.existsByUsername(adminUsername)) {
                User u = new User();
                u.setUsername(adminUsername);
                u.setEmail("admin@example.com");
                u.setPassword(encoder.encode("Admin@123"));
                u.setRoles(Set.of(Role.ADMIN, Role.USER));
                repo.save(u);
            }
        };
    }
}
