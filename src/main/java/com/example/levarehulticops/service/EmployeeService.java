package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee update(Employee employee);
    void delete(Long id);
    Employee getById(Long id);
    Page<Employee> getAll(Pageable pageable);
}
