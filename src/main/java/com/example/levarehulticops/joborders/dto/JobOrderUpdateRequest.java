package com.example.levarehulticops.joborders.dto;

import javax.validation.constraints.NotNull;

/**
 * DTO for updating an existing JobOrder.
 * Only when status == CREATED, workOrderId and itemId may be modified.
 * Responsible employee and comments may always be modified.
 * Version is required for optimistic locking.
 */
public record JobOrderUpdateRequest(
        /** Optional new parent WorkOrder ID (only if status == CREATED) */
        Long workOrderId,

        /** Optional new equipment Item ID (only if status == CREATED) */
        Long itemId,

        /** New responsible employee ID */
        @NotNull(message = "Responsible employee ID must not be null")
        Long responsibleEmployeeId,

        /** Updated comments */
        String comments,

        /** Current version for optimistic locking */
        @NotNull(message = "Version must not be null")
        Long version
) {}
