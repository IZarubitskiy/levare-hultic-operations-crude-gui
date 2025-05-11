package com.example.levarehulticops.joborders.dto;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;
import com.example.levarehulticops.items.dto.ItemReadDto;

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

        /** Employee responsible for execution */
        EmployeeDto responsibleEmployee,

        /** Any comments */
        String comments,

        /** Version for optimistic locking */
        Long version
) {}
