package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    Item create(Item item);
    Item update(Item item);
    void delete(Long id);
    Item getById(Long id);
    Page<Item> getAll(Pageable pageable);
}
