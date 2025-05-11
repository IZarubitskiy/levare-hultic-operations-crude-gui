package com.example.levarehulticops.attendances.repository;

import com.example.levarehulticops.attendances.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
