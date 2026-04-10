package com.elib.identity.service;

import com.elib.identity.dto.AuthResponse;
import com.elib.identity.dto.AuthRequest;
import com.elib.identity.dto.UserResponse;
import com.elib.identity.entity.User;
import com.elib.identity.exception.ResourceNotFoundException;
import com.elib.identity.mapper.UserMapper;
import com.elib.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserDetailsService userDetailsService;

    @Transactional(readOnly = true)
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user: {}", request.emailOrUsername());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.emailOrUsername(),
                request.password()
            )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found"));

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(userResponse)
            .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails =
            userDetailsService.loadUserByUsername(username);

        String newAccessToken =
            jwtService.generateAccessToken(userDetails);
        String newRefreshToken =
            jwtService.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found"));

        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(userResponse)
            .build();
    }
}
