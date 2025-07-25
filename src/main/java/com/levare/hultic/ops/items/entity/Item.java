package com.levare.hultic.ops.items.entity;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.workorders.entity.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an individual item instance with all attributes used in job and work orders.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;

    /**
     * Reference to catalog entry — table `item_info`
     */
    private ItemInfo itemInfo;

    /**
     * Serial number of the item
     */
    private String serialNumber;

    /**
     * Serial number of the item
     */
    private String oldSerialNumber;

    /**
     * Ownership classification of the item
     */
    private Client ownership;

    /**
     * Current condition of the item
     */
    private ItemCondition itemCondition;

    /**
     * Current status of the item
     */
    private ItemStatus itemStatus;

    /**
     * Relation to JobOrder (when this item is assigned to a specific job)
     */
    private Long jobOrderId;

    /**
     * Comments regarding this item
     */

    private Long workOrderId;

    private String comments;
}
