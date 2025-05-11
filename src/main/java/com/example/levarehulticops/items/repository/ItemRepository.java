package com.example.levarehulticops.items.repository;

import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.workorders.entity.Client;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByItemConditionAndOwnership(ItemCondition itemCondition, Client ownership, Pageable pageable);
    Page<Item> findByItemConditionIn(ItemCondition[] conditions, Pageable pageable);
    Page<Item> findByItemConditionAndOwnershipNotIn(ItemCondition itemCondition, Client[] excludedClients, Pageable pageable);
    Page<Item> findByItemConditionInAndItemStatus(
            ItemCondition[] conditions,
            ItemStatus status,
            Pageable pageable
    );
    /**
     * Finds items whose condition is in the given list,
     * whose status equals the given status,
     * and whose ownership is not any of the excluded clients.
     */
    Page<Item> findByItemConditionInAndItemStatusAndOwnershipNotIn(
            ItemCondition[] conditions,
            ItemStatus status,
            Client[] excludedClients,
            Pageable pageable
    );

    /**
     * Finds items with given condition, status, and ownership not in excludedClients.
     * For outstanding page: condition = USED.
     */
    Page<Item> findByItemConditionAndItemStatusAndOwnershipNotIn(
            ItemCondition condition,
            ItemStatus status,
            Client[] excludedClients,
            Pageable pageable
    );

    /**
     * Finds items with given condition, status, and exact ownership.
     * For RNE page: condition = USED, status = ON_STOCK, ownership = RETS.
     */
    Page<Item> findByItemConditionAndItemStatusAndOwnership(
            ItemCondition condition,
            ItemStatus status,
            Client ownership,
            Pageable pageable
    );

    /**
     * Finds items whose condition is in the given list,
     * whose status equals the given status,
     * and whose ownership equals the given client.
     */
    Page<Item> findByItemConditionInAndItemStatusAndOwnership(
            ItemCondition[] conditions,
            ItemStatus status,
            Client ownership,
            Pageable pageable
    );


}
