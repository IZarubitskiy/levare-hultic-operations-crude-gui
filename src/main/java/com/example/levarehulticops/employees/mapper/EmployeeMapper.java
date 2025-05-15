package com.example.levarehulticops.employees.mapper;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.employees.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    /**
     * Преобразует одну сущность в DTO
     */
    public EmployeeDto toDto(Employee e) {
        return new EmployeeDto(
                e.getId(),
                e.getName(),
                e.getPosition()
        );
    }
}
