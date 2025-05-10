package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import com.example.levarehulticops.entity.ItemInfo;
import com.example.levarehulticops.entity.enums.Client;
import com.example.levarehulticops.entity.enums.ItemType;
import com.example.levarehulticops.entity.enums.ItemCondition;
import com.example.levarehulticops.entity.enums.ItemStatus;

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
     * Справочная информация о детали — таблица `item_info`
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_number", referencedColumnName = "part_number", nullable = false)
    private ItemInfo itemInfo;

    /**
     * Клиентский номер детали
     */
    @Column(name = "client_part_number", nullable = false)
    private String clientPartNumber;

    /**
     * Серийный номер
     */
    @Column(name = "serial_number")
    private String serialNumber;

    /**
     * Принадлежность детали к клиенту (ownership)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ownership", nullable = false)
    private Client ownership;

    /**
     * Тип детали
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    /**
     * Состояние детали
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false)
    private ItemCondition itemCondition;

    /**
     * Статус детали
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    private ItemStatus itemStatus;

    /**
     * Связь с WorkOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id")
    private WorkOrder workOrder;

    /**
     * Связь с JobOrder (когда работа по Item назначена на конкретный JobOrder)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id")
    private JobOrder jobOrder;

    /**
     * Комментарий к записи
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
