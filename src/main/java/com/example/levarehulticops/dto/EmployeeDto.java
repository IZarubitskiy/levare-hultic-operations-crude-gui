package com.example.levarehulticops.dto;

/**
 * DTO for representing an employee in WorkOrder and JobOrder contexts.
 */
public record EmployeeDto(
        /** Unique identifier of the employee */
        Long id,
        /** Full name of the employee */
        String name
) {}