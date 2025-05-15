package com.example.levarehulticops.workdays.service;

import com.example.levarehulticops.workdays.entity.WorkDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkDayService {
    WorkDay create(WorkDay workDay);

    WorkDay update(WorkDay workDay);

    void delete(Long id);

    WorkDay getById(Long id);

    Page<WorkDay> getAll(Pageable pageable);
}
