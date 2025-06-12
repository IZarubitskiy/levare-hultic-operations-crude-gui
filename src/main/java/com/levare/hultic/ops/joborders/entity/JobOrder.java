package com.levare.hultic.ops.joborders.entity;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an individual JobOrder which executes part of a WorkOrder.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOrder {

    private Long id;

    /**
     * WorkOrder to which this JobOrder belongs
     */
    private WorkOrder workOrder;

    /**
     * Equipment item associated with this JobOrder
     */
    private Item item;

    /**
     * Current status of the JobOrder
     */
    private JobOrderStatus status;

    /**
     * User responsible for executing this JobOrder
     */
    private User responsibleUser;

    /**
     * Free-form comments about this JobOrder
     */
    private String comments;

}
