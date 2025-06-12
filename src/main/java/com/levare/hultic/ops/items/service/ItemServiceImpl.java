package com.levare.hultic.ops.items.service;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.util.SerialNumberGenerator;
import com.levare.hultic.ops.workorders.entity.Client;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of ItemService using manual DAO and domain objects.
 */
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final ItemInfoService itemInfoService;

    public ItemServiceImpl(ItemDao itemDao, ItemInfoService itemInfoService) {
        this.itemDao = itemDao;
        this.itemInfoService = itemInfoService;
    }

    @Override
    public Item create(Item item) {
        itemDao.insert(item);
        return item;
    }

    @Override
    public Item update(Long id, Item item) {
        Item existing = getById(id);

        item.setId(existing.getId());
        itemDao.update(item);
        return item;
    }

    @Override
    public void delete(Long id) {
        Item existing = getById(id);
        itemDao.deleteById(existing.getId());
    }

    @Override
    public Item getById(Long id) {
        return itemDao.findAll().stream()
                .filter(i -> Objects.equals(i.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    @Override
    public List<Item> getAll() {
        return itemDao.findAll();
    }

    @Override
    public List<Item> filterItems(List<ItemCondition> conditions, List<ItemStatus> statuses, List<Client> ownerships) {
        return itemDao.findAll().stream()
                .filter(i -> conditions.contains(i.getItemCondition()))
                .filter(i -> statuses.contains(i.getItemStatus()))
                .filter(i -> ownerships.contains(i.getOwnership()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> filterItemsExcludeClients(List<ItemCondition> conditions, List<ItemStatus> statuses, List<Client> ownerships) {
        return itemDao.findAll().stream()
                .filter(i -> conditions.contains(i.getItemCondition()))
                .filter(i -> statuses.contains(i.getItemStatus()))
                .filter(i -> !ownerships.contains(i.getOwnership()))
                .collect(Collectors.toList());
    }

    @Override
    public Item newItemFromCatalog(String itemInfoId, Client client) {
        ItemInfo info = itemInfoService.getByPartNumber(itemInfoId);

        Item item = new Item();
        item.setItemInfo(info);
        item.setSerialNumber(SerialNumberGenerator.generate());
        item.setOwnership(client);
        item.setItemCondition(ItemCondition.NEW_ASSEMBLY);
        item.setItemStatus(ItemStatus.NEW_ASSEMBLY_REQUEST);

        itemDao.insert(item);
        return item;
    }

    @Override
    public Item updateStatus(Item item, ItemStatus newStatus) {
        Item existing = getById(item.getId());
        existing.setItemStatus(newStatus);
        itemDao.update(existing);
        return existing;
    }
}
