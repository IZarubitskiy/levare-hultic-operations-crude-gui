package com.example.levarehulticops.iteminfos.service;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemInfoService {
    Page<ItemInfoDto> getAll(Pageable pageable);
    ItemInfo getById(String partNumber);
    ItemInfo create(ItemInfo info);
    ItemInfo update(ItemInfo info);
    // нет удаления: справочник правим, но не удаляем вовсе
    Page<ItemInfoDto> filter(
            String partNumber,
            String description,
            ItemType itemType,
            Pageable pg
    );
    Page<ItemInfoDto> search(String partNumber, String description, String itemType, Pageable pageable);
}
