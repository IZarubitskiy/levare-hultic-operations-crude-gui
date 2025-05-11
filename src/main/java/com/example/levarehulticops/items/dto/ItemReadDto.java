package com.example.levarehulticops.items.dto;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;

/**
 * DTO for reading Item details.
 */
public record ItemReadDto(
        /** Item ID */
        Long id,

        /** Reference to catalog entry (ItemInfo ID) */
        ItemInfoDto itemInfo,

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

        /** Current JobOrder ID of the item */
        Long jobOrderId,

        /** Free-form comments */
        String comments

) {}