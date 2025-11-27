package com.microservice.authentication.services;

import com.microservice.authentication.clients.UserClient;
import com.microservice.authentication.dto.AuthRequest;
import com.microservice.authentication.dto.AuthResponse;
import com.microservice.authentication.dto.RegisterRequest;
import com.microservice.authentication.dto.UserDto;
import com.microservice.authentication.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        Optional<UserDto> userOpt = userClient.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            UserDto user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtProvider.createTokenWithUserInfo(user.getEmail(), user.getRole(), user.getId(), user.getNombre(), user.getTelefono(), user.getRegion(), user.getComuna());
                return new AuthResponse(token);
            }
        }
        throw new RuntimeException("Invalid credentials");
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userClient.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Set default role if not provided
        String role = (request.getRole() == null || request.getRole().isEmpty()) ? "CLIENT" : request.getRole();

        // Create UserDto from RegisterRequest
        UserDto userDto = UserDto.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(hashedPassword)
                .telefono(request.getTelefono())
                .region(request.getRegion())
                .comuna(request.getComuna())
                .role(role)
                .build();

        UserDto savedUser = userClient.save(userDto);
        String token = jwtProvider.createTokenWithUserInfo(savedUser.getEmail(), savedUser.getRole(), savedUser.getId(), savedUser.getNombre(), savedUser.getTelefono(), savedUser.getRegion(), savedUser.getComuna());
        return new AuthResponse(token);
    }

    public boolean validate(String token) {
        return jwtProvider.validate(token);
    }
}
