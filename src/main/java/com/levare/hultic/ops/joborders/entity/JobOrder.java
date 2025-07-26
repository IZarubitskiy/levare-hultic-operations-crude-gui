package com.levare.hultic.ops.joborders.entity;

import com.levare.hultic.ops.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * Represents an individual JobOrder which executes part of a WorkOrder.
 */
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
