package com.levare.hultic.ops.joborders.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOrder {
    private Long id;
    private Long workOrderId;
    private Long itemId;
    private JobOrderStatus status;
    private JobOrderType jobOrderType;
    private LocalDate plannedDate;
    private LocalDate plannedDateUpdated;
    private LocalDate finishedDate;
    private String comments;
}
