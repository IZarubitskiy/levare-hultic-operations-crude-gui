package com.example.levarehulticops.transactions.entity;

import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.joborders.entity.JobOrderStatus;
import com.example.levarehulticops.joborders.entity.JobOrder;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_order_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Какой JO изменился
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id", nullable = false)
    private JobOrder jobOrder;

    /**
     * Статус до изменения
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", nullable = false)
    private JobOrderStatus oldStatus;

    /**
     * Статус после изменения
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private JobOrderStatus newStatus;

    /**
     * Время изменения
     */
    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    /**
     * Кто сделал изменение
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private Employee changedBy;

    /**
     * Комментарий к записи
     */
    @Lob
    @Column(name = "comments", nullable = true)
    private String comments;
}
