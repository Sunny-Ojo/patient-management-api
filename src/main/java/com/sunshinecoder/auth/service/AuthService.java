package com.sunshinecoder.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sunshinecoder.auth.dto.AuthResponse;
import com.sunshinecoder.auth.dto.UserLoginRequest;
import com.sunshinecoder.auth.dto.UserRegistrationRequest;
import com.sunshinecoder.auth.entity.User;
import com.sunshinecoder.auth.repository.TokenBlacklistRepository;
import com.sunshinecoder.auth.repository.UserRepository;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    TokenBlacklistRepository tokenBlacklistRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Transactional
    public AuthResponse registerUser(UserRegistrationRequest request) {

        if (userRepository.isEmailOrPhoneRegistered(request.getEmail(), request.getPhoneNumber())) {
        throw new BadRequestException("Email or phone number already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(hashPassword(request.getPassword()));

        userRepository.persist(user);

        // Generate JWT token
        String token = generateJwtToken(user);
        
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getPhoneNumber());
    }

    public AuthResponse loginUser(UserLoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotAuthorizedException("Invalid email or password"));

        // Verify password
        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new NotAuthorizedException("Invalid email or password");
        }

        // Generate JWT token
        String token = generateJwtToken(user);

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getPhoneNumber());
    }


    @Transactional
    public void logout(String token) {
        if (token == null || token.isBlank()) throw new BadRequestException("No token provided");

        if (!tokenBlacklistRepository.isBlacklisted(token)) {
            tokenBlacklistRepository.blacklistToken(token);
        }

    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword);
        return result.verified;
    }

    private String generateJwtToken(User user) {
        return Jwt.issuer(issuer)
                .upn(user.getEmail())
                .groups(new HashSet<>(List.of("USER")))
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .expiresIn(Duration.ofHours(24))
                .sign();
    }
}