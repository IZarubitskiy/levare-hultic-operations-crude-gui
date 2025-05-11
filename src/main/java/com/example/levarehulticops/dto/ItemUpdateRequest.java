package com.example.levarehulticops.dto;

import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.entity.enums.ItemStatus;
import com.example.levarehulticops.entity.enums.ItemType;
import javax.validation.constraints.NotNull;

/**
 * DTO for updating an existing Item.
 * Only the provided fields will be modified; version is required for optimistic locking.
 */
public record ItemUpdateRequest(
        /** Client-specific part number */
        String clientPartNumber,

        /** Serial number */
        String serialNumber,

        /** Ownership classification */
        Client ownership,

        /** Type of the item */
        ItemType itemType,

        /** Current condition of the item */
        ItemCondition itemCondition,

        /** Current status of the item */
        ItemStatus itemStatus,

        /** Assigned JobOrder ID (optional) */
        Long jobOrderId,

        /** Free-form comments */
        String comments

) {}
