package com.example.levarehulticops.items.mapper;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.mapper.ItemInfoMapper;
import org.springframework.stereotype.Component;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;

/**
 * Manual mapper for Item ↔ DTOs.
 */
@Component
public class ItemMapper {

    private final ItemInfoMapper itemInfoMapper;

    public ItemMapper(ItemInfoMapper itemInfoMapper) {
        this.itemInfoMapper = itemInfoMapper;
    }
    /**
     * Convert Item entity to read-only DTO.
     */
    public ItemReadDto toReadDto(Item item) {
        // map ItemInfo entity → ItemInfoDto
        ItemInfoDto infoDto = itemInfoMapper.toReadDto(item.getItemInfo());
        return new ItemReadDto(
                item.getId(),
                infoDto,
                item.getClientPartNumber(),
                item.getSerialNumber(),
                item.getOwnership(),
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