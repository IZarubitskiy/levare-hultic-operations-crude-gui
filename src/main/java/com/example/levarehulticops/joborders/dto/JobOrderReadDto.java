package com.example.levarehulticops.joborders.dto;

import com.example.levarehulticops.users.dto.UserDto;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;

/**
 * DTO for reading a JobOrder for edit/view.
 * Includes reference to parent WorkOrder.
 */
public record JobOrderReadDto(
        /** JobOrder ID */
        Long id,

        /** ID of the parent WorkOrder */
        Long workOrderId,

        /** Equipment item details */
        ItemReadDto item,

        /** Current status of the job order */
        JobOrderStatus status,

        /** User responsible for execution */
        UserDto responsibleEmployee,

        /** Any comments */
        String comments,

        /** Version for optimistic locking */
        Long version
) {
}
