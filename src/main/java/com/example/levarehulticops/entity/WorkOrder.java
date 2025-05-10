package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.WorkOrderStatus;

@Entity
@Table(name = "work_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный номер заявки
     */
    @Column(name = "work_order_number", nullable = false, unique = true)
    private String workOrderNumber;

    /**
     * Клиент (Petco, Metco, Ketco, Retco, Rets)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Client client;

    /**
     * Название скважины или местоположения
     */
    @Column(nullable = false)
    private String well;

    /**
     * Позиции заказа (Item)
     */
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    /**
     * Дата подачи заявки
     */
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    /**
     * Планируемая дата доставки
     */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /**
     * Статус заявки (Created, Approved, InProgress, Done, Cancelled)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    /**
     * Сотрудник, подавший заявку
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private Employee requestor;

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
