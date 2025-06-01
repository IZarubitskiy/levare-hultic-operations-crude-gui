package com.example.levarehulticops.items.service;

import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.repository.ItemInfoRepository;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import com.example.levarehulticops.items.dto.ItemCreateRequest;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.mapper.ItemMapper;
import com.example.levarehulticops.items.repository.ItemRepository;
import com.example.levarehulticops.items.util.SerialNumberGenerator;
import com.example.levarehulticops.workorders.entity.Client;
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
    private final ItemInfoService itemInfoService;

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
    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemReadDto> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> filterItems(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> ownerships,
            Pageable pageable
    ) {
        return itemRepository
                .findByItemConditionInAndItemStatusInAndOwnershipIn(
                        conditions, statuses, ownerships, pageable
                )
                .map(itemMapper::toReadDto);
    }

    @Override
    public Page<ItemReadDto> filterItemsExcludeClients(
            List<ItemCondition> conditions,
            List<ItemStatus> statuses,
            List<Client> ownerships,
            Pageable pageable
    ) {
        return itemRepository
                .findByItemConditionInAndItemStatusInAndOwnershipNotIn(
                        conditions, statuses, ownerships, pageable
                )
                .map(itemMapper::toReadDto);
    }

    @Override
    public Item newItemCreateRequest(String itemInfoId, Client client) {
        ItemInfo info = itemInfoService.getByPartNumber(itemInfoId);

        Item item = new Item();
        item.setItemInfo(info);
        item.setSerialNumber(SerialNumberGenerator.generate());
        item.setOwnership(client);
        item.setItemCondition(ItemCondition.NEW_ASSEMBLY);
        item.setItemStatus(ItemStatus.NEW_ASSEMBLY_REQUEST);
        return itemRepository.save(item);
    }

    @Override
    public Item statusUpdate(Item i, ItemStatus itemStatus) {
        Item iUpdate = getById(i.getId());
        iUpdate.setItemStatus(itemStatus);
        return itemRepository.save(iUpdate);
    }
}
