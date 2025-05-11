package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.example.levarehulticops.entity.enums.ItemType;

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
     * Unique catalog entry part number — lookup key
     */
    @Id
    @Column(name = "part_number", nullable = false, unique = true)
    private String partNumber;

    /**
     * Description of the item
     */
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    /**
     * Type of the item (e.g., Pump, Valve, Seal)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    /**
     * List of analogous catalog entries (does not include this entry)
     */
    @ManyToMany
    @JoinTable(
            name = "item_info_analogs",
            joinColumns = @JoinColumn(name = "part_number"),
            inverseJoinColumns = @JoinColumn(name = "analog_part_number")
    )
    private List<ItemInfo> analogList = new ArrayList<>();

    /**
     * Free-form comments for the catalog entry
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
