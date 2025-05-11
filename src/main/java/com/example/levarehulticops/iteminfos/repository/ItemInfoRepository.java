package com.example.levarehulticops.iteminfos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import java.util.Optional;

/**
 * Repository for ItemInfo entities.
 */
@Repository
public interface ItemInfoRepository extends JpaRepository<ItemInfo, String> {

    /**
     * Find ItemInfo by its primary key (partNumber).
     */
    @Override
    Optional<ItemInfo> findById(String partNumber);

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
    boolean existsByPartNumber (String partNumber);
}