package com.example.levarehulticops.controller.rest;

import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemRestController {
    private final ItemService itemService;

    @GetMapping
    public Page<Item> list(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {
        return itemService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Item get(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item) {
        return itemService.create(item);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item item) {
        item.setId(id);
        return itemService.update(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
    @GetMapping("/outstanding")
    public Page<Item> outstanding(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pg = PageRequest.of(page, size);
        // USED и Ownership != RET, CORP
        List<Client> own = Arrays.stream(Client.values())
                .filter(o -> o != Client.RETS && o != Client.CORP)
                .toList();
        return itemService.findByConditionAndOwnership(ItemCondition.USED, own, pg);
    }

    @GetMapping("/rne")
    public Page<Item> rne(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pg = PageRequest.of(page, size);
        return itemService.findByConditionAndOwnership(
                ItemCondition.USED,
                List.of(Client.RETS),
                pg
        );
    }

    @GetMapping("/corporate")
    public Page<Item> corporate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pg = PageRequest.of(page, size);
        return itemService.findByConditionAndOwnership(
                ItemCondition.USED,
                List.of(Client.CORP),
                pg
        );
    }

    @GetMapping("/stock")
    public Page<Item> stock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pg = PageRequest.of(page, size);
        return itemService.findByConditionIn(
                List.of(ItemCondition.REPAIRED, ItemCondition.NEW),
                pg
        );
    }
}
