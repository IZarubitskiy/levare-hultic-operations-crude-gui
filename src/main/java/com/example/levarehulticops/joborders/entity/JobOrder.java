package com.example.levarehulticops.joborders.entity;

import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "job_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * WorkOrder to which this JobOrder belongs
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    /**
     * Equipment item associated with this JobOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Current status of the JobOrder
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobOrderStatus status;

    /**
     * Employee responsible for executing this JobOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_employee_id", nullable = false)
    private Employee responsibleEmployee;

    /**
     * Free-form comments about this JobOrder
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;

    /**
     * Version for optimistic locking
     */
    @Version
    private Long version;
}
