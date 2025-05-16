package com.example.levarehulticops.workorders.entity;

import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.items.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique work order number
     */
    @Column(name = "work_order_number", nullable = false, unique = true)
    private String workOrderNumber;

    /**
     * Client (Petco, Metco, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Client client;

    /**
     * Name of the well or location
     */
    @Column(nullable = false)
    private String well;

    /**
     * Items associated with this work order
     * Many-to-many link to inventory items
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "wo_items",
            joinColumns = @JoinColumn(name = "workorder_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> items = new HashSet<>();

    /**
     * Date when the work order was requested
     */
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    /**
     * Planned delivery date
     */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * Status of the work order
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    /**
     * User who submitted the work order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    /**
     * Additional comments
     */
    @Lob
    private String comments;

    /**
     * Version for optimistic locking
     */
    @Version
    private Long version;

    /**
     * Convenience method to add an item
     */
    public void addItem(Item item) {
        items.add(item);
        // If bi-directional, also update the opposite side here
    }

    /**
     * Convenience method to remove an item
     */
    public void removeItem(Item item) {
        items.remove(item);
        // If bi-directional, also update the opposite side here
    }
}
