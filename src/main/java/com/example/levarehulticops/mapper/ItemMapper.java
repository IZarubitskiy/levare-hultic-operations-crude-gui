package com.example.levarehulticops.mapper;

import org.mapstruct.*;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.ItemCreateRequest;
import com.example.levarehulticops.dto.ItemUpdateRequest;

/**
 * Mapper for converting between Item entity and its DTOs.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    /**
     * Map Item entity to read-only DTO.
     */
    @Mapping(target = "itemInfoId", source = "itemInfo.partNumber")
    ItemReadDto toReadDto(Item entity);

    /**
     * Map create-request DTO to a new Item entity.
     * - id, jobOrder and version are ignored (managed by JPA/service)
     * - itemInfo will be set in service layer based on itemInfoId
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "jobOrder", ignore = true)
    @Mapping(target = "itemInfo", ignore = true)
    Item toEntity(ItemCreateRequest dto);

    /**
     * Map update-request DTO onto an existing Item entity.
     * - ignores null values
     * - jobOrder and itemInfo are handled in service
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "jobOrder", ignore = true)
    @Mapping(target = "itemInfo", ignore = true)
    void updateEntityFromDto(ItemUpdateRequest dto, @MappingTarget Item entity);
}
