package com.example.levarehulticops.workorders.controller;

import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/workorders")
@RequiredArgsConstructor
public class WorkOrderController {
    private final WorkOrderService workOrderService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<WorkOrder> pg = workOrderService.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "workorders/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("workOrder", new WorkOrder());
        return "workorders/create";
    }

    @PostMapping
    public String create(@ModelAttribute WorkOrder workOrder) {
        workOrderService.create(workOrder);
        return "redirect:/workorders";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("workOrder", workOrderService.getById(id));
        return "workorders/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("workOrder", workOrderService.getById(id));
        return "workorders/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute WorkOrder workOrder) {
        workOrder.setId(id);
        workOrderService.update(workOrder);
        return "redirect:/workorders";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return "redirect:/workorders";
    }
}
