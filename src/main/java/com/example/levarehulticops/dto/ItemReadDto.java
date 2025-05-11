package com.example.levarehulticops.dto;

import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.entity.enums.ItemStatus;
import com.example.levarehulticops.entity.enums.ItemType;

/**
 * DTO for reading Item details.
 */
public record ItemReadDto(
        /** Item ID */
        Long id,

        /** Reference to catalog entry (ItemInfo ID) */
        Long itemInfoId,

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

        /** Current JobOrder ID of the item */
        Long jobOrderId,

        /** Free-form comments */
        String comments

) {}