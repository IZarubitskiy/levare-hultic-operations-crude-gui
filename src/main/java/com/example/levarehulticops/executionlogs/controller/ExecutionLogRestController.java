// src/main/java/com/example/levarehulticops/controller/ExecutionLogRestController.java
package com.example.levarehulticops.executionlogs.controller;

import com.example.levarehulticops.executionlogs.entity.ExecutionLog;
import com.example.levarehulticops.executionlogs.service.ExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execution-logs")
@RequiredArgsConstructor
public class ExecutionLogRestController {
    private final ExecutionLogService service;

    @GetMapping
    public Page<ExecutionLog> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        return service.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ExecutionLog get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExecutionLog create(@RequestBody ExecutionLog executionLog) {
        return service.create(executionLog);
    }

    @PutMapping("/{id}")
    public ExecutionLog update(@PathVariable Long id, @RequestBody ExecutionLog executionLog) {
        executionLog.setId(id);
        return service.update(executionLog);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
