// src/main/java/com/example/levarehulticops/service/impl/AttendanceServiceImpl.java
package com.example.levarehulticops.attendances.service;

import com.example.levarehulticops.attendances.entity.Attendance;
import com.example.levarehulticops.attendances.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository repo;

    @Override
    public Attendance create(Attendance attendance) {
        return repo.save(attendance);
    }

    @Override
    public Attendance update(Attendance attendance) {
        if (!repo.existsById(attendance.getId())) {
            throw new EntityNotFoundException("Attendance not found: " + attendance.getId());
        }
        return repo.save(attendance);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Attendance not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
