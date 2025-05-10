// src/main/java/com/example/levarehulticops/service/ItemInfoService.java
package com.example.levarehulticops.service;

import com.example.levarehulticops.entity.ItemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemInfoService {
    ItemInfo create(ItemInfo itemInfo);
    ItemInfo update(ItemInfo itemInfo);
    void delete(String partNumber);
    ItemInfo getById(String partNumber);
    Page<ItemInfo> getAll(Pageable pageable);
}
