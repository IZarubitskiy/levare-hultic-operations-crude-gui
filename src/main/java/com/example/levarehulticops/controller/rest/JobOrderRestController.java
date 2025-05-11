// src/main/java/com/example/levarehulticops/controller/rest/JobOrderRestController.java
package com.example.levarehulticops.controller.rest;

import com.example.levarehulticops.dto.*;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import com.example.levarehulticops.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/joborders")
@RequiredArgsConstructor
public class JobOrderRestController {

    private final JobOrderService jobOrderService;

    @GetMapping
    public Page<JobOrderReadDto> list(Pageable pageable) {
        return jobOrderService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public JobOrderReadDto getById(@PathVariable Long id) {
        return jobOrderService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobOrderReadDto create(@RequestBody JobOrderCreateRequest dto) {
        return jobOrderService.create(dto);
    }

    @PutMapping("/{id}")
    public JobOrderReadDto update(@PathVariable Long id,
                                  @RequestBody JobOrderUpdateRequest dto) {
        return jobOrderService.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public JobOrderReadDto changeStatus(@PathVariable Long id,
                                        @RequestBody JobOrderStatusChangeDto dto) {
        return jobOrderService.changeStatus(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        jobOrderService.delete(id);
    }

    @GetMapping("/status/{status}")
    public Page<JobOrderReadDto> byStatus(@PathVariable JobOrderStatus status,
                                          Pageable pageable) {
        return jobOrderService.getByStatus(status, pageable);
    }
}
