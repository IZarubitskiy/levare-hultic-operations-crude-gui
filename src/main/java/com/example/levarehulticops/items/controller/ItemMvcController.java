package com.example.levarehulticops.items.controller;

import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.iteminfos.entity.ItemType;
import com.example.levarehulticops.items.service.ItemService;
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

    /**
     * Stock Egypt page — shows items NEW or REPAIRED, ON_STOCK,
     * ownership not RETS or CORP.
     */
    @GetMapping("/stock_egypt")
    public String stockEgypt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getStockEgyptItems(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/stock_egypt";
    }

    /**
     * RNE page — показывает все Item со статусом USED и клиентом RETS.
     */
    @GetMapping("/rne")
    public String rneItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getRneItems(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/rne";
    }

    /**
     * Corporate page — показывает все Item со статусом USED и клиентом CORP.
     */
    @GetMapping("/rne_corporate")
    public String rneCorporateItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getRneCorporateItems(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/rne_corporate";
    }

    /**
     * Outstanding page — показывает все Item со статусом USED, но не с клиентами CORP или RETS.
     */
    @GetMapping("/outstanding")
    public String outstandingItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getOutstandingItems(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/outstanding";
    }

    @GetMapping("/stock_corporate")
    public String stockCorporate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<ItemReadDto> pg = itemService.getStockCorporateItems(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/stock_corporate";
    }

    // ==================== Стандартные CRUD-методы ====================

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("createDto", new ItemCreateRequest("", "", "", null, null, null, null, ""));
        model.addAttribute("clients", Client.values());
        model.addAttribute("types", ItemType.values());
        model.addAttribute("conditions", ItemCondition.values());
        model.addAttribute("statuses", ItemStatus.values());
        return "items/create";
    }

    @PostMapping
    public String create(@ModelAttribute("createDto") ItemCreateRequest dto) {
        itemService.createItem(dto);
        return "redirect:/items/stock";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        ItemReadDto dto = itemService.getById(id);
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
    public String update(@PathVariable Long id, @ModelAttribute("updateDto") ItemUpdateRequest dto) {
        itemService.update(id, dto);
        return "redirect:/items/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/items/stock";
    }
}
