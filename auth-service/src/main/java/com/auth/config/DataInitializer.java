package com.auth.config;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setName("Super Admin");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // hashed password
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);

            System.out.println("Admin user created: admin/admin123");
        }
    }
}
