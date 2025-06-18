package com.levare.hultic.ops.iteminfos.service;

import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of ItemInfoService using DAO.
 */
@RequiredArgsConstructor
public class ItemInfoServiceImpl implements ItemInfoService {

    private final ItemInfoDao dao;

    @Override
    public List<ItemInfo> getAll() {
        return dao.findAll();
    }

    @Override
    public ItemInfo getByPartNumber(String partNumber) {
        return dao.findAll().stream()
                .filter(i -> i.getPartNumber().equalsIgnoreCase(partNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + partNumber));
    }

    @Override
    public ItemInfo create(ItemInfo info) {
        // Prevent duplicates by part number or description
        boolean partExists = dao.findAll().stream()
                .anyMatch(i -> i.getPartNumber().equalsIgnoreCase(info.getPartNumber()));
        if (partExists) {
            throw new IllegalArgumentException("Part number already exists: " + info.getPartNumber());
        }

        boolean descriptionExists = dao.findAll().stream()
                .anyMatch(i -> i.getDescription().equalsIgnoreCase(info.getDescription()));
        if (descriptionExists) {
            throw new IllegalArgumentException("Description already exists: " + info.getDescription());
        }

        dao.insert(info);
        return info;
    }

    @Override
    public ItemInfo update(ItemInfo info) {
        // Ensure the item exists before updating
        ItemInfo existing = getByPartNumber(info.getPartNumber());

        // Prevent using duplicate description by other part
        dao.findAll().stream()
                .filter(i -> !Objects.equals(i.getId(), existing.getId()))
                .filter(i -> i.getDescription().equalsIgnoreCase(info.getDescription()))
                .findAny()
                .ifPresent(i -> {
                    throw new IllegalArgumentException("Description already used by: " + i.getPartNumber());
                });

        dao.update(info);
        return info;
    }

    @Override
    public List<ItemInfo> search(String partNumber, String description, ItemType itemType) {
        List<ItemInfo> result = new ArrayList<>();
        for (ItemInfo item : dao.findAll()) {
            boolean matches = true;

            if (partNumber != null && !partNumber.isBlank()) {
                matches &= item.getPartNumber().toLowerCase().contains(partNumber.toLowerCase());
            }
            if (description != null && !description.isBlank()) {
                matches &= item.getDescription().toLowerCase().contains(description.toLowerCase());
            }
            if (itemType != null) {
                matches &= itemType == item.getItemType();
            }

            if (matches) {
                result.add(item);
            }
        }
        return result;
    }
}
