// src/main/java/com/example/levarehulticops/controller/WorkDayController.java
package com.example.levarehulticops.workdays.controller;

import com.example.levarehulticops.workdays.entity.WorkDay;
import com.example.levarehulticops.workdays.service.WorkDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/workdays")
@RequiredArgsConstructor
public class WorkDayController {
    private final WorkDayService service;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<WorkDay> pg = service.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "workdays/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("workDay", new WorkDay());
        return "workdays/create";
    }

    @PostMapping
    public String create(@ModelAttribute WorkDay workDay) {
        service.create(workDay);
        return "redirect:/workdays";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("workDay", service.getById(id));
        return "workdays/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("workDay", service.getById(id));
        return "workdays/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute WorkDay workDay) {
        workDay.setId(id);
        service.update(workDay);
        return "redirect:/workdays";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/workdays";
    }
}
