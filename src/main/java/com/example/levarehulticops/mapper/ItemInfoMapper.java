package com.example.levarehulticops.mapper;

import org.mapstruct.*;
import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.dto.ItemInfoDto;

/**
 * Mapper for converting between ItemInfo entity and its DTO.
 */
@Mapper(componentModel = "spring")
public interface ItemInfoMapper {

    /**
     * Map ItemInfo entity to DTO.
     */
    ItemInfoDto toDto(ItemInfo entity);
}
