// src/main/java/com/example/levarehulticops/controller/mvc/ItemMvcController.java
package com.example.levarehulticops.controller.mvc;

import com.example.levarehulticops.dto.ItemCreateRequest;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.ItemUpdateRequest;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.entity.enums.ItemStatus;
import com.example.levarehulticops.entity.enums.ItemType;
import com.example.levarehulticops.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemMvcController {

    private final ItemService itemService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createDto", new ItemCreateRequest(
                "", "", "", null, null, null, null, null, ""));
        model.addAttribute("clients", Client.values());
        model.addAttribute("types", ItemType.values());
        model.addAttribute("conditions", ItemCondition.values());
        model.addAttribute("statuses", ItemStatus.values());
        return "items/create";
    }

    @PostMapping
    public String create(@ModelAttribute("createDto") ItemCreateRequest dto) {
        itemService.createItem(dto);
        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        ItemReadDto dto = itemService.getById(id);  // если getById возвращает DTO
        model.addAttribute("item", dto);
        return "items/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ItemReadDto dto = itemService.getById(id);
        model.addAttribute("updateDto", new ItemUpdateRequest(
                dto.clientPartNumber(),
                dto.serialNumber(),
                dto.ownership(),
                dto.itemType(),
                dto.itemCondition(),
                dto.itemStatus(),
                null,
                dto.comments()
        ));
        model.addAttribute("itemId", id);
        model.addAttribute("clients", Client.values());
        model.addAttribute("types", ItemType.values());
        model.addAttribute("conditions", ItemCondition.values());
        model.addAttribute("statuses", ItemStatus.values());
        return "items/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("updateDto") ItemUpdateRequest dto
    ) {
        itemService.update(id, dto);
        return "redirect:/items/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/items";
    }
}
