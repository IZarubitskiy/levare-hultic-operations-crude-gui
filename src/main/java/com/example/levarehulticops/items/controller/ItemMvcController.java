package com.example.levarehulticops.items.controller;

import com.example.levarehulticops.iteminfos.entity.ItemType;
import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.service.ItemService;
import com.example.levarehulticops.workorders.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemMvcController {

    private final ItemService itemService;

    /**
     * Stock Egypt page — shows items:
     * Condition NEW or REPAIRED,
     * Status ON_STOCK or BOOKED,
     * Ownership not RETS or CORP.
     */

    @GetMapping("/stock_egypt")
    public String stockEgypt(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        // Собираем параметры фильтрации
        List<ItemCondition> conditions = List.of(ItemCondition.NEW, ItemCondition.REPAIRED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.STOCK_BOOKED, ItemStatus.STOCK_REQUEST);
        List<Client> ownerships = List.of(Client.RETS, Client.CORP);

        // Вызываем метод
        Page<ItemReadDto> pg = itemService.filterItemsExcludeClients(
                conditions,
                statuses,
                ownerships,
                PageRequest.of(page, size)
        );

        model.addAttribute("page", pg);
        return "items/stock_egypt";
    }

    /**
     * RNE page — shows items:
     * Condition USED,
     * Status ON_STOCK or REQUESTED_REPAIR,
     * Ownership RETS.
     */
    @GetMapping("/rne")
    public String rneItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        // Собираем параметры фильтрации
        List<ItemCondition> conditions = List.of(ItemCondition.USED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.REPAIR_REQUEST, ItemStatus.REPAIR_BOOKED);
        List<Client> ownerships = List.of(Client.RETS);

        // Вызываем метод
        Page<ItemReadDto> pg = itemService.filterItems(
                conditions,
                statuses,
                ownerships,
                PageRequest.of(page, size)
        );

        model.addAttribute("page", pg);
        return "items/rne";
    }

    /**
     * Corporate page — shows items:
     * Condition USED,
     * Status ON_STOCK or REQUESTED_REPAIR,
     * ownership CORP.
     */
    @GetMapping("/rne_corporate")
    public String rneCorporateItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        // Собираем параметры фильтрации
        List<ItemCondition> conditions = List.of(ItemCondition.USED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.REPAIR_REQUEST, ItemStatus.REPAIR_BOOKED);
        List<Client> ownerships = List.of(Client.CORP);

        // Вызываем метод
        Page<ItemReadDto> pg = itemService.filterItems(
                conditions,
                statuses,
                ownerships,
                PageRequest.of(page, size)
        );

        model.addAttribute("page", pg);
        return "items/rne_corporate";
    }

    /**
     * Outstanding page — shows items:
     * Condition USED,
     * Status ON_STOCK or REQUESTED_REPAIR,
     * Ownership not RETS or CORP.
     */
    @GetMapping("/outstanding")
    public String outstandingItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        // Собираем параметры фильтрации
        List<ItemCondition> conditions = List.of(ItemCondition.USED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.REPAIR_REQUEST, ItemStatus.REPAIR_BOOKED);
        List<Client> ownerships = List.of(Client.RETS, Client.CORP);

        // Вызываем метод
        Page<ItemReadDto> pg = itemService.filterItemsExcludeClients(
                conditions,
                statuses,
                ownerships,
                PageRequest.of(page, size)
        );

        model.addAttribute("page", pg);
        return "items/outstanding";
    }

    /**
     * Stock Corporate page — shows items:
     * Condition NEW or REPAIRED,
     * Status ON_STOCK or BOOKED,
     * Ownership  CORP.
     */
    @GetMapping("/stock_corporate")
    public String stockCorporate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        // Собираем параметры фильтрации
        List<ItemCondition> conditions = List.of(ItemCondition.NEW, ItemCondition.REPAIRED);
        List<ItemStatus> statuses = List.of(ItemStatus.ON_STOCK, ItemStatus.STOCK_BOOKED, ItemStatus.STOCK_REQUEST);
        List<Client> ownerships = List.of(Client.CORP);

        // Вызываем метод
        Page<ItemReadDto> pg = itemService.filterItems(
                conditions,
                statuses,
                ownerships,
                PageRequest.of(page, size)
        );
        model.addAttribute("page", pg);
        return "items/stock_corporate";
    }
/*
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
    }*/
}
