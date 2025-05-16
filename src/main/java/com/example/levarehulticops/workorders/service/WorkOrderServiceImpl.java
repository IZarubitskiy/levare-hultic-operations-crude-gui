// src/main/java/com/example/levarehulticops/service/impl/WorkOrderServiceImpl.java
package com.example.levarehulticops.workorders.service;

import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.service.ItemService;
import com.example.levarehulticops.users.entity.AccessLevel;
import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.users.service.UserService;
import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.entity.WorkOrder;
import com.example.levarehulticops.workorders.entity.WorkOrderStatus;
import com.example.levarehulticops.workorders.mapper.WorkOrderMapper;
import com.example.levarehulticops.workorders.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderMapper workOrderMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemInfoService itemInfoService;
    @Override
    public WorkOrderReadDto create(WorkOrderCreateRequest dto, String username) {
        // 1. Load the User entity via UserService
        User user = userService.getUserByUsername(username);

        // 2. Check authorization based on role:
        //    - ADMIN may create for any client
        //    - SALES may only create for their serviced clients
        if (user.getRole() == AccessLevel.SALES &&
                !user.getServicedClients().contains(dto.client())) {
            throw new AccessDeniedException(
                    "User '" + username +
                            "' is not allowed to create WorkOrder for client: " +
                            dto.client()
            );
        }
// 4. создание списка айтемов и выгрузка айтемов из дто и из проверка оттносятся ли они к этоу клиенту, не забукированны ли они  и после этого занесение в список.

        if (dto.stockItemIds() != null) {
            List<Item> woItems = dto.stockItemIds().stream()
                    .map(id -> {
                        Item i = itemService.getById(id);
                        if (!i.getItemStatus().equals(ItemStatus.ON_STOCK)) {
                            throw new IllegalStateException("Item " + id + " is already booked");
                        }
                        if (!i.getOwnership().equals(dto.client())){
                            throw new IllegalStateException("Item " + id + " is own by different client");
                        }
                        return i;
                    })
                    .toList();
        }

        if(dto.newItemsIds() != null){
            List<Item> newItems = dto.newItemsIds().stream()
                    .map(id ->{
                        Item i = new Item();
                        itemService.createItem()
                    }
        }
        // 3. Map DTO to entity (mapper handles simple field mapping)
        WorkOrder wo = workOrderMapper.toEntity(dto);

        // 4. Populate system-managed fields
        wo.setRequestor(user);
        wo.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        wo.setStatus(WorkOrderStatus.CREATED);

        // 5. Persist the WorkOrder
        workOrderRepository.save(wo);

        // 6. Convert to Read DTO and return
        return workOrderMapper.toReadDto(wo);
    }


        // 3. Map DTO to entity (mapper stubs related entities)
        WorkOrder wo = workOrderMapper.toEntity(dto);

        // 4. Populate system-managed fields
        wo.setRequestor(user);  // associate the authenticated user
        wo.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        wo.setStatus(WorkOrderStatus.CREATED);

        // 5. Persist the new WorkOrder
        workOrderRepository.save(wo);

        // 6. Convert to read DTO and return
        return workOrderMapper.toReadDto(wo);


    @Override
    public WorkOrder update(WorkOrder workOrder) {
        if (!workOrderRepository.existsById(workOrder.getId())) {
            throw new EntityNotFoundException("WorkOrder not found: " + workOrder.getId());
        }
        return workOrderRepository.save(workOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkOrder getById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkOrder not found: " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<WorkOrder> getAll(Pageable pageable) {
        return workOrderRepository.findAll(pageable);
    }

    @Override
    public void delete(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("WorkOrder not found: " + id);
        }
        workOrderRepository.deleteById(id);
    }

    @Override
    public Page<WorkOrderReadDto> getByStatus(WorkOrderStatus status, Pageable pageable) {
        return workOrderRepository.findAllByStatus(status, pageable)
                .map(workOrderMapper::toReadDto);
    }
}
