package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.repository.ItemInfoRepository;
import com.example.levarehulticops.service.ItemInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemInfoServiceImpl implements ItemInfoService {
    private final ItemInfoRepository repo;

    @Override
    public ItemInfo create(ItemInfo itemInfo) {
        return repo.save(itemInfo);
    }

    @Override
    public ItemInfo update(ItemInfo itemInfo) {
        if (!repo.existsById(itemInfo.getPartNumber())) {
            throw new EntityNotFoundException("ItemInfo not found: " + itemInfo.getPartNumber());
        }
        return repo.save(itemInfo);
    }

    @Override
    public void delete(String partNumber) {
        if (!repo.existsById(partNumber)) {
            throw new EntityNotFoundException("ItemInfo not found: " + partNumber);
        }
        repo.deleteById(partNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemInfo getById(String partNumber) {
        return repo.findById(partNumber)
                .orElseThrow(() -> new EntityNotFoundException("ItemInfo not found: " + partNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemInfo> getAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
