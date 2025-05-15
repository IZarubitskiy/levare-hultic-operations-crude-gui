package com.example.levarehulticops.workorders.entity;

import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.items.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * Client (Petco, Metco, Ketco, Retco, Rets)
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
     * 1) Items actually taken from stock (serial numbers)
     * link table: wo_stock_items(workorder_id, item_id)
     */
    @ManyToMany
    @JoinTable(
            name = "wo_items",
            joinColumns = @JoinColumn(name = "workorder_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    /**
     * Date when the work order was submitted
     */
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    /**
     * Planned delivery date
     */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * Work order status (Created, Approved, InProgress, Done, Cancelled)
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
     * Comments for the work order
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;

    /**
     * Field for optimistic locking
     */
    @Version
    private Long version;
}
