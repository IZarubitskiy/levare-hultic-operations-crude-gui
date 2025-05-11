// src/main/java/com/example/levarehulticops/controller/WorkDayRestController.java
package com.example.levarehulticops.workdays.controller;

import com.example.levarehulticops.workdays.entity.WorkDay;
import com.example.levarehulticops.workdays.service.WorkDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workdays")
@RequiredArgsConstructor
public class WorkDayRestController {
    private final WorkDayService service;

    @GetMapping
    public Page<WorkDay> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        return service.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public WorkDay get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkDay create(@RequestBody WorkDay workDay) {
        return service.create(workDay);
    }

    @PutMapping("/{id}")
    public WorkDay update(@PathVariable Long id, @RequestBody WorkDay workDay) {
        workDay.setId(id);
        return service.update(workDay);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
