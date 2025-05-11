package com.example.levarehulticops.service;

import com.example.levarehulticops.dto.JobOrderReadDto;
import com.example.levarehulticops.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobOrderService {
    JobOrder create(JobOrder jobOrder);
    JobOrderReadDto updateJobOrder(Long id, JobOrderUpdateRequest dto) );
    void delete(Long id);
    JobOrder getById(Long id);
    Page<JobOrder> getAll(Pageable pageable);
    Page<JobOrder> getByStatus(JobOrderStatus status, Pageable pageable);
}
