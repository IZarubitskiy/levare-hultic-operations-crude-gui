package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "item_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"part_number"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfo {

    /**
     * Уникальный номер детали — ключ поиска
     */
    @Id
    @Column(name = "part_number", nullable = false, unique = true)
    private String partNumber;

    /**
     * Описание детали
     */
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    /**
     * Список аналоговых деталей (не включает саму запись)
     */
    @ManyToMany
    @JoinTable(
            name = "item_info_analogs",
            joinColumns = @JoinColumn(name = "part_number"),
            inverseJoinColumns = @JoinColumn(name = "analog_part_number")
    )
    private List<ItemInfo> analogList = new ArrayList<>();

    /**
     * Комментарий к записи
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
