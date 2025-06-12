// src/main/java/com/example/levarehulticops/controller/WorkOrderRestController.java
package com.example.levarehulticops.workorders.controller;

import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;
import com.example.levarehulticops.workorders.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workorders")
@RequiredArgsConstructor
public class WorkOrderRestController {
    private final WorkOrderService workOrderService;

    @GetMapping
    public Page<WorkOrder> list(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size) {
        return workOrderService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public WorkOrder get(@PathVariable Long id) {
        return workOrderService.getById(id);
    }


    @GetMapping("/create-job-order")
    public Page<WorkOrderReadDto> listForJob(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // здесь фильтруем по нужному статусу, например READY_FOR_JOB
        return workOrderService.getByStatus(
                WorkOrderStatus.IN_PROGRESS,
                PageRequest.of(page, size)
        );
    }

    @PutMapping("/{id}")
    public WorkOrder update(@PathVariable Long id,
                            @RequestBody WorkOrder workOrder) {
        workOrder.setId(id);
        return workOrderService.update(workOrder);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        workOrderService.delete(id);
    }
}
