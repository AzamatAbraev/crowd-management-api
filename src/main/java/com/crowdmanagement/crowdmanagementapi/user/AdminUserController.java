package com.crowdmanagement.crowdmanagementapi.user;

import com.crowdmanagement.crowdmanagementapi.user.dto.*;
import com.crowdmanagement.crowdmanagementapi.utils.ApiResponse;
import com.crowdmanagement.crowdmanagementapi.utils.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('theking')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUsers(
            @RequestParam(required = false) String search) {

        List<UserRepresentationDto> users = (search != null && !search.isBlank())
                ? adminUserService.searchUsers(search)
                : adminUserService.getAllUsers();

        return ResponseBuilder.build(HttpStatus.OK, "Users fetched successfully", users);
    }


    @GetMapping("/roles")
    public ResponseEntity<ApiResponse> getAllRealmRoles() {
        return ResponseBuilder.build(HttpStatus.OK, "Realm roles fetched",
                adminUserService.getAllRealmRoles());
    }


    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable String userId) {
        return ResponseBuilder.build(HttpStatus.OK, "User fetched",
                adminUserService.getUserById(userId));
    }


    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        String newUserId = adminUserService.createUser(request);
        return ResponseBuilder.build(HttpStatus.CREATED, "User created successfully",
                Map.of("id", newUserId));
    }


    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request) {

        adminUserService.updateUser(userId, request);
        return ResponseBuilder.build(HttpStatus.OK, "User updated successfully", null);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
        adminUserService.deleteUser(userId);
        return ResponseBuilder.build(HttpStatus.OK, "User deleted successfully", null);
    }


    @PutMapping("/{userId}/status")
    public ResponseEntity<ApiResponse> setUserEnabled(
            @PathVariable String userId,
            @RequestParam boolean enabled) {

        adminUserService.setUserEnabled(userId, enabled);
        return ResponseBuilder.build(HttpStatus.OK,
                "User " + (enabled ? "enabled" : "disabled") + " successfully", null);
    }


    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse> resetPassword(
            @PathVariable String userId,
            @RequestBody ResetPasswordRequest request) {

        adminUserService.resetPassword(userId, request);
        return ResponseBuilder.build(HttpStatus.OK, "Password reset successfully", null);
    }


    @GetMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse> getUserRoles(@PathVariable String userId) {
        return ResponseBuilder.build(HttpStatus.OK, "User roles fetched",
                adminUserService.getUserRoles(userId));
    }


    @PostMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse> assignRole(
            @PathVariable String userId,
            @PathVariable String roleName) {

        adminUserService.assignRole(userId, roleName);
        return ResponseBuilder.build(HttpStatus.OK,
                "Role '" + roleName + "' assigned to user", null);
    }


    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse> removeRole(
            @PathVariable String userId,
            @PathVariable String roleName) {

        adminUserService.removeRole(userId, roleName);
        return ResponseBuilder.build(HttpStatus.OK,
                "Role '" + roleName + "' removed from user", null);
    }
}
