package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.repository.ItemRepository;
import com.example.levarehulticops.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repo;

    @Override
    public Item create(Item item) {
        return repo.save(item);
    }

    @Override
    public Item update(Item item) {
        if (!repo.existsById(item.getId())) {
            throw new EntityNotFoundException("Item not found: " + item.getId());
        }
        return repo.save(item);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Item not found: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Item> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Item> findByConditionAndOwnership(
            ItemCondition condition,
            List<Client> ownerships,
            Pageable pageable
    ) {
        return repo
                .findByConditionAndOwnership(condition, ownerships, pageable);
    }

    @Override
    public Page<Item> findByConditionIn(
            List<ItemCondition> conditions,
            Pageable pageable
    ) {
        return repo.findByConditionIn(conditions, pageable);
    }
}
