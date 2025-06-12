package com.levare.hultic.ops.workorders.service;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of WorkOrderService for JavaFX.
 */
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderDao workOrderDao;
    private final UserService userService;
    private final ItemService itemService;

    public WorkOrderServiceImpl(WorkOrderDao workOrderDao, UserService userService, ItemService itemService) {
        this.workOrderDao = workOrderDao;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        workOrder.setStatus(WorkOrderStatus.CREATED);
        workOrder.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));

        // Update item statuses
        Set<Item> updatedItems = workOrder.getItems().stream()
                .map(this::loadAndValidateStockItem)
                .collect(Collectors.toSet());

        workOrder.setItems(updatedItems);
        workOrderDao.insert(workOrder);
        return workOrder;
    }

    @Override
    public WorkOrder update(WorkOrder workOrder) {
        getById(workOrder.getId()); // throws if not found
        workOrderDao.update(workOrder);
        return workOrder;
    }

    @Override
    public void delete(Long id) {
        getById(id); // throws if not found
        workOrderDao.deleteById(id);
    }

    @Override
    public WorkOrder getById(Long id) {
        return workOrderDao.findById(id);
    }

    @Override
    public List<WorkOrder> getAll() {
        return workOrderDao.findAll();
    }

    @Override
    public List<WorkOrder> getByStatus(WorkOrderStatus status) {
        return workOrderDao.findByStatus(status);
    }

    private Item loadAndValidateStockItem(Item item) {
        Item i = itemService.getById(item.getId());

        if (i.getItemStatus() != ItemStatus.ON_STOCK) {
            throw new IllegalStateException("Item " + i.getId() + " is not on stock");
        }

        Item updated = switch (i.getItemCondition()) {
            case USED -> itemService.updateStatus(i, ItemStatus.REPAIR_REQUEST);
            case NEW, REPAIRED -> itemService.updateStatus(i, ItemStatus.STOCK_REQUEST);
            default -> throw new IllegalStateException("Unexpected item condition: " + i.getItemCondition());
        };

        return updated;
    }
}
