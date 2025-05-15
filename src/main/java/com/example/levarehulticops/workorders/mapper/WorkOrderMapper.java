package com.example.levarehulticops.workorders.mapper;

import com.example.levarehulticops.employees.dto.EmployeeDto;
import com.example.levarehulticops.employees.entity.Employee;
import com.example.levarehulticops.employees.mapper.EmployeeMapper;
import com.example.levarehulticops.iteminfos.dto.ItemInfoDto;
import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.mapper.ItemInfoMapper;
import com.example.levarehulticops.items.dto.ItemReadDto;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.mapper.ItemMapper;
import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.dto.WorkOrderUpdateRequest;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manual mapper for WorkOrder ↔ DTOs.
 */
@Component
public class WorkOrderMapper {

    private final ItemMapper itemMapper;
    private final ItemInfoMapper itemInfoMapper;
    private final EmployeeMapper employeeMapper;

    public WorkOrderMapper(ItemMapper itemMapper,
                           ItemInfoMapper itemInfoMapper,
                           EmployeeMapper employeeMapper) {
        this.itemMapper = itemMapper;
        this.itemInfoMapper = itemInfoMapper;
        this.employeeMapper = employeeMapper;
    }

    /**
     * Entity → Read DTO
     */
    public WorkOrderReadDto toReadDto(WorkOrder wo) {
        List<ItemReadDto> stock = wo.getStockItems().stream()
                .map(itemMapper::toReadDto)
                .collect(Collectors.toList());
        List<ItemReadDto> repair = wo.getRepairItems().stream()
                .map(itemMapper::toReadDto)
                .collect(Collectors.toList());
        List<ItemInfoDto> fresh = wo.getNewRequests().stream()
                .map(itemInfoMapper::toReadDto)
                .collect(Collectors.toList());
        EmployeeDto req = employeeMapper.toDto(wo.getRequestor());

        return new WorkOrderReadDto(
                wo.getId(),
                wo.getWorkOrderNumber(),
                wo.getClient(),
                wo.getWell(),
                stock,
                repair,
                fresh,
                wo.getRequestDate(),
                wo.getDeliveryDate(),
                wo.getStatus(),
                req,
                wo.getComments(),
                wo.getVersion()
        );
    }

    /**
     * Manual mapping from WorkOrderCreateRequest + requestorId → WorkOrder entity.
     * NOTE: related entities are created as stubs (only ID set) — they will be managed/merged by JPA.
     */
    public WorkOrder toEntity(WorkOrderCreateRequest dto, Long requestorId) {
        // 1. Base fields
        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderNumber(dto.workOrderNumber());
        wo.setClient(dto.client());                  // enum
        wo.setWell(dto.well());
        wo.setDeliveryDate(dto.deliveryDate());
        wo.setComments(dto.comments());

        // 2. Related collections (create stubs by ID)
        if (dto.stockItemIds() != null && !dto.stockItemIds().isEmpty()) {
            wo.setStockItems(
                    dto.stockItemIds().stream()
                            .map(id -> {
                                Item item = new Item();
                                item.setId(id);
                                return item;
                            })
                            .collect(Collectors.toList())
            );
        }

        if (dto.repairItemIds() != null && !dto.repairItemIds().isEmpty()) {
            wo.setRepairItems(
                    dto.repairItemIds().stream()
                            .map(id -> {
                                Item item = new Item();
                                item.setId(id);
                                return item;
                            })
                            .collect(Collectors.toList())
            );
        }

        if (dto.newRequestItemInfoIds() != null && !dto.newRequestItemInfoIds().isEmpty()) {
            wo.setNewRequests(
                    dto.newRequestItemInfoIds().stream()
                            .map(id -> {
                                ItemInfo info = new ItemInfo();
                                info.setId(id);
                                return info;
                            })
                            .collect(Collectors.toList())
            );
        }

        // 3. Requestor stub
        Employee requestor = new Employee();
        requestor.setId(requestorId);
        wo.setRequestor(requestor);

        return wo;
    }

    /**
     * Update DTO → existing Entity
     */
    public void updateEntityFromDto(WorkOrderUpdateRequest dto, WorkOrder wo) {
        if (dto.stockItemIds() != null) {
            wo.setStockItems(dto.stockItemIds().stream()
                    .map(id -> {
                        Item i = new Item();
                        i.setId(id);
                        return i;
                    })
                    .collect(Collectors.toList()));
        }
        if (dto.repairItemIds() != null) {
            wo.setRepairItems(dto.repairItemIds().stream()
                    .map(id -> {
                        Item i = new Item();
                        i.setId(id);
                        return i;
                    })
                    .collect(Collectors.toList()));
        }
        if (dto.newRequestIds() != null) {
            wo.setNewRequests(dto.newRequestIds().stream()
                    .map(code -> {
                        ItemInfo info = new ItemInfo();
                        info.setId(code);
                        return info;
                    })
                    .collect(Collectors.toList()));
        }
        if (dto.deliveryDate() != null) {
            wo.setDeliveryDate(dto.deliveryDate());
        }
        if (dto.comments() != null) {
            wo.setComments(dto.comments());
        }
    }
}
