package com.example.levarehulticops.items.service;

import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.workorders.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    ItemReadDto createItem(ItemCreateRequest dto);

    ItemReadDto update(Long id, ItemUpdateRequest dto);

    void delete(Long id);

    Item getById(Long id);

    Page<ItemReadDto> getAll(Pageable pageable);

    Page<ItemReadDto> filterItems(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> ownerships,
            Pageable pageable
    );

    Page<ItemReadDto> filterItemsExcludeClients(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> ownerships,
            Pageable pageable
    );
    Item newItemCreateRequest(String itemInfoId, Client client) ;

    Item statusUpdate (Item i, ItemStatus itemStatus);
}
