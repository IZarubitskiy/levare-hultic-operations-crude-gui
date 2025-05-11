package com.example.levarehulticops.mapper;

import org.mapstruct.*;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.dto.*;

/**
 * Mapper for converting between JobOrder entity and its DTOs.
 */
@Mapper(
        componentModel = "spring",
        uses = {
                ItemMapper.class,          // maps Item ↔ ItemReadDto
                EmployeeMapper.class       // maps Employee ↔ EmployeeDto
        }
)
public interface JobOrderMapper {

    /**
     * Map JobOrder entity to JobOrderReadDto.
     */
    @Mapping(target = "workOrderId", source = "workOrder.id")
    JobOrderReadDto toReadDto(JobOrder entity);

    /**
     * Map JobOrderCreateRequest to a new JobOrder entity.
     * - id, version, status, workOrder, item and responsibleEmployee are managed elsewhere
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "responsibleEmployee", ignore = true)
    JobOrder toEntity(JobOrderCreateRequest dto);

    /**
     * Map JobOrderUpdateRequest onto an existing JobOrder entity.
     * - only comments and version check are applied here;
     * - workOrder, item, status and responsibleEmployee are handled in service
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "responsibleEmployee", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(JobOrderUpdateRequest dto, @MappingTarget JobOrder entity);

/**
 * Map JobOrderStatusChangeDto onto an existing JobOrder entity.
 * - only status is updated here; version check is handled in service
 */
