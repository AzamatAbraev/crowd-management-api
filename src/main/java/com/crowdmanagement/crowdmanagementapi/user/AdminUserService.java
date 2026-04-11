package com.crowdmanagement.crowdmanagementapi.user;

import com.crowdmanagement.crowdmanagementapi.user.dto.*;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final RealmResource realmResource;

    public AdminUserService(RealmResource realmResource) {
        this.realmResource = realmResource;
    }

    public List<UserRepresentationDto> getAllUsers() {
        return realmResource.users().list(0, 1000)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserRepresentationDto> searchUsers(String query) {
        return realmResource.users().search(query, 0, 200)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserRepresentationDto getUserById(String userId) {
        try {
            UserRepresentation user = realmResource.users().get(userId).toRepresentation();
            return toDto(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found with id: " + userId);
        }
    }

    public String createUser(CreateUserRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.username());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setEnabled(true);

        if (request.password() != null && !request.password().isBlank()) {
            CredentialRepresentation credential = buildCredential(
                    request.password(), request.temporaryPassword());
            user.setCredentials(Collections.singletonList(credential));
        }

        try (Response response = realmResource.users().create(user)) {
            if (response.getStatus() == 201) {
                String location = response.getHeaderString("Location");
                return location.replaceAll(".*/", "");
            } else if (response.getStatus() == 409) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Username or email already exists");
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to create user, Keycloak status: " + response.getStatus());
            }
        }
    }

    public void updateUser(String userId, UpdateUserRequest request) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();

        if (request.firstName() != null)
            user.setFirstName(request.firstName());
        if (request.lastName() != null)
            user.setLastName(request.lastName());
        if (request.email() != null)
            user.setEmail(request.email());

        userResource.update(user);
    }

    public void setUserEnabled(String userId, boolean enabled) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(enabled);
        userResource.update(user);
    }

    public void resetPassword(String userId, ResetPasswordRequest request) {
        CredentialRepresentation credential = buildCredential(
                request.newPassword(), request.temporary());
        getUserResource(userId).resetPassword(credential);
    }

    public void deleteUser(String userId) {
        try (Response response = realmResource.users().delete(userId)) {
            if (response.getStatus() != 204) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Failed to delete user, Keycloak status: " + response.getStatus());
            }
        }
    }

    public List<String> getUserRoles(String userId) {
        return getUserResource(userId)
                .roles()
                .realmLevel()
                .listEffective()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    public void assignRole(String userId, String roleName) {
        RoleRepresentation role = getRealmRole(roleName);
        getUserResource(userId)
                .roles()
                .realmLevel()
                .add(Collections.singletonList(role));
    }

    public void removeRole(String userId, String roleName) {
        RoleRepresentation role = getRealmRole(roleName);
        getUserResource(userId)
                .roles()
                .realmLevel()
                .remove(Collections.singletonList(role));
    }

    public List<String> getAllRealmRoles() {
        return realmResource.roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    private UserResource getUserResource(String userId) {
        try {
            return realmResource.users().get(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found with id: " + userId);
        }
    }

    private RoleRepresentation getRealmRole(String roleName) {
        try {
            return realmResource.roles().get(roleName).toRepresentation();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Role not found: " + roleName);
        }
    }

    private CredentialRepresentation buildCredential(String password, boolean temporary) {
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(temporary);
        return cred;
    }

    private UserRepresentationDto toDto(UserRepresentation u) {
        List<String> roles;
        try {
            roles = realmResource.users().get(u.getId())
                    .roles().realmLevel().listEffective()
                    .stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            roles = Collections.emptyList();
        }

        return new UserRepresentationDto(
                u.getId(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                Boolean.TRUE.equals(u.isEnabled()),
                roles);
    }
}
