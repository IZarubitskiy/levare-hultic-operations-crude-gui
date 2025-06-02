package com.example.levarehulticops.workorders.controller;

import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.service.WorkOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/workorders")
@RequiredArgsConstructor
public class WorkOrderMvcController {
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
    public String showCreateForm(
            Model model,
            @RequestParam(value = "currentUserId", required = false, defaultValue = "0") Long currentUserId
    ) {
        model.addAttribute("workOrder", new WorkOrderCreateRequest(
                "",    // workOrderNumber
                null,  // client
                "",    // well
                null,  // ItemIds
                null,  // newRequestItemInfoIds
                null,  // deliveryDate
                ""     // comments
        ));
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("today", LocalDate.now().toString());
        return "workorders/create";
    }

    @PostMapping
    public String createWorkOrder(
            @Valid @ModelAttribute("workOrder") WorkOrderCreateRequest dto,
            Principal principal        // Spring Security: текущий пользователь
    ) {
        // внутри сервиса выставится requestorId из principal,
        // и статус автоматически будет CREATED
        workOrderService.create(dto, principal.getName());
        return "redirect:/workorders"; // редирект на список или страницу детали
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
