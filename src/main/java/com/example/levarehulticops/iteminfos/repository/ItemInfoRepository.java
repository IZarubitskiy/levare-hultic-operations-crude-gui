package com.example.levarehulticops.iteminfos.repository;

import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ItemInfo entities.
 */
@Repository
public interface ItemInfoRepository extends JpaRepository<ItemInfo, String> {

    /**
     * Find ItemInfo by its primary key (partNumber).
     */
    Optional<ItemInfo> findByPartNumber(String partNumber);

    /**
     * Check existence of ItemInfo by its primary key.
     */
    @Override
    boolean existsById(String partNumber);

    /**
     * Find ItemInfo by description.
     */
    Optional<ItemInfo> findByDescription(String description);

    /**
     * Check existence of ItemInfo by description.
     */
    boolean existsByDescription(String description);

    /**
     * Check existence of ItemInfo by partnumber.
     */
    boolean existsByPartNumber(String partNumber);

    Page<ItemInfoDto> findByPartNumberContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndItemType(
            String partNumber,
            String description,
            ItemType itemType,
            Pageable pageable
    );

    Page<ItemInfo> findAll(@Nullable Specification<ItemInfo> spec, Pageable pageable);
}