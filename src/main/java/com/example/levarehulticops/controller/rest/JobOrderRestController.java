package com.example.levarehulticops.controller.rest;

import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.levarehulticops.entity.enums.JobOrderStatus;

@RestController
@RequestMapping("/api/joborders")
@RequiredArgsConstructor
public class JobOrderRestController {
    private final JobOrderService jobOrderService;

    @GetMapping
    public Page<JobOrder> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return jobOrderService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public JobOrder get(@PathVariable Long id) {
        return jobOrderService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobOrder create(@RequestBody JobOrder jobOrder) {
        return jobOrderService.create(jobOrder);
    }

    @PutMapping("/{id}")
    public JobOrder update(
            @PathVariable Long id,
            @RequestBody JobOrder jobOrder) {
        jobOrder.setId(id);
        return jobOrderService.update(jobOrder);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        jobOrderService.delete(id);
    }
    @GetMapping("/in-progress")
    public Page<JobOrder> inProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return jobOrderService.getByStatus(JobOrderStatus.IN_PROGRESS, pageable);
    }

}
