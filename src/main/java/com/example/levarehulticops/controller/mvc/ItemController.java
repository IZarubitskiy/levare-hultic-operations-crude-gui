package com.example.levarehulticops.controller.mvc;

import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        Page<Item> pg = itemService.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "items/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new Item());
        return "items/create";
    }

    @PostMapping
    public String create(@ModelAttribute Item item) {
        itemService.create(item);
        return "redirect:/items";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.getById(id));
        return "items/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.getById(id));
        return "items/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Item item) {
        item.setId(id);
        itemService.update(item);
        return "redirect:/items";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/items";
    }

    @GetMapping("/outstanding")
    public String outstanding(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable pg = PageRequest.of(page, size);

        // собираем все Ownership кроме RET и CORP
        List<Client> own = Arrays.stream(Client.values())
                .filter(o -> o != Client.RETS && o != Client.CORP)
                .collect(Collectors.toList());

        model.addAttribute("page",
                itemService.findByConditionAndOwnership(ItemCondition.USED, own, pg));
        return "items/outstanding";
    }

    @GetMapping("/rne")
    public String rne(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Pageable pg = PageRequest.of(page, size);
        model.addAttribute("page",
                itemService.findByConditionAndOwnership(
                        ItemCondition.USED,
                        Collections.singletonList(Client.RETS),
                        pg));
        return "items/rne";
    }

    @GetMapping("/corporate")
    public String corporate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Pageable pg = PageRequest.of(page, size);
        model.addAttribute("page",
                itemService.findByConditionAndOwnership(
                        ItemCondition.USED,
                        Collections.singletonList(Client.CORP),
                        pg));
        return "items/corporate";
    }

    @GetMapping("/stock")
    public String stock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        Pageable pg = PageRequest.of(page, size);
        model.addAttribute("page",
                itemService.findByConditionIn(
                        Arrays.asList(ItemCondition.REPAIRED, ItemCondition.NEW),
                        pg));
        return "items/stock";
    }
}
