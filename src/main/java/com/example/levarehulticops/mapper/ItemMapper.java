package com.example.levarehulticops.mapper;

import org.springframework.stereotype.Component;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.ItemCreateRequest;
import com.example.levarehulticops.dto.ItemUpdateRequest;
import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.entity.Employee;

/**
 * Manual mapper for Item ↔ DTOs.
 */
@Component
public class ItemMapper {

    /**
     * Convert Item entity to read-only DTO.
     */
    public ItemReadDto toReadDto(Item item) {
        return new ItemReadDto(
                item.getId(),
                item.getItemInfo().getId(),
                item.getClientPartNumber(),
                item.getSerialNumber(),
                item.getOwnership(),
                item.getItemType(),
                item.getItemCondition(),
                item.getItemStatus(),
                item.getJobOrder() != null ? item.getJobOrder().getId() : null,
                item.getComments()
        );
    }

    /**
     * Convert create-request DTO to a new Item entity.
     * Associations (itemInfo, jobOrder) must be set in service layer.
     */
    public Item toEntity(ItemCreateRequest dto) {
        Item item = new Item();
        item.setClientPartNumber(dto.clientPartNumber());
        item.setSerialNumber(dto.serialNumber());
        item.setOwnership(dto.ownership());
        item.setItemType(dto.itemType());
        item.setItemCondition(dto.itemCondition());
        item.setItemStatus(dto.itemStatus());
        item.setComments(dto.comments());
        // itemInfo and jobOrder are injected in service
        return item;
    }

    /**
     * Apply update-request DTO onto existing Item entity.
     * Only non-null fields are applied here; associations handled in service.
     */
    public void updateEntityFromDto(ItemUpdateRequest dto, Item item) {
        if (dto.clientPartNumber() != null) {
            item.setClientPartNumber(dto.clientPartNumber());
        }
        if (dto.serialNumber() != null) {
            item.setSerialNumber(dto.serialNumber());
        }
        if (dto.ownership() != null) {
            item.setOwnership(dto.ownership());
        }
        if (dto.itemType() != null) {
            item.setItemType(dto.itemType());
        }
        if (dto.itemCondition() != null) {
            item.setItemCondition(dto.itemCondition());
        }
        if (dto.itemStatus() != null) {
            item.setItemStatus(dto.itemStatus());
        }
        if (dto.comments() != null) {
            item.setComments(dto.comments());
        }
        // version check and save in service layer
    }
}