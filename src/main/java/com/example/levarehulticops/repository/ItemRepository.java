package com.example.levarehulticops.repository;

import com.example.levarehulticops.entity.Item;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByConditionAndOwnership(ItemCondition cond,
                                           List<Client> ownerships, Pageable pg);
    Page<Item> findByConditionIn(List<ItemCondition> conds, Pageable pg);

}
