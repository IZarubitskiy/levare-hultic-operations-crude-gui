// src/main/java/com/example/levarehulticops/controller/AttendanceController.java
package com.example.levarehulticops.attendances.controller;

import com.example.levarehulticops.attendances.entity.Attendance;
import com.example.levarehulticops.attendances.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<Attendance> pg = service.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "attendance/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        return "attendance/create";
    }

    @PostMapping
    public String create(@ModelAttribute Attendance attendance) {
        service.create(attendance);
        return "redirect:/attendance";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("attendance", service.getById(id));
        return "attendance/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("attendance", service.getById(id));
        return "attendance/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Attendance attendance) {
        attendance.setId(id);
        service.update(attendance);
        return "redirect:/attendance";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/attendance";
    }
}
