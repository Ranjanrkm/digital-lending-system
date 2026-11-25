package com.sprint.lending.config;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BorrowerProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      BorrowerProfileRepository profileRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@lending.com")) {
            User admin = new User();
            admin.setEmail("admin@lending.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("rahul@demo.com")) {
            User borrowerUser = new User();
            borrowerUser.setEmail("rahul@demo.com");
            borrowerUser.setPassword(passwordEncoder.encode("borrower123"));
            borrowerUser.setRole(Role.BORROWER);
            userRepository.save(borrowerUser);

            BorrowerProfile profile = new BorrowerProfile();
            profile.setFullName("Rahul Sharma");
            profile.setPan("ABCDE1234F");
            profile.setMonthlyIncome(new BigDecimal("85000.00"));
            profile.setEmploymentType("SALARIED");
            profile.setUser(borrowerUser);
            profileRepository.save(profile);
        }
    }
}