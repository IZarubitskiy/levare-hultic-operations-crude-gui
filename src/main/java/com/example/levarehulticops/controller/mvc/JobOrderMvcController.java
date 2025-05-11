// src/main/java/com/example/levarehulticops/controller/mvc/JobOrderMvcController.java
package com.example.levarehulticops.controller.mvc;

import com.example.levarehulticops.dto.*;
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
public class JobOrderMvcController {

    private final JobOrderService jobOrderService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<JobOrderReadDto> pg = jobOrderService.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "joborders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // Prepare empty DTO for binding
        model.addAttribute("createDto", new JobOrderCreateRequest(null, null, null, null));
        return "joborders/create";
    }

    @PostMapping
    public String create(@ModelAttribute("createDto") JobOrderCreateRequest dto) {
        jobOrderService.create(dto);
        return "redirect:/joborders";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        JobOrderReadDto dto = jobOrderService.getById(id);
        model.addAttribute("jobOrder", dto);
        // Prepare status-change DTO initialized with current values
        model.addAttribute("statusDto",
                new JobOrderStatusChangeDto(dto.status(), dto.version()));
        return "joborders/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        JobOrderReadDto dto = jobOrderService.getById(id);
        // Prepopulate workOrderId and itemId only; responsibleEmployeeId comes from header
        JobOrderUpdateRequest updateDto = new JobOrderUpdateRequest(
                dto.workOrderId(),   // existing parent WO
                dto.item().id(),     // existing item ID
                dto.responsibleEmployee().id(),                // leave responsibleEmployeeId null
                dto.comments(),
                dto.version()
        );
        model.addAttribute("updateDto", updateDto);
        model.addAttribute("jobOrderId", id);
        return "joborders/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("updateDto") JobOrderUpdateRequest dto) {
        jobOrderService.update(id, dto);
        return "redirect:/joborders";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @ModelAttribute("statusDto") JobOrderStatusChangeDto dto) {
        jobOrderService.changeStatus(id, dto);
        return "redirect:/joborders/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        jobOrderService.delete(id);
        return "redirect:/joborders";
    }

    @GetMapping("/in-progress")
    public String inProgress(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             Model model) {
        Page<JobOrderReadDto> pg = jobOrderService.getByStatus(
                JobOrderStatus.IN_PROGRESS,
                PageRequest.of(page, size)
        );
        model.addAttribute("page", pg);
        return "joborders/in-progress";
    }
}
