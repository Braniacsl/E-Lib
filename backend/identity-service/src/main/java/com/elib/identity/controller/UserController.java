package com.elib.identity.controller;

import com.elib.identity.dto.UserRequest;
import com.elib.identity.dto.UserResponse;
import com.elib.identity.dto.UserSummaryResponse;
import com.elib.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
            description = "User created"),
        @ApiResponse(responseCode = "400",
            description = "Invalid input"),
        @ApiResponse(responseCode = "409",
            description = "Email or username already exists")
    })
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "User found"),
        @ApiResponse(responseCode = "404",
            description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    @Operation(summary = "Get user summary by ID (internal)")
    public ResponseEntity<UserSummaryResponse> getUserSummary(
            @PathVariable UUID id) {
        UserSummaryResponse response =
            userService.getUserSummary(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "User found"),
        @ApiResponse(responseCode = "404",
            description = "User not found")
    })
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "User updated"),
        @ApiResponse(responseCode = "400",
            description = "Invalid input"),
        @ApiResponse(responseCode = "404",
            description = "User not found"),
        @ApiResponse(responseCode = "409",
            description = "Email or username already exists")
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest request) {
        UserResponse response =
            userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204",
            description = "User deleted"),
        @ApiResponse(responseCode = "404",
            description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles/{role}")
    @Operation(summary = "Add role to user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Role added"),
        @ApiResponse(responseCode = "404",
            description = "User not found")
    })
    public ResponseEntity<UserResponse> addRoleToUser(
            @PathVariable UUID id,
            @PathVariable String role) {
        UserResponse response =
            userService.addRoleToUser(id, role);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles/{role}")
    @Operation(summary = "Remove role from user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Role removed"),
        @ApiResponse(responseCode = "404",
            description = "User not found")
    })
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable UUID id,
            @PathVariable String role) {
        UserResponse response =
            userService.removeRoleFromUser(id, role);
        return ResponseEntity.ok(response);
    }
}
