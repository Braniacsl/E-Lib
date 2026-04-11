package com.elib.identity.service;

import com.elib.identity.dto.UserRequest;
import com.elib.identity.dto.UserResponse;
import com.elib.identity.dto.UserSummaryResponse;
import com.elib.identity.entity.User;
import com.elib.identity.exception.ResourceNotFoundException;
import com.elib.identity.mapper.UserMapper;
import com.elib.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                "User with email " + request.email() + " already exists");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                "Username " + request.username() + " is already taken");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("User created with ID: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + id));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserSummary(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + id));
        return userMapper.toSummaryResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponse)
            .toList();
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + id));

        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                "User with email " + request.email()
                    + " already exists");
        }

        if (!user.getUsername().equals(request.username())
                && userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                "Username " + request.username()
                    + " is already taken");
        }

        userMapper.updateEntityFromRequest(request, user);

        if (request.password() != null
                && !request.password().isEmpty()) {
            user.setPassword(
                passwordEncoder.encode(request.password()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse addRoleToUser(UUID userId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + userId));
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserResponse removeRoleFromUser(UUID userId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User not found with ID: " + userId));
        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
}
