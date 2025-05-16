package com.example.levarehulticops.iteminfos.controller;

import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/iteminfos")
@RequiredArgsConstructor
public class ItemInfoController {
    private final ItemInfoService service;
/*
    @GetMapping("/{partNumber}")
    public String view(@PathVariable String partNumber, Model model) {
        model.addAttribute("info", service.getById(partNumber));
        return "iteminfos/view";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("info", new ItemInfo());
        return "iteminfos/create";
    }

    @PostMapping
    public String create(@ModelAttribute ItemInfo info, Model model) {
        service.create(info);
        return "redirect:/iteminfos";
    }

    @GetMapping("/{partNumber}/edit")
    public String editForm(@PathVariable String partNumber, Model model) {
        model.addAttribute("info", service.getById(partNumber));
        return "iteminfos/edit";
    }

    @PostMapping("/{partNumber}/edit")
    public String update(@PathVariable String partNumber,
                         @ModelAttribute ItemInfo info) {
        info.setPartNumber(partNumber);
        service.update(info);
        return "redirect:/iteminfos";
    }*/
}
