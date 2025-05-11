// src/main/java/com/example/levarehulticops/entity/Attendance.java
package com.example.levarehulticops.attendances.entity;

import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.workdays.entity.WorkDay;
import lombok.*;
import javax.persistence.*;

@Entity
@Table(
        name = "attendance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"work_day_id", "employee_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Рабочий день, к которому относится запись явки
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_day_id", nullable = false)
    private WorkDay workDay;

    /**
     * Сотрудник
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * Флаг присутствия на рабочем дне
     */
    @Column(name = "present", nullable = false)
    private boolean present;

    /**
     * Комментарий по явке (например, причина отсутствия)
     */
    @Lob
    @Column(name = "comments")
    private String comments;
}
