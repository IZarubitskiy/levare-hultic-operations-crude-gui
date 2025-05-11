package com.example.levarehulticops.workordertemplates.repository;

import com.example.levarehulticops.workordertemplates.entity.WorkOrderTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderTemplateRepository extends JpaRepository<WorkOrderTemplate, Long> {
}
