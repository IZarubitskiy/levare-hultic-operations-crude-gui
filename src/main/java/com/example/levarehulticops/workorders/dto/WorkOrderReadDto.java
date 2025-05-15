package com.example.levarehulticops.workorders.dto;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
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

        /** Stock items taken from inventory */
        List<ItemReadDto> stockItems,

        /** Items sent for repair */
        List<ItemReadDto> repairItems,

        /** Items newly requested (by catalog entry) */
        List<ItemInfoDto> newRequests,

        LocalDate requestDate,
        LocalDate deliveryDate,
        WorkOrderStatus status,
        EmployeeDto requestor,
        String comments,
        Long version
) {
}
