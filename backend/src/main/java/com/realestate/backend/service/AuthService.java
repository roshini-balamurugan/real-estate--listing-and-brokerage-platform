package com.realestate.backend.service;

import com.realestate.backend.dto.AuthRequest;
import com.realestate.backend.dto.AuthResponse;
import com.realestate.backend.dto.RegisterRequest;
import com.realestate.backend.dto.UserDTO;
import com.realestate.backend.model.Role;
import com.realestate.backend.model.User;
import com.realestate.backend.repository.UserRepository;
import com.realestate.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new RuntimeException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());

        if (request.getRole() == Role.ROLE_AGENT) {
            user.setAgencyName(request.getAgencyName());
            user.setLicenseNumber(request.getLicenseNumber());
            user.setAgentVerified(false); // requires admin verification
        } else if (request.getRole() == Role.ROLE_ADMIN) {
            user.setAgentVerified(true);
        } else {
            user.setAgentVerified(null);
        }

        User savedUser = userRepository.save(user);

        // Send welcome notification
        String welcomeMsg = "Welcome to the Real Estate Platform, " + savedUser.getName() + "!";
        if (savedUser.getRole() == Role.ROLE_AGENT) {
            welcomeMsg += " Your agent verification request is submitted and pending admin approval.";
        }
        notificationService.createNotification(savedUser, "Welcome to Real Estate Platform", welcomeMsg, "SYSTEM");

        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getRole().name(), savedUser.getId(), savedUser.getName());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getPhone(),
                savedUser.getAgencyName(),
                savedUser.getLicenseNumber(),
                savedUser.getAgentVerified()
        );
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name(), user.getId(), user.getName());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getPhone(),
                user.getAgencyName(),
                user.getLicenseNumber(),
                user.getAgentVerified()
        );
    }

    public UserDTO getCurrentUser(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return UserDTO.fromUser(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
