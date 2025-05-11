package com.example.levarehulticops.workdays.repository;

import com.example.levarehulticops.workdays.entity.WorkDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkDayRepository extends JpaRepository<WorkDay, Long> {
}
