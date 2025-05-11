package com.example.levarehulticops.dto;

import javax.validation.constraints.NotNull;

/**
 * DTO for creating a new JobOrder.
 * The initial status will be set on the server side.
 */
public record JobOrderCreateRequest(
        /** ID of the WorkOrder this JobOrder belongs to */
        @NotNull(message = "WorkOrder ID must not be null")
        Long workOrderId,

        /** ID of the equipment item this JobOrder applies to */
        @NotNull(message = "Item ID must not be null")
        Long itemId,

        /** ID of the employee responsible for this job */
        @NotNull(message = "Responsible employee ID must not be null")
        Long responsibleEmployeeId,

        /** Optional comments */
        String comments
) {}
