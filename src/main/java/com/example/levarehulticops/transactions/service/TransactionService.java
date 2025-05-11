package com.example.levarehulticops.transactions.service;

import com.example.levarehulticops.transactions.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    Transaction create(Transaction tx);
    Transaction update(Transaction tx);
    void delete(Long id);
    Transaction getById(Long id);
    Page<Transaction> getAll(Pageable pageable);
}
