package com.example.levarehulticops.joborders.repository;

import com.example.levarehulticops.joborders.entity.JobOrder;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOrderRepository extends JpaRepository<JobOrder, Long> {
    Page<JobOrder> findByStatus(JobOrderStatus status, Pageable pageable);
}
