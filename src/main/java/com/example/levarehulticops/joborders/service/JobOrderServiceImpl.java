package com.example.levarehulticops.joborders.service;

import javax.persistence.OptimisticLockException;
import javax.persistence.EntityNotFoundException;

import com.example.levarehulticops.joborders.dto.JobOrderCreateRequest;
import com.example.levarehulticops.joborders.dto.JobOrderReadDto;
import com.example.levarehulticops.joborders.dto.JobOrderUpdateRequest;
import com.example.levarehulticops.joborders.dto.JobOrderStatusChangeDto;
import com.example.levarehulticops.joborders.entity.JobOrder;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;
import com.example.levarehulticops.joborders.mapper.JobOrderMapper;
import com.example.levarehulticops.employees.repository.EmployeeRepository;
import com.example.levarehulticops.items.repository.ItemRepository;
import com.example.levarehulticops.joborders.repository.JobOrderRepository;
import com.example.levarehulticops.workorders.repository.WorkOrderRepository;
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