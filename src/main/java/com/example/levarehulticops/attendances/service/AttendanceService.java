// src/main/java/com/example/levarehulticops/service/AttendanceService.java
package com.example.levarehulticops.attendances.service;

import com.example.levarehulticops.attendances.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceService {
    Attendance create(Attendance attendance);

    Attendance update(Attendance attendance);

    void delete(Long id);

    Attendance getById(Long id);

    Page<Attendance> getAll(Pageable pageable);
}
