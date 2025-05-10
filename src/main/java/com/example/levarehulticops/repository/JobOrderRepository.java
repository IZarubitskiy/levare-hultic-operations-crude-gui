package com.example.levarehulticops.repository;

import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOrderRepository extends JpaRepository<JobOrder, Long> {
    Page<JobOrder> findByStatus(JobOrderStatus status, Pageable pageable);
}
