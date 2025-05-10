package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    Item create(Item item);
    Item update(Item item);
    void delete(Long id);
    Item getById(Long id);
    Page<Item> getAll(Pageable pageable);
    /**
     * Все Items с условием и списком Ownership.
     */
    Page<Item> findByConditionAndOwnership(
            ItemCondition condition,
            List<Client> ownerships,
            Pageable pageable
    );

    /**
     * Все Items, у которых condition входит в список.
     */
    Page<Item> findByConditionIn(
            List<ItemCondition> conditions,
            Pageable pageable
    );
}
