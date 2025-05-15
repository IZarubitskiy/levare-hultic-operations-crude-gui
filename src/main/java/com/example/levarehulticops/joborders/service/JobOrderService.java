package com.example.levarehulticops.joborders.service;

import com.example.levarehulticops.joborders.dto.JobOrderCreateRequest;
import com.example.levarehulticops.joborders.dto.JobOrderReadDto;
import com.example.levarehulticops.joborders.dto.JobOrderStatusChangeDto;
import com.example.levarehulticops.joborders.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobOrderService {
    JobOrderReadDto create(JobOrderCreateRequest dto);

    JobOrderReadDto update(Long id, JobOrderUpdateRequest dto);

    JobOrderReadDto changeStatus(Long id, JobOrderStatusChangeDto dto);

    void delete(Long id);

    JobOrderReadDto getById(Long id);

    Page<JobOrderReadDto> getAll(Pageable pageable);

    Page<JobOrderReadDto> getByStatus(JobOrderStatus status, Pageable pageable);
}