// src/main/java/com/example/levarehulticops/controller/ExecutionLogController.java
package com.example.levarehulticops.executionlogs.controller;

import com.example.levarehulticops.executionlogs.entity.ExecutionLog;
import com.example.levarehulticops.executionlogs.service.ExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/execution-logs")
@RequiredArgsConstructor
public class ExecutionLogController {
    private final ExecutionLogService service;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<ExecutionLog> pg = service.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "execution-logs/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("executionLog", new ExecutionLog());
        return "execution-logs/create";
    }

    @PostMapping
    public String create(@ModelAttribute ExecutionLog executionLog) {
        service.create(executionLog);
        return "redirect:/execution-logs";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("executionLog", service.getById(id));
        return "execution-logs/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("executionLog", service.getById(id));
        return "execution-logs/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute ExecutionLog executionLog) {
        executionLog.setId(id);
        service.update(executionLog);
        return "redirect:/execution-logs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/execution-logs";
    }
}
