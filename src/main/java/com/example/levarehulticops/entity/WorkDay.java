package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "work_days",
        uniqueConstraints = @UniqueConstraint(columnNames = {"work_date"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Дата рабочего дня
     */
    @Column(name = "work_date", nullable = false, unique = true)
    private LocalDate workDate;

    /**
     * Сотрудники, запланированные на этот день
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workday_employees",
            joinColumns = @JoinColumn(name = "work_day_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private List<Employee> employees = new ArrayList<>();

    /**
     * JobOrder, которые должны быть выполнены в этот день
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "workday_joborders",
            joinColumns = @JoinColumn(name = "work_day_id"),
            inverseJoinColumns = @JoinColumn(name = "job_order_id")
    )
    private List<JobOrder> jobOrders = new ArrayList<>();

    /**
     * Комментарий к плану на день
     */
    @Lob
    @Column(name = "comments")
    private String comments;
}
