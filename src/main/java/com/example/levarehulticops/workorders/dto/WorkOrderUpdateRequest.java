package com.example.levarehulticops.workorders.dto;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for updating an existing WorkOrder.
 * Only equipment lists, delivery date, comments and version may be modified.
 * All other fields are managed automatically on the server side.
 */
public record WorkOrderUpdateRequest(

        /** Optional list of item IDs */
        List<Long> stockItemIds,

        /** Optional list of new item info request IDs */
        List<String> newItemsIds,

        /** Updated delivery date */
        LocalDate deliveryDate,

        /** Updated comments */
        String comments,

        /** Current version for optimistic locking */
        @NotNull(message = "Version must not be null")
        Long version
) {
}
