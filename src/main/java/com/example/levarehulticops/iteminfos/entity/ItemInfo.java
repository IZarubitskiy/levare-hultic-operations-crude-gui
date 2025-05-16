package com.example.levarehulticops.iteminfos.entity;

import com.example.levarehulticops.workorders.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalog entry for items, with per-client part-number mappings.
 */
@Entity
@Table(
        name = "item_info",
        uniqueConstraints = @UniqueConstraint(columnNames = {"part_number"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfo implements Serializable {

        /**
     * Unique catalog key for the part.
     */
        @Id
    @Column(name = "part_number", nullable = false, unique = true)
    private String partNumber;

    /**
     * Description of the item.
     */
    @Column(name = "description", length = 1000, nullable = false)
    private String description;

    /**
     * Type of the item
     */
    @Column(name = "item_type")
    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    /**
     * Map of client → client-specific part number.
     */
    @ElementCollection
    @CollectionTable(
            name = "item_info_client_parts",
            joinColumns = @JoinColumn(name = "item_info_id")
    )
    @MapKeyColumn(name = "client")
    @Column(name = "client_part_number")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Client, String> clientPartNumbers = new HashMap<>();

    /**
     * List of analogous catalog entries (not including this entry).
     */
    @ManyToMany
    @JoinTable(
            name = "item_info_analogs",
            joinColumns = @JoinColumn(name = "item_info_id"),
            inverseJoinColumns = @JoinColumn(name = "analog_item_info_id")
    )
    private List<ItemInfo> analogList = new ArrayList<>();

    /**
     * Free-form comments for the catalog entry.
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
