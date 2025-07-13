package com.levare.hultic.ops.workorders.service;

import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.joborders.entity.JobOrder;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.service.TrackingRecordService;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.entity.WorkOrder;
import com.levare.hultic.ops.workorders.entity.WorkOrderStatus;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Implementation of WorkOrderService for JavaFX, using WorkOrder entity as defined.
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class WorkOrderServiceImpl implements WorkOrderService {

    WorkOrderDao workOrderDao;
    ItemService itemService;
    UserService userService;
    JobOrderService jobOrderService;
    TrackingRecordService trackingRecordService;



    public WorkOrderServiceImpl(WorkOrderDao workOrderDao,
                                UserService userService,
                                ItemService itemService,
                                JobOrderService jobOrderService,
                                TrackingRecordService trackingRecordService) {
        this.workOrderDao = workOrderDao;
        this.userService = userService;
        this.itemService = itemService;
        this.jobOrderService = jobOrderService;
        this.trackingRecordService = trackingRecordService;
    }

    @Override
    public WorkOrder create(WorkOrder workOrder) {
        // Initialize metadata
        workOrder.setStatus(WorkOrderStatus.CREATED);
        workOrder.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));

        // Validate and book each item by ID
        for (Long itemId : workOrder.getItemsId()) {
            Item item = itemService.getById(itemId);
            if (item.getItemStatus() != ItemStatus.ON_STOCK && item.getItemStatus() != ItemStatus.NEW_ASSEMBLY_BOOKED) {
                System.out.println("Item " + itemId + " is not on stock");
                throw new IllegalStateException();
            } else {
                switch (item.getItemCondition()) {
                    case USED -> itemService.updateStatus(item, ItemStatus.REPAIR_BOOKED);
                    case NEW, REPAIRED -> itemService.updateStatus(item, ItemStatus.STOCK_BOOKED);
                    case NEW_ASSEMBLY -> itemService.updateStatus(item, ItemStatus.NEW_ASSEMBLY_BOOKED);
                    default -> throw new IllegalStateException(
                            "Unexpected item condition: " + item.getItemCondition());
                }
            }
        }

        trackingRecordService.workOrderTracking(workOrderDao.insert(workOrder), ActionType.CREATION, "new WO created");
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
    public void delete(Long id, String reason) {

        // modifying items
        for (Long itemId : getById(id).getItemsId()) {
            Item item = itemService.getById(itemId);
            if (item.getJobOrderId() == null){
                switch (item.getItemStatus()){
                    case REPAIR_BOOKED -> {
                        if (item.getItemCondition() != ItemCondition.NEW || item.getItemCondition() != ItemCondition.REPAIRED) {
                            itemService.updateStatus(item, ItemStatus.ON_STOCK);
                        }
                    }
                    case STOCK_BOOKED -> itemService.updateStatus(item, ItemStatus.ON_STOCK);
                    case NEW_ASSEMBLY_BOOKED -> itemService.delete(item.getId());
                    default -> throw new IllegalStateException(
                            "Unexpected item Status: " + item.getItemStatus());
                    }
            } else {
                JobOrder joUpdate = jobOrderService.getById(item.getJobOrderId());
                joUpdate.setWorkOrderId(null);
                jobOrderService.update(item.getJobOrderId(), joUpdate);
            }}
        trackingRecordService.workOrderTracking(getById(id), ActionType.REMOVAL, reason);
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
