package com.example.levarehulticops.employees.dto;

/**
 * DTO for representing an employee in WorkOrder and JobOrder contexts.
 */
public record EmployeeDto(
        /** Unique identifier of the employee */
        Long id,
        /** Full name of the employee */
        String name,
        /** Position of the employee */
        String position
) {
}