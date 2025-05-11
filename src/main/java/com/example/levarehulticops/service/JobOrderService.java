package com.example.levarehulticops.service;

import com.example.levarehulticops.dto.JobOrderCreateRequest;
import com.example.levarehulticops.dto.JobOrderReadDto;
import com.example.levarehulticops.dto.JobOrderStatusChangeDto;
import com.example.levarehulticops.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

public interface JobOrderService {
    JobOrderReadDto create(JobOrderCreateRequest dto);
    JobOrderReadDto update(Long id, JobOrderUpdateRequest dto);
    JobOrderReadDto changeStatus(Long id, JobOrderStatusChangeDto dto);
    void delete(Long id);
    JobOrderReadDto getById(Long id);
    Page<JobOrderReadDto> getAll(Pageable pageable);
    Page<JobOrderReadDto> getByStatus(JobOrderStatus status, Pageable pageable);
    }