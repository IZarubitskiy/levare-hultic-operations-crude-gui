package com.example.levarehulticops.dto;

import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.entity.enums.ItemStatus;
import com.example.levarehulticops.entity.enums.ItemType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

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

        /** Type of the item */
        @NotNull(message = "Item type must not be null")
        ItemType itemType,

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
