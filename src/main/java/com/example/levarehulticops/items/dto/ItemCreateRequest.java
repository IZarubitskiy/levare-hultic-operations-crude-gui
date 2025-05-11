package com.example.levarehulticops.items.dto;

import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO for creating a new Item.
 * All fields except serialNumber, jobOrderId and comments are required.
 */
public record ItemCreateRequest(
        /** Part number reference (catalog entry) */
        @NotNull(message = "ItemInfo ID must not be null")
        String itemInfoPartNumber,

        /** Client-specific part number */
        @NotBlank(message = "Client part number must not be blank")
        String clientPartNumber,

        /** Serial number (optional) */
        String serialNumber,

        /** Ownership classification */
        @NotNull(message = "Ownership must not be null")
        Client ownership,

        /** Current condition of the item */
        @NotNull(message = "Item condition must not be null")
        ItemCondition itemCondition,

        /** Current status of the item */
        @NotNull(message = "Item status must not be null")
        ItemStatus itemStatus,

        /** Assigned JobOrder ID (optional) */
        Long jobOrderId,

        /** Free-form comments (optional) */
        String comments
) {}
