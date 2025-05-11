package com.example.levarehulticops.dto;

import com.example.levarehulticops.entity.enums.Client;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating a new WorkOrder.
 * The status will be set to CREATED on the server side,
 * and the requestor will be determined from the authenticated user.
 */
public record WorkOrderCreateRequest(
        @NotBlank(message = "Work order number must not be blank")
        String workOrderNumber,

        @NotNull(message = "Client must not be null")
        Client client,

        @NotBlank(message = "Well must not be blank")
        String well,

        /** Optional list of stock item IDs */
        List<Long> stockItemIds,

        /** Optional list of repair item IDs */
        List<Long> repairItemIds,

        /** Optional list of new request itemInfo IDs */
        List<Long> newRequestIds,

        @NotNull(message = "Request date must not be null")
        LocalDate requestDate,

        @NotNull(message = "Delivery date must not be null")
        LocalDate deliveryDate,

        /** Optional comments */
        String comments
) {}
