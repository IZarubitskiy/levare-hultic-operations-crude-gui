package com.levare.hultic.ops.items.service;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.workorders.entity.Client;

import java.util.List;

/**
 * Service interface for managing Item entities in the GUI context.
 */
public interface ItemService {

    Item create(Item item);

    Item update(Long id, Item item);

    void delete(Long id);

    Item getById(Long id);

    List<Item> getAll();

    List<Item> filterItems(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> ownerships
    );

    List<Item> filterItemsExcludeClients(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> excludedOwnerships
    );

    Item newItemFromCatalog(String itemInfoId, Client client);

    void updateStatus(Item item, ItemStatus newStatus);

    void updateWithJobOrder(Long itemId, Long jobOrderId);

    String generateSerialNumber(Item item);
}
