package com.example.levarehulticops.joborders.dto;

import com.example.levarehulticops.joborders.entity.JobOrderStatus;

import javax.validation.constraints.NotNull;

/**
 * DTO for changing the status of a JobOrder.
 */
public record JobOrderStatusChangeDto(
        /** New status for the job order */
        @NotNull(message = "Job order status must not be null")
        JobOrderStatus status,

        /** Current version for optimistic locking */
        @NotNull(message = "Version must not be null")
        Long version
) {
}
