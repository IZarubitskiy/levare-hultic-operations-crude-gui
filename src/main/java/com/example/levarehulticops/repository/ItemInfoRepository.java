package com.example.levarehulticops.repository;

import com.example.levarehulticops.entity.ItemInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ItemInfoRepository extends JpaRepository<ItemInfo, String> {
    // PK — partNumber
    Optional<ItemInfo> findByPartNumber(String partNumber);
    boolean existsByPartNumber(String partNumber);
    boolean existsByDescription(String description);

    Optional<ItemInfo> findByDescription(String description);
}
