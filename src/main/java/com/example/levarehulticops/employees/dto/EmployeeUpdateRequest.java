package com.example.levarehulticops.employees.dto;

import com.example.levarehulticops.employees.entity.AccessLevel;
import javax.validation.constraints.NotNull;

/**
 * DTO for updating an existing Employee.
 * Only provided fields will be modified; ID is required.
 */
public record EmployeeUpdateRequest(
        /** Employee ID */
        @NotNull(message = "Employee ID must not be null")
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
) {}
