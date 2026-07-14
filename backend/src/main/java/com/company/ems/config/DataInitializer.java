package com.company.ems.config;

import com.company.ems.entity.Employee;
import com.company.ems.entity.User;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Seeds default admin user and sample employees on first startup.
 * Runs once when database is empty.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(UserRepository userRepo, EmployeeRepository empRepo,
                               PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() == 0) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepo.save(admin);
            }

            if (empRepo.count() == 0) {
                Employee e1 = new Employee();
                e1.setFirstName("John");
                e1.setLastName("Doe");
                e1.setEmail("john.doe@company.com");
                e1.setDepartment("Engineering");
                e1.setJobTitle("Software Engineer");
                e1.setHireDate(LocalDate.of(2022, 1, 15));
                e1.setSalary(75000.0);

                Employee e2 = new Employee();
                e2.setFirstName("Sarah");
                e2.setLastName("Wilson");
                e2.setEmail("sarah.wilson@company.com");
                e2.setDepartment("HR");
                e2.setJobTitle("HR Manager");
                e2.setHireDate(LocalDate.of(2021, 6, 1));
                e2.setSalary(68000.0);

                empRepo.save(e1);
                empRepo.save(e2);
            }
        };
    }
}
