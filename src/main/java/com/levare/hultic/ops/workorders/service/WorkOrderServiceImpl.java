package com.levare.hultic.ops.workorders.service;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Implementation of WorkOrderService for JavaFX, using WorkOrder entity as defined.
 */
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderDao workOrderDao;
    private final ItemService   itemService;
    private final UserService   userService;

    public WorkOrderServiceImpl(WorkOrderDao workOrderDao,
                                UserService userService,
                                ItemService itemService) {
        this.workOrderDao = workOrderDao;
        this.userService  = userService;
        this.itemService  = itemService;
    }

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        // Initialize metadata
        workOrder.setStatus(WorkOrderStatus.CREATED);
        workOrder.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));

        // Validate and book each item by ID
        for (Long itemId : workOrder.getItemsId()) {
            Item item = itemService.getById(itemId);
            if (item.getItemStatus() != ItemStatus.ON_STOCK) {
                throw new IllegalStateException("Item " + itemId + " is not on stock");
            } else {
                switch (item.getItemCondition()) {
                    case USED -> itemService.updateStatus(item, ItemStatus.REPAIR_BOOKED);
                    case NEW, REPAIRED -> itemService.updateStatus(item, ItemStatus.STOCK_BOOKED);
                    default -> throw new IllegalStateException(
                            "Unexpected item condition: " + item.getItemCondition());
                }
            }
        }

        // Persist work order (DAO handles item links and requestor ID)
        workOrderDao.insert(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder update(WorkOrder workOrder) {
        // Ensure exists
        getById(workOrder.getId());
        // Persist changes
        workOrderDao.update(workOrder);
        return workOrder;
    }

    @Override
    public void delete(Long id) {
        // Ensure exists
        getById(id);
        workOrderDao.deleteById(id);
    }

    @Override
    public WorkOrder getById(Long id) {
        WorkOrder order = workOrderDao.findById(id);
        if (order == null) {
            throw new IllegalArgumentException("WorkOrder not found with id=" + id);
        }
        // Populate full User for requestor
        User req = order.getRequestor();
        if (req != null) {
            User full = userService.getById(req.getId());
            order.setRequestor(full);
        }
        return order;
    }

    @Override
    public List<WorkOrder> getAll() {
        List<WorkOrder> orders = workOrderDao.findAll();
        orders.forEach(this::populateRequestor);
        return orders;
    }

    @Override
    public List<WorkOrder> getByStatus(WorkOrderStatus status) {
        List<WorkOrder> orders = workOrderDao.findByStatus(status);
        orders.forEach(this::populateRequestor);
        return orders;
    }

    private void populateRequestor(WorkOrder order) {
        User req = order.getRequestor();
        if (req != null) {
            User full = userService.getById(req.getId());
            order.setRequestor(full);
        }
    }
}
