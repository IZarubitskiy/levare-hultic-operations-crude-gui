package com.example.levarehulticops.iteminfos.controller;

import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iteminfos")
@RequiredArgsConstructor
public class ItemInfoRestController {
    private final ItemInfoService service;

    @GetMapping
    public Page<ItemInfo> list(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size) {
        return service.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{partNumber}")
    public ItemInfo get(@PathVariable String partNumber) {
        return service.getById(partNumber);
    }

    @PostMapping
    public ItemInfo create(@RequestBody ItemInfo info) {
        return service.create(info);
    }

    @PutMapping("/{partNumber}")
    public ItemInfo update(@PathVariable String partNumber,
                           @RequestBody ItemInfo info) {
        info.setPartNumber(partNumber);
        return service.update(info);
    }
}
