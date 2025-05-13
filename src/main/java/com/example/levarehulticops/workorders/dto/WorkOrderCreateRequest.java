package com.example.levarehulticops.workorders.dto;

import com.example.levarehulticops.workorders.entity.Client;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * DTO for creating a new WorkOrder.
 * Status is set to CREATED on the server side,
 * requestor is derived from the authenticated user.
 */
public record WorkOrderCreateRequest(

        @NotBlank(message = "Work order number must not be blank")
        String workOrderNumber,

        @NotNull(message = "Client ID must not be null")
        Client client,

        @NotBlank(message = "Well must not be blank")
        String well,

        /** Optional list of existing stock item IDs */
        List<Long> stockItemIds,

        /** Optional list of existing repair item IDs */
        List<Long> repairItemIds,

        /** Optional list of new item info request IDs */
        List<Long> newRequestItemInfoIds,

        @NotNull(message = "Delivery date must not be null")
        LocalDate deliveryDate,

        /** Optional comments */
        String comments

) {

        /**
         * Ensure that deliveryDate is today or in the future.
         */
        @AssertTrue(message = "Delivery date must be today or in the future")
        public boolean isDeliveryDateValid() {
                if (deliveryDate == null) {
                        return true;  // @NotNull will catch missing date
                }
                LocalDate today = LocalDate.now(ZoneId.of("Africa/Cairo"));
                return !deliveryDate.isBefore(today);
        }
}
