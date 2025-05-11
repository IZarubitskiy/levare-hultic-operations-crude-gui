package com.example.levarehulticops.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for updating an existing WorkOrder.
 * Only equipment lists, delivery date, comments and version may be modified.
 * All other fields are managed automatically on the server side.
 */
public record WorkOrderUpdateRequest(
        /** Updated list of stock item IDs */
        List<Long> stockItemIds,

        /** Updated list of repair item IDs */
        List<Long> repairItemIds,

        /** Updated list of new request itemInfo IDs */
        List<Long> newRequestIds,

        /** Updated delivery date */
        LocalDate deliveryDate,

        /** Updated comments */
        String comments,

        /** Current version for optimistic locking */
        @NotNull(message = "Version must not be null")
        Long version
) {}
