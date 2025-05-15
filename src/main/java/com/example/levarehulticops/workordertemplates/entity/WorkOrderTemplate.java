package com.example.levarehulticops.workordertemplates.entity;

import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_order_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkOrderTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя шаблона
     */
    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName;

    /**
     * Владелец шаблона (пользователь)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Employee owner;

    /**
     * Справочные записи деталей, входящие в шаблон
     */
    @ManyToMany
    @JoinTable(
            name = "workorder_template_iteminfo",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "part_number")
    )
    private List<ItemInfo> itemsInfo = new ArrayList<>();

    /**
     * Комментарий к шаблону
     */
    @Lob
    @Column(name = "comments")
    private String comments;
}
