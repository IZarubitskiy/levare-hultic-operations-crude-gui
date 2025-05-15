package com.example.levarehulticops.iteminfos.controller;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iteminfos")
@RequiredArgsConstructor
public class ItemInfoRestController {
    private final ItemInfoService service;

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

    @GetMapping("/search")
    public Page<ItemInfoDto> search(
            @RequestParam(defaultValue = "") String partNumber,
            @RequestParam(defaultValue = "") String description,
            @RequestParam(defaultValue = "") String itemType,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(partNumber, description, itemType, pageable);
    }

    @GetMapping("/all")
    public Page<ItemInfoDto> getAll(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.getAll(pageable);
    }

}
