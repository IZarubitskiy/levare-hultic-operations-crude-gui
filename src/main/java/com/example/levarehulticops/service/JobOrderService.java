package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobOrderService {
    JobOrder create(JobOrder jobOrder);
    JobOrder update(JobOrder jobOrder);
    void delete(Long id);
    JobOrder getById(Long id);
    Page<JobOrder> getAll(Pageable pageable);
    Page<JobOrder> getByStatus(JobOrderStatus status, Pageable pageable);
}
