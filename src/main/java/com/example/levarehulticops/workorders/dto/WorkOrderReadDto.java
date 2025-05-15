package com.example.levarehulticops.workorders.dto;

import com.example.levarehulticops.users.dto.UserDto;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for reading a WorkOrder for edit/view.
 * Includes full equipment details via nested DTOs.
 */
public record WorkOrderReadDto(
        Long id,
        String workOrderNumber,
        Client client,
        String well,
        List<ItemReadDto> items,
        LocalDate requestDate,
        LocalDate deliveryDate,
        WorkOrderStatus status,
        UserDto requestor,
        String comments,
        Long version
) {
}
