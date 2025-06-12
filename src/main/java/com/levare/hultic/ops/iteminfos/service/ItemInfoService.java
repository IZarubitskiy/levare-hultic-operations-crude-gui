package com.levare.hultic.ops.iteminfos.service;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.entity.ItemType;

import java.util.List;

/**
 * Service interface for ItemInfo.
 * Provides business logic operations for managing catalog items.
 */
public interface ItemInfoService {

    List<ItemInfo> getAll();

    ItemInfo getByPartNumber(String partNumber);

    ItemInfo create(ItemInfo info);

    ItemInfo update(ItemInfo info);

    List<ItemInfo> search(String partNumber, String description, ItemType itemType);

    // No delete method: items are never removed
}
