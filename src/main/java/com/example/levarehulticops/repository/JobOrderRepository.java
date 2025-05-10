package com.example.levarehulticops.repository;

import com.example.levarehulticops.entity.JobOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOrderRepository extends JpaRepository<JobOrder, Long> {
}
