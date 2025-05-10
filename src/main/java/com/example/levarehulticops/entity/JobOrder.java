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
     * Оборудование, к которому относится данный JobOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Статус JobOrder
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobOrderStatus status;

    /**
     * Сотрудник, ответственный за исполнение JobOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_employee_id", nullable = false)
    private Employee responsibleEmployee;

    /**
     * Комментарий к записи
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;

    /**
     * Поле для оптимистической блокировки
     */
    @Version
    private Long version;

}
