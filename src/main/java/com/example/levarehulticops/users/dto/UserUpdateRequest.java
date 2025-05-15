package com.example.levarehulticops.users.dto;

import com.example.levarehulticops.users.entity.AccessLevel;

import javax.validation.constraints.NotNull;

/**
 * DTO for updating an existing User.
 * Only provided fields will be modified; ID is required.
 */
public record UserUpdateRequest(
        /** User ID */
        @NotNull(message = "User ID must not be null")
        Long id,

        /** New full name (optional) */
        String name,

        /** New business position (optional) */
        String position,

        /** New username (optional) */
        String username,

        /** New plain-text password (optional) */
        String password,

        /** New system access level (optional) */
        AccessLevel role
) {
}
