// src/main/java/com/example/levarehulticops/controller/rest/TransactionRestController.java
package com.example.levarehulticops.controller.rest;

import com.example.levarehulticops.entity.Transaction;
import com.example.levarehulticops.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionRestController {
    private final TransactionService service;

    /**
     * Список транзакций (истории изменений статусов)
     */
    @GetMapping
    public Page<Transaction> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.getAll(PageRequest.of(page, size));
    }

    /**
     * Детали одной транзакции
     */
    @GetMapping("/{id}")
    public Transaction get(@PathVariable Long id) {
        return service.getById(id);
    }
}