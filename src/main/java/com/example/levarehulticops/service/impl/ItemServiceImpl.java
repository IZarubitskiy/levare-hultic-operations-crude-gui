package com.example.levarehulticops.service.impl;

import com.example.levarehulticops.dto.ItemCreateRequest;
import com.example.levarehulticops.dto.ItemReadDto;
import com.example.levarehulticops.dto.ItemUpdateRequest;
import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.mapper.ItemMapper;
import com.example.levarehulticops.repository.ItemInfoRepository;
import com.example.levarehulticops.repository.ItemRepository;
import com.example.levarehulticops.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
        item.setItemType(dto.itemType());
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
    public Page<Item> findByConditionAndOwnership(
            ItemCondition condition,
            List<Client> ownerships,
            Pageable pageable
    ) {
        return itemRepository
                .findByItemConditionAndOwnershipIn(condition, ownerships, pageable);
    }

    @Override
    public Page<Item> findByConditionIn(
            List<ItemCondition> conditions,
            Pageable pageable
    ) {
        return itemRepository.findByItemConditionIn(conditions, pageable);
    }
}
