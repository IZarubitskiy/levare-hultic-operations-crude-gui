package com.example.levarehulticops.items.service;

import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.mapper.ItemMapper;
import com.example.levarehulticops.iteminfos.repository.ItemInfoRepository;
import com.example.levarehulticops.items.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemInfoRepository itemInfoRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemReadDto createItem(ItemCreateRequest dto) {
        String partNumber = dto.itemInfoPartNumber();
        ItemInfo info = itemInfoRepository.findById(partNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ItemInfo not found, partNumber=" + partNumber));

        String clientPart = info.getClientPartNumbers()
                .getOrDefault(dto.ownership(), info.getPartNumber());

        Item item = new Item();
        item.setItemInfo(info);
        item.setClientPartNumber(clientPart);
        item.setSerialNumber(dto.serialNumber());
        item.setOwnership(dto.ownership());
        item.setItemCondition(dto.itemCondition());
        item.setItemStatus(dto.itemStatus());
        item.setComments(dto.comments());
        Item saved = itemRepository.save(item);

        return itemMapper.toReadDto(saved);
    }

    @Override
    public ItemReadDto update(Long id, ItemUpdateRequest dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + id));

        // Apply non-null updates to the entity
        itemMapper.updateEntityFromDto(dto, item);

        Item updated = itemRepository.save(item);
        return itemMapper.toReadDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found: " + id);
        }
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemReadDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + id));
        return itemMapper.toReadDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemReadDto> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getItemsByConditionAndClient(ItemCondition condition, Client client, Pageable pageable) {
        Page<Item> page =
                itemRepository.findByItemConditionAndOwnership(condition, client, pageable);
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getItemsByConditionIn(ItemCondition[] conditions, Pageable pageable) {
        Page<Item> page =
                itemRepository.findByItemConditionIn(conditions, pageable);
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getItemsByConditionAndNotClients(ItemCondition condition, Client[] excludedClients, Pageable pageable) {
        Page<Item> page =
                itemRepository.findByItemConditionAndOwnershipNotIn(condition, excludedClients, pageable);
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getStockEgyptItems(Pageable pageable) {
        ItemCondition[] conds = { ItemCondition.NEW, ItemCondition.REPAIRED };
        Client[] excluded = { Client.CORP, Client.RETS };
        Page<Item> page =
                itemRepository.findByItemConditionInAndItemStatusAndOwnershipNotIn(
                        conds, ItemStatus.ON_STOCK, excluded, pageable
                );
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getOutstandingItems(Pageable pageable) {
        Client[] excluded = { Client.CORP, Client.RETS };
        Page<Item> page =
                itemRepository.findByItemConditionAndItemStatusAndOwnershipNotIn(
                        ItemCondition.USED,
                        ItemStatus.ON_STOCK,
                        excluded,
                        pageable
                );
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getRneItems(Pageable pageable) {
        Page<Item> page =
                itemRepository.findByItemConditionAndItemStatusAndOwnership(
                        ItemCondition.USED,
                        ItemStatus.ON_STOCK,
                        Client.RETS,
                        pageable
                );
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getStockCorporateItems(Pageable pageable) {
        // Теперь NEW и REPAIRED
        ItemCondition[] conds = { ItemCondition.NEW, ItemCondition.REPAIRED };
        Page<Item> page = itemRepository.findByItemConditionInAndItemStatusAndOwnership(
                conds,
                ItemStatus.ON_STOCK,
                Client.CORP,
                pageable
        );
        return page.map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> getRneCorporateItems(Pageable pageable) {
        Page<Item> page = itemRepository.findByItemConditionAndItemStatusAndOwnership(
                ItemCondition.USED,
                ItemStatus.ON_STOCK,
                Client.CORP,
                pageable
        );
        return page.map(itemMapper::toReadDto);
    }
}
