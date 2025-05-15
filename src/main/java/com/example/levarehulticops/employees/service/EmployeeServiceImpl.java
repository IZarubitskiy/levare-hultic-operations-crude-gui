// src/main/java/com/example/levarehulticops/service/impl/EmployeeServiceImpl.java
package com.example.levarehulticops.employees.service;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.employees.mapper.EmployeeMapper;
import com.example.levarehulticops.employees.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repo;
    private final EmployeeMapper employeeMapper;

    @Override
    public Employee create(Employee employee) {
        return repo.save(employee);
    }

    @Override
    public Employee update(Employee employee) {
        if (!repo.existsById(employee.getId())) {
            throw new EntityNotFoundException("Employee not found: " + employee.getId());
        }
        return repo.save(employee);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDto> getAll(Pageable pageable) {
        return repo.findAll(pageable).map(employeeMapper::toDto);
    }
}
