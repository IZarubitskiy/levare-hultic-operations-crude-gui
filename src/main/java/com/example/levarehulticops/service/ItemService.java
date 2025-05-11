package com.example.levarehulticops.service;

import com.example.levarehulticops.dto.ItemCreateRequest;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.ItemUpdateRequest;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    ItemReadDto createItem(ItemCreateRequest dto);
    ItemReadDto update(Long id, ItemUpdateRequest dto);
    void delete(Long id);
    ItemReadDto getById(Long id);
    Page<ItemReadDto> getAll(Pageable pageable);
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
