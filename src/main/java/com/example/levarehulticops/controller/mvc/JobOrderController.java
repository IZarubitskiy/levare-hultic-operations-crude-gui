// src/main/java/com/example/levarehulticops/controller/JobOrderController.java
package com.example.levarehulticops.controller.mvc;

import com.example.levarehulticops.entity.JobOrder;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import com.example.levarehulticops.service.JobOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/joborders")
@RequiredArgsConstructor
public class JobOrderController {
    private final JobOrderService jobOrderService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Page<JobOrder> pg = jobOrderService.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "joborders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("jobOrder", new JobOrder());
        return "joborders/create";
    }

    @PostMapping
    public String create(@ModelAttribute JobOrder jobOrder) {
        jobOrderService.create(jobOrder);
        return "redirect:/joborders";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("jobOrder", jobOrderService.getById(id));
        return "joborders/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("jobOrder", jobOrderService.getById(id));
        return "joborders/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute JobOrder jobOrder) {
        jobOrder.setId(id);
        jobOrderService.update(jobOrder);
        return "redirect:/joborders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        jobOrderService.delete(id);
        return "redirect:/joborders";
    }
    /**
     * Страница со всеми JobOrder в статусе IN_PROGRESS
     */
    @GetMapping("/in-progress")
    public String inProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        // Получаем только IN_PROGRESS
        Page<JobOrder> pg = jobOrderService.getByStatus(
                JobOrderStatus.IN_PROGRESS,
                PageRequest.of(page, size)
        );
        model.addAttribute("page", pg);
        return "joborders/in-progress";
    }

}
