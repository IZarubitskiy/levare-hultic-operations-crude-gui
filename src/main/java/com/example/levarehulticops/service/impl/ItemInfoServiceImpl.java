package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.repository.ItemInfoRepository;
import com.example.levarehulticops.service.ItemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemInfoServiceImpl implements ItemInfoService {
    private final ItemInfoRepository repo;

    @Override
    public Page<ItemInfo> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public ItemInfo getById(String partNumber) {
        return repo.findById(partNumber)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + partNumber));
    }

    @Override
    @Transactional
    public ItemInfo create(ItemInfo info) {
        // запретить дубли по номеру или описанию
        if (repo.existsByPartNumber(info.getPartNumber())) {
            throw new IllegalArgumentException("Part number already exists: " + info.getPartNumber());
        }
        if (repo.existsByDescription(info.getDescription())) {
            throw new IllegalArgumentException("Description already exists: " + info.getDescription());
        }
        return repo.save(info);
    }

    @Override
    @Transactional
    public ItemInfo update(ItemInfo info) {
        if (!repo.existsById(info.getPartNumber())) {
            throw new IllegalArgumentException("Cannot update non-existing part: " + info.getPartNumber());
        }
        // При изменении описания — проверить, что такое описание не занято другим номером
        repo.findByDescription(info.getDescription())
                .filter(other -> !other.getPartNumber().equals(info.getPartNumber()))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Description already used by " + other.getPartNumber());
                });
        return repo.save(info);
    }
}
