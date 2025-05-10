package com.example.levarehulticops.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import com.example.levarehulticops.entity.enums.JobOrderStatus;
import com.example.levarehulticops.entity.enums.ExecutionEventType;

@Entity
@Table(name = "execution_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Рабочий день, в который зафиксировано выполнение
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_day_id", nullable = false)
    private WorkDay workDay;

    /**
     * Связанный JobOrder
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id", nullable = false)
    private JobOrder jobOrder;

    /**
     * Тип события: выполнено, отменено или перенесено
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private ExecutionEventType eventType;

    /**
     * Статус JobOrder в момент события
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobOrderStatus status;

    /**
     * Время фиксации события
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Причина отмены или переноса (опционально)
     */
    @Lob
    @Column(name = "reason")
    private String reason;

    /**
     * Комментарий к записи выполнения (опционально)
     */
    @Lob
    @Column(name = "comments")
    private String comments;
}
