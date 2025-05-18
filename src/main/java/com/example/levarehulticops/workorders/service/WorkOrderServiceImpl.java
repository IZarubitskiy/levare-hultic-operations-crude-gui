package com.example.levarehulticops.workorders.service;

import com.example.levarehulticops.iteminfos.entity.ItemInfo;
import com.example.levarehulticops.iteminfos.service.ItemInfoService;
import com.example.levarehulticops.items.dto.ItemUpdateRequest;
import com.example.levarehulticops.items.entity.ItemCondition;
import com.example.levarehulticops.items.entity.ItemStatus;
import com.example.levarehulticops.items.entity.Item;
import com.example.levarehulticops.items.mapper.ItemMapper;
import com.example.levarehulticops.items.service.ItemService;
import com.example.levarehulticops.users.entity.AccessLevel;
import com.example.levarehulticops.users.entity.User;
import com.example.levarehulticops.users.service.UserService;
import com.example.levarehulticops.workorders.dto.WorkOrderCreateRequest;
import com.example.levarehulticops.workorders.dto.WorkOrderReadDto;
import com.example.levarehulticops.workorders.entity.Client;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderMapper workOrderMapper;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemInfoService itemInfoService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public WorkOrderReadDto create(WorkOrderCreateRequest dto, String username) {
        User user = userService.getUserByUsername(username);

        // Authorize: only ADMIN or SALES servicing this client can proceed
        boolean isAdmin = user.getRole() == AccessLevel.ADMIN;
        boolean isSalesAllowed = user.getRole() == AccessLevel.SALES
                && user.getServicedClients().contains(dto.client());
        if (!isAdmin && !isSalesAllowed) {
            throw new AccessDeniedException(
                    "User '" + username +
                            "' is not allowed to create WorkOrder for client: " +
                            dto.client()
            );
        }

        // Load & validate items in one pass
        Set<Item> woItems = Stream.concat(
                        Optional.ofNullable(dto.stockItemIds()).orElse(List.of()).stream()
                                .map(this::loadAndValidateStockItem),
                        Optional.ofNullable(dto.newItemsIds()).orElse(List.of()).stream()
                                .map(id -> itemService.newItemCreateRequest(id, dto.client()))
                )
                .collect(Collectors.toSet());

        // Map DTO → entity and set system fields
        WorkOrder wo = workOrderMapper.toEntity(dto);
        wo.setRequestor(user);
        wo.setRequestDate(LocalDate.now(ZoneId.of("Africa/Cairo")));
        wo.setStatus(WorkOrderStatus.CREATED);
        wo.setItems(woItems);

        System.out.println("метка");
        System.out.println(woItems);
        System.out.println(dto.stockItemIds());

        // Save and convert in one shot
        WorkOrder saved = workOrderRepository.save(wo);
        return workOrderMapper.toReadDto(saved);
    }

    /** Load a stock item and ensure it's ON_STOCK and owned by the correct client */
    private Item loadAndValidateAndChangeStatusStockItem(Long id) {
        Item i = itemService.getById(id);
        if (i.getItemStatus() != ItemStatus.ON_STOCK) {
            throw new IllegalStateException("Item " + id + " is already booked");
        }

        if (i.getItemCondition() == ItemCondition.USED ||)
        ItemUpdateRequest updatedItem = new ItemUpdateRequest(
                null,
                i.getSerialNumber(),
                i.getOwnership(),
                i.getItemCondition(),

                );
        /** Serial number */
        String serialNumber,

        /** Ownership classification */
        Client ownership,

        /** Current condition of the item */
        ItemCondition itemCondition,

        /** Current status of the item */
        ItemStatus itemStatus,

        /** Assigned JobOrder ID (optional) */
        Long jobOrderId,

        /** Free-form comments */
        String comments

        return i;
    }


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

