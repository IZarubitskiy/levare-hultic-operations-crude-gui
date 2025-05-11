package com.example.levarehulticops.employees.repository;

import com.example.levarehulticops.employees.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
