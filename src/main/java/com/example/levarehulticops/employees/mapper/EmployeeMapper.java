package com.example.levarehulticops.employees.mapper;

import org.mapstruct.*;
import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.employees.dto.EmployeeDto;

/**
 * Mapper for converting between Employee entity and its DTO.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /**
     * Map Employee entity to DTO.
     */
    EmployeeDto toDto(Employee entity);
}
