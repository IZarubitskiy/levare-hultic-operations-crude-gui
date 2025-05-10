package com.example.levarehulticops.controller.mvc;

import com.example.levarehulticops.entity.Transaction;
import com.example.levarehulticops.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService service;

    /**
     * Просмотр всех транзакций (истории изменений статусов)
     */
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {
        Page<Transaction> pg = service.getAll(PageRequest.of(page, size));
        model.addAttribute("page", pg);
        return "transactions/list";
    }

    /**
     * Просмотр детальной информации по конкретной транзакции
     */
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Transaction tx = service.getById(id);
        model.addAttribute("transaction", tx);
        return "transactions/view";
    }
}
