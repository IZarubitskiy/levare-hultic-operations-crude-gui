package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.ItemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemInfoService {
    Page<ItemInfo> getAll(Pageable pageable);
    ItemInfo getById(String partNumber);
    ItemInfo create(ItemInfo info);
    ItemInfo update(ItemInfo info);
    // нет удаления: справочник правим, но не удаляем вовсе
}
