package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import com.example.levarehulticops.entity.enums.JobOrderStatus;

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
