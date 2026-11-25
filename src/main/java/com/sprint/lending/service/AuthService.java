package com.sprint.lending.service;

import com.sprint.lending.dto.AuthResponse;
import com.sprint.lending.dto.LoginRequest;
import com.sprint.lending.dto.RegisterRequest;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;
import com.sprint.lending.exception.BadRequestException;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.UserRepository;
import com.sprint.lending.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BorrowerProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       BorrowerProfileRepository profileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (profileRepository.existsByPan(req.getPan().toUpperCase())) {
            throw new BadRequestException("PAN number is already registered");
        }

        User user = new User();
        user.setEmail(req.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.BORROWER);
        user = userRepository.save(user);

        BorrowerProfile profile = new BorrowerProfile();
        profile.setFullName(req.getFullName().trim());
        profile.setPan(req.getPan().trim().toUpperCase());
        profile.setMonthlyIncome(req.getMonthlyIncome());
        profile.setEmploymentType(req.getEmploymentType());
        profile.setUser(user);
        profileRepository.save(profile);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), profile.getFullName());
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        String fullName = user.getBorrowerProfile() != null
                ? user.getBorrowerProfile().getFullName()
                : "System Admin";

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), fullName);
    }
}