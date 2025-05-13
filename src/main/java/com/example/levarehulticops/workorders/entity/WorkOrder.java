package com.example.levarehulticops.workorders.entity;

import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.items.entity.Item;
import lombok.*;
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
     *    link table: wo_stock_items(workorder_id, item_id)
     */
    @ManyToMany
    @JoinTable(
            name = "wo_stock_items",
            joinColumns = @JoinColumn(name = "workorder_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> stockItems = new ArrayList<>();

    /**
     * 2) Items sent for repair (serial numbers)
     *    link table: wo_repair_items(workorder_id, item_id)
     */
    @ManyToMany
    @JoinTable(
            name = "wo_repair_items",
            joinColumns = @JoinColumn(name = "workorder_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> repairItems = new ArrayList<>();

    /**
     * 3) Items ordered (new), without serial numbers, reference only by item info
     *    link table: wo_new_requests(workorder_id, item_info_id)
     */
    @ManyToMany
    @JoinTable(
            name = "wo_new_requests",
            joinColumns = @JoinColumn(name = "workorder_id"),
            inverseJoinColumns = @JoinColumn(name = "item_info_id")
    )
    private List<ItemInfo> newRequests = new ArrayList<>();

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
     * Employee who submitted the work order
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private Employee requestor;

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
