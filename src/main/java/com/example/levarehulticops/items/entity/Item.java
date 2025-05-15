package com.example.levarehulticops.items.entity;

import com.example.levarehulticops.joborders.entity.JobOrder;
import lombok.*;
import javax.persistence.*;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.workorders.entity.Client;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to catalog entry — table `item_info`
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_info_part_number", referencedColumnName = "part_number", nullable = false)
    private ItemInfo itemInfo;

    /**
     * Client-specific part number
     */
    @Column(name = "client_part_number", nullable = true)
    private String clientPartNumber;

    /**
     * Serial number of the item
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * Ownership classification of the item
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ownership", nullable = false)
    private Client ownership;

    /**
     * Current condition of the item
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    private ItemCondition itemCondition;

    /**
     * Current status of the item
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    private ItemStatus itemStatus;

    /**
     * Relation to JobOrder (when this item is assigned to a specific job)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id")
    private JobOrder jobOrder;

    /**
     * Comments regarding this item
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
