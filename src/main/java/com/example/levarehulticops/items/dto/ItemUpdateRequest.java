package com.example.levarehulticops.items.dto;

import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;

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

        /** Current condition of the item */
        ItemCondition itemCondition,

        /** Current status of the item */
        ItemStatus itemStatus,

        /** Assigned JobOrder ID (optional) */
        Long jobOrderId,

        /** Free-form comments */
        String comments

) {}
