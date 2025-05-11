package com.example.levarehulticops.service.impl;

import javax.persistence.OptimisticLockException;
import javax.persistence.EntityNotFoundException;

import com.example.levarehulticops.dto.JobOrderCreateRequest;
import com.example.levarehulticops.dto.JobOrderReadDto;
import com.example.levarehulticops.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.dto.JobOrderStatusChangeDto;
import com.example.levarehulticops.entity.Employee;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.WorkOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import com.example.levarehulticops.mapper.JobOrderMapper;
import com.example.levarehulticops.repository.EmployeeRepository;
import com.example.levarehulticops.repository.ItemRepository;
import com.example.levarehulticops.repository.JobOrderRepository;
import com.example.levarehulticops.repository.WorkOrderRepository;
import com.example.levarehulticops.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class JobOrderServiceImpl implements JobOrderService {
    private final JobOrderRepository jobOrderRepository;
    private final WorkOrderRepository workOrderRepository;
    private final ItemRepository itemRepository;
    private final EmployeeRepository employeeRepository;
    private final JobOrderMapper mapper;

    @Override
    public JobOrderReadDto create(JobOrderCreateRequest dto) {
        JobOrder jo = mapper.toEntity(dto);
        jo.setStatus(JobOrderStatus.CREATED);
        JobOrder saved = jobOrderRepository.save(jo);
        return mapper.toReadDto(saved);
    }

    @Override
    public JobOrderReadDto update(Long id, JobOrderUpdateRequest dto) {
        JobOrder jo = jobOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found, id=" + id));

        if (!jo.getVersion().equals(dto.version())) {
            throw new OptimisticLockException("JobOrder was modified concurrently");
        }

        mapper.updateEntityFromDto(dto, jo);
        JobOrder updated = jobOrderRepository.save(jo);
        return mapper.toReadDto(updated);
    }

    @Override
    public JobOrderReadDto changeStatus(Long id, JobOrderStatusChangeDto dto) {
        JobOrder jo = jobOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found, id=" + id));

        if (!jo.getVersion().equals(dto.version())) {
            throw new OptimisticLockException("JobOrder was modified concurrently");
        }

        jo.setStatus(dto.status());
        JobOrder updated = jobOrderRepository.save(jo);
        return mapper.toReadDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!jobOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("JobOrder not found: " + id);
        }
        jobOrderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public JobOrderReadDto getById(Long id) {
        JobOrder jo = jobOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("JobOrder not found: " + id));
        return mapper.toReadDto(jo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobOrderReadDto> getAll(Pageable pageable) {
        return jobOrderRepository.findAll(pageable)
                .map(mapper::toReadDto);
    }

    @Override
    public Page<JobOrderReadDto> getByStatus(JobOrderStatus status, Pageable pageable) {
        return jobOrderRepository.findByStatus(status, pageable)
                .map(mapper::toReadDto);
    }
}