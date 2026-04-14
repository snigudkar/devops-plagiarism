package com.authmodule.service;

import com.authmodule.dto.AuthResponse;
import com.authmodule.dto.LoginRequest;
import com.authmodule.dto.RegisterRequest;
import com.authmodule.entity.User;
import com.authmodule.exception.InvalidCredentialsException;
import com.authmodule.exception.UserAlreadyExistsException;
import com.authmodule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * 
     * @param request RegisterRequest containing email, name, and password
     * @return AuthResponse with registration result
     * @throws UserAlreadyExistsException if user with email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: User with email {} already exists", request.getEmail());
            throw new UserAlreadyExistsException(
                    "User with email " + request.getEmail() + " already exists");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(hashedPassword)
                .role("USER")
                .build();

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", savedUser.getEmail());

        // Build response
        return AuthResponse.builder()
                .success(true)
                .message("User registered successfully")
                .user(AuthResponse.UserDTO.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .name(savedUser.getName())
                        .role(savedUser.getRole())
                        .build())
                .build();
    }

    /**
     * Login user with email and password
     * 
     * @param request LoginRequest containing email and password
     * @return AuthResponse with login result
     * @throws InvalidCredentialsException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user with email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: User with email {} not found", request.getEmail());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Invalid password for user {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getEmail());

        // Build response (token generation can be extended with JWT)
        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .token("") // Can be extended with JWT token generation
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .build())
                .build();
    }

    /**
     * Get user details by ID
     * 
     * @param userId User ID
     * @return User details
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.debug("Fetching user with id: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Get user by email
     * 
     * @param email User email
     * @return User details
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
