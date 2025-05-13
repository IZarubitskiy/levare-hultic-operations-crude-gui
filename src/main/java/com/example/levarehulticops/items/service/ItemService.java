package com.example.levarehulticops.items.service;

import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    ItemReadDto createItem(ItemCreateRequest dto);
    ItemReadDto update(Long id, ItemUpdateRequest dto);
    void delete(Long id);
    ItemReadDto getById(Long id);
    Page<ItemReadDto> getItemsByConditionAndClient(ItemCondition condition, Client client, Pageable pageable);
    Page<ItemReadDto> getItemsByConditionIn(ItemCondition[] conditions, Pageable pageable);
    Page<ItemReadDto> getAll(Pageable pageable);
    Page<ItemReadDto> getItemsByConditionAndNotClients(ItemCondition condition, Client[] excludedClients, Pageable pageable);
    Page<ItemReadDto> getStockItemsByClient(Client client, Pageable pageable);
        /**
     * Returns items for Stock Egypt:
     * condition NEW or REPAIRED, status ON_STOCK,
     * and ownership not RETS or CORP.
     */
    Page<ItemReadDto> getStockEgyptItems(Pageable pageable);

    /**
     * Returns outstanding items:
     * condition USED, status ON_STOCK,
     * ownership not RETS or CORP.
     */
    Page<ItemReadDto> getOutstandingItems(Pageable pageable);

    /**
     * Returns RNE items:
     * condition USED, status ON_STOCK, ownership = RETS.
     */
    Page<ItemReadDto> getRneItems(Pageable pageable);

    /**
     * Returns corporate stock items:
     * condition NEW or REPAIRED, status ON_STOCK, ownership = CORP.
     */
    Page<ItemReadDto> getStockCorporateItems(Pageable pageable);

    /**
     * Returns RNE Corporate items:
     * condition = USED, status = ON_STOCK, ownership = CORP.
     */
    Page<ItemReadDto> getRneCorporateItems(Pageable pageable);
}
