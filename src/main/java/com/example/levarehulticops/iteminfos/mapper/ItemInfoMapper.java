package com.example.levarehulticops.iteminfos.mapper;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between ItemInfo entity and its DTO.
 */
// ItemInfoMapper example:
@Component
public class ItemInfoMapper {
    /**
     * Convert ItemInfo entity to DTO
     */
    public ItemInfoDto toReadDto(ItemInfo info) {
        return new ItemInfoDto(
                info.getPartNumber(),
                info.getDescription(),
                info.getItemType()
        );
    }
}
