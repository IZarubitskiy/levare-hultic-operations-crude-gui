// src/main/java/com/example/levarehulticops/items/controller/ItemRestController.java
package com.example.levarehulticops.items.controller;

import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.service.ItemService;
import com.example.levarehulticops.workorders.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemRestController {

    private final ItemService itemService;

    @GetMapping
    public Page<ItemReadDto> list(Pageable pageable) {
        return itemService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ItemReadDto getById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemReadDto create(@RequestBody ItemCreateRequest dto) {
        return itemService.createItem(dto);
    }

    @PutMapping("/{id}")
    public ItemReadDto update(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest dto
    ) {
        return itemService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }

    @GetMapping("/search")
    public Page<ItemReadDto> search(
            @RequestParam List<ItemCondition> conditions,
            @RequestParam List<ItemStatus> statuses,
            @RequestParam List<Client> ownerships,
            Pageable pageable
    ) {
        return itemService.filterItems(conditions, statuses, ownerships, pageable);
    }

    // алиас для stock
    @GetMapping("/stock")
    public Page<ItemReadDto> stock(
            @RequestParam Client client,
            Pageable pageable
    ) {
        return search(
                List.of(ItemCondition.NEW, ItemCondition.REPAIRED),
                List.of(ItemStatus.ON_STOCK),
                List.of(client),
                pageable
        );
    }

    // алиас для repair
    @GetMapping("/repair")
    public Page<ItemReadDto> repair(
            @RequestParam Client client,
            Pageable pageable
    ) {
        return search(
                List.of(ItemCondition.USED),
                List.of(ItemStatus.ON_STOCK),
                List.of(client),
                pageable
        );
    }

    @GetMapping("/used")
    public Page<ItemReadDto> listUsedEquipment(
            @RequestParam List<ItemCondition> conditions,
            @RequestParam List<ItemStatus> statuses,
            Pageable pageable
    ) {
        // ownerships = null означает “игнорировать владельцев”
        return itemService.filterItemsExcludeClients(
                conditions,
                statuses,
                null,
                pageable
        );
    }
}
