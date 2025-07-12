package com.levare.hultic.ops.tracking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRecord {

    private Long id;
    private LocalDate recordDate;
    private ActionTarget actionTarget;
    private ActionType actionType;
    private Long targetWorkOrderId;
    private Long targetJobOrderId;
    private String targetPN;
    private String targetSN;
    private String targetDescription;
    private String reason;
    }
