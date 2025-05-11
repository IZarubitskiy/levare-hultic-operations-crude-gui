// src/main/java/com/example/levarehulticops/controller/AttendanceRestController.java
package com.example.levarehulticops.attendances.controller;

import com.example.levarehulticops.attendances.entity.Attendance;
import com.example.levarehulticops.attendances.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceRestController {
    private final AttendanceService service;

    @GetMapping
    public Page<Attendance> list(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        return service.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Attendance get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance create(@RequestBody Attendance attendance) {
        return service.create(attendance);
    }

    @PutMapping("/{id}")
    public Attendance update(@PathVariable Long id, @RequestBody Attendance attendance) {
        attendance.setId(id);
        return service.update(attendance);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
