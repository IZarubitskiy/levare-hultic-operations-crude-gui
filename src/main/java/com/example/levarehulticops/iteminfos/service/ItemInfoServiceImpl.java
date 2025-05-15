package com.example.levarehulticops.iteminfos.service;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.entity.ItemType;
import com.example.levarehulticops.iteminfos.mapper.ItemInfoMapper;
import com.example.levarehulticops.iteminfos.repository.ItemInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemInfoServiceImpl implements ItemInfoService {
    private final ItemInfoRepository repo;
    private final ItemInfoMapper itemInfoMapper;

    @Override
    public Page<ItemInfoDto> getAll(Pageable pageable) {
        return repo.findAll(pageable)
                .map(itemInfo -> new ItemInfoDto(
                        itemInfo.getPartNumber(),
                        itemInfo.getDescription(),
                        itemInfo.getItemType()
                ));
    }

    @Override
    public ItemInfo getById(String partNumber) {
        return repo.findById(partNumber)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + partNumber));
    }

    @Override
    @Transactional
    public ItemInfo create(ItemInfo info) {
        // запретить дубли по номеру или описанию
        if (repo.existsByPartNumber(info.getPartNumber())) {
            throw new IllegalArgumentException("Part number already exists: " + info.getPartNumber());
        }
        if (repo.existsByDescription(info.getDescription())) {
            throw new IllegalArgumentException("Description already exists: " + info.getDescription());
        }
        return repo.save(info);
    }

    @Override
    @Transactional
    public ItemInfo update(ItemInfo info) {
        if (!repo.existsById(info.getPartNumber())) {
            throw new IllegalArgumentException("Cannot update non-existing part: " + info.getPartNumber());
        }
        // При изменении описания — проверить, что такое описание не занято другим номером
        repo.findByDescription(info.getDescription())
                .filter(other -> !other.getPartNumber().equals(info.getPartNumber()))
                .ifPresent(other -> {
                    throw new IllegalArgumentException("Description already used by " + other.getPartNumber());
                });
        return repo.save(info);
    }

    @Override
    public Page<ItemInfoDto> filter(
            String partNumber,
            String description,
            ItemType itemType,
            Pageable pageable
    ) {
        return repo.findByPartNumberContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndItemType(
                partNumber == null ? "" : partNumber,
                description == null ? "" : description,
                itemType,
                pageable
        );
    }

    @Override
    public Page<ItemInfoDto> search(String partNumber, String description, String itemType, Pageable pageable) {
        // пример на Specification:
        Specification<ItemInfo> spec = Specification.where(null);

        if (!partNumber.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("partNumber")), "%" + partNumber.toLowerCase() + "%"));
        }
        if (!description.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
        }
        if (!itemType.isBlank()) {
            spec = spec.and((root, q, cb) ->
                    cb.equal(root.get("itemType"), ItemType.valueOf(itemType)));
        }

        return repo.findAll(spec, pageable)
                .map(itemInfoMapper::toReadDto);
    }
}
