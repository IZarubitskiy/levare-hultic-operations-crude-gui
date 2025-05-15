package com.example.levarehulticops.employees.service;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.employees.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Employee create(Employee employee);

    Employee update(Employee employee);

    void delete(Long id);

    Employee getById(Long id);

    Page<EmployeeDto> getAll(Pageable pageable);
}
