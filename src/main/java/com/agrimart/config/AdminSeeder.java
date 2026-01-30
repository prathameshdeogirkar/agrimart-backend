package com.agrimart.config;

import com.agrimart.entity.Role;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class AdminSeeder {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<User> admin = userRepository.findByEmail("admin@agrimart.com");
            if (admin.isEmpty()) {
                System.out.println("🚀 Creating default Admin user...");
                User newAdmin = User.builder()
                        .email("admin@agrimart.com")
                        .password(passwordEncoder.encode("admin123"))
                        .name("Super Admin")
                        .role(Role.ADMIN)
                        .mobile("9876543210")
                        .build();

                userRepository.save(newAdmin);
                System.out.println("✅ Admin user created: admin@agrimart.com / admin123");
            } else {
                System.out.println("ℹ️ Admin user already exists. Skipping creation.");
            }
        };
    }
}
