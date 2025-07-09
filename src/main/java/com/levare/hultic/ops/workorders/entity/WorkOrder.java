package com.levare.hultic.ops.workorders.entity;

import com.levare.hultic.ops.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Work Order created for a specific client and well.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    private Long id;

    /**
     * Unique work order number
     */
    private String workOrderNumber;

    /**
     * Client (Petco, Metco, etc.)
     */
    private Client client;

    /**
     * Name of the well or location
     */
    private String well;

    /**
     * IDs of items associated with this work order
     */
    @Builder.Default
    private Set<Long> itemsId = new HashSet<>();

    /**
     * Date when the work order was requested
     */
    private LocalDate requestDate;

    /**
     * Planned delivery date
     */
    private LocalDate deliveryDate;

    /**
     * Status of the work order
     */
    private WorkOrderStatus status;

    /**
     * User who submitted the work order
     */
    private User requestor;

    /**
     * Additional comments
     */
    private String comments;

    /**
     * Convenience method to add an item ID
     */
    public void addItemId(Long itemId) {
        if (itemId != null) {
            itemsId.add(itemId);
        }
    }

    /**
     * Convenience method to remove an item ID
     */
    public void removeItemId(Long itemId) {
        itemsId.remove(itemId);
    }
}
