package com.example.levarehulticops.transactions.service;

import com.example.levarehulticops.transactions.entity.Transaction;
import com.example.levarehulticops.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository repo;

    @Override
    public Transaction create(Transaction tx) {
        return repo.save(tx);
    }

    @Override
    public Transaction update(Transaction tx) {
        if (!repo.existsById(tx.getId())) {
            throw new EntityNotFoundException("Transaction not found: " + tx.getId());
        }
        return repo.save(tx);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}