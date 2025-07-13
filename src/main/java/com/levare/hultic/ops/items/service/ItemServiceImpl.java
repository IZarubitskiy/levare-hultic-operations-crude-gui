package com.levare.hultic.ops.items.service;

import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.dao.SerialNumberDao;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.tracking.model.ActionType;
import com.levare.hultic.ops.tracking.service.TrackingRecordService;
import com.levare.hultic.ops.workorders.entity.Client;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

/**
 * Implementation of ItemService using manual DAO and domain objects.
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemDao itemDao;
    ItemInfoService itemInfoService;
    SerialNumberDao serialNumberDao;
    TrackingRecordService trackingRecordService;

    public ItemServiceImpl(ItemDao itemDao, ItemInfoService itemInfoService, SerialNumberDao serialNumberDao,TrackingRecordService trackingRecordService) {
        this.itemDao = itemDao;
        this.itemInfoService = itemInfoService;
        this.serialNumberDao = serialNumberDao;
        this.trackingRecordService = trackingRecordService;
    }

    @Override
    public Item create(Item item) {
        itemDao.insert(item);

        return item;
    }

    @Override
    public Item update(Long id, Item item) {
        Item existing = getById(id);

        item.setId(existing.getId());
        itemDao.update(item);
        return item;
    }

    @Override
    public void updateWithJobOrder(Long itemId, Long jobOrderId) {
        Item itemUpdated = getById(itemId);
        itemUpdated.setJobOrderId(jobOrderId);
        itemDao.update(itemUpdated);
        trackingRecordService.itemTracking(itemUpdated, ActionType.STATUS_CHANGE, "Jo set for Item");
    }


    @Override
    public void delete(Long id) {
        Item existing = getById(id);
        itemDao.deleteById(existing.getId());
    }

    @Override
    public Item getById(Long id) {
        return itemDao.findAll().stream()
                .filter(i -> Objects.equals(i.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    @Override
    public List<Item> getAll() {
        return itemDao.findAll();
    }

    @Override
    public List<Item> filterItems(List<ItemCondition> conditions, List<ItemStatus> statuses, List<Client> ownerships) {
        return itemDao.findAll().stream()
                .filter(i -> conditions.contains(i.getItemCondition()))
                .filter(i -> statuses.contains(i.getItemStatus()))
                .filter(i -> ownerships.contains(i.getOwnership()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> filterItemsExcludeClients(List<ItemCondition> conditions, List<ItemStatus> statuses, List<Client> ownerships) {
        return itemDao.findAll().stream()
                .filter(i -> conditions.contains(i.getItemCondition()))
                .filter(i -> statuses.contains(i.getItemStatus()))
                .filter(i -> !ownerships.contains(i.getOwnership()))
                .collect(Collectors.toList());
    }

    @Override
    public Item newItemFromCatalog(String itemInfoPartNumber, Client client) {
        ItemInfo info = itemInfoService.getByPartNumber(itemInfoPartNumber);

        Item item = new Item();
        item.setItemInfo(info);
        item.setOwnership(client);
        item.setItemCondition(ItemCondition.NEW_ASSEMBLY);
        item.setItemStatus(ItemStatus.NEW_ASSEMBLY_BOOKED);
        item.setSerialNumber(generateSerialNumber(item));
        itemDao.insert(item);
        trackingRecordService.itemTracking(item, ActionType.CREATION, "New assembly initiated");
        return item;
    }

    @Override
    public Item updateStatus(Item item, ItemStatus newStatus) {
        Item existing = getById(item.getId());
        ItemStatus oldStatus = existing.getItemStatus();
        existing.setItemStatus(newStatus);
        itemDao.update(existing);
        trackingRecordService.itemTracking(
                item,
                ActionType.STATUS_CHANGE,
                "Item status was changed from" + oldStatus.name() + " to " + newStatus.name() );
        return existing;
    }

    public String generateSerialNumber(Item item) {
        if (item.getItemStatus() == ItemStatus.NEW_ASSEMBLY_BOOKED && item.getItemCondition() == ItemCondition.NEW_ASSEMBLY) {
            StringBuilder sn = new StringBuilder(11);
            switch (item.getItemInfo().getItemType()) {
                case MOTOR -> sn.append("1");
                case PUMP -> sn.append("2");
                case SEAL -> sn.append("3");
                case BOI -> sn.append("4");
                case GAS_SEPARATOR -> sn.append("4");
                case SENSOR -> sn.append("5");
                case MLE -> sn.append("6");
                case CABLE -> sn.append("7");
                case SWITCHBOARD -> sn.append("8");
                case TRANSFORMER -> sn.append("9");
                case VSD -> sn.append("A");
                case THRUST_CHAMBER -> sn.append("B");
                case HPS_UNIT -> sn.append("C");
                case M2M_MLE -> sn.append("D");
                default -> throw new IllegalStateException(
                        "Unexpected item type : " + item.getItemInfo().getItemType());
            }
            switch (item.getItemInfo().getSeries()) {
                case 338 -> sn.append("A");
                case 338375 -> sn.append("B");
                case 375 -> sn.append("C");
                case 387 -> sn.append("D");
                case 400 -> sn.append("E");
                case 450456 -> sn.append("F");
                case 513 -> sn.append("G");
                case 538 -> sn.append("H");
                case 540 -> sn.append("I");
                case 562 -> sn.append("J");
                case 562538 -> sn.append("K");
                case 675 -> sn.append("L");
                case 675538 -> sn.append("M");
                case 862 -> sn.append("N");
                case 862538 -> sn.append("O");
                case 9501050 -> sn.append("P");
                case 728 -> sn.append("Q");
                default -> throw new IllegalStateException(
                        "Unexpected item Series : " + item.getItemInfo().getSeries());
            }

            sn.append("A");

            String lastTwo = String.valueOf(LocalDate.now().getYear()).substring(String.valueOf(LocalDate.now().getYear()).length() - 2);
            sn.append(lastTwo);

            switch (LocalDate.now().getMonthValue()) {
                case 1 -> sn.append("A");
                case 2 -> sn.append("B");
                case 3 -> sn.append("C");
                case 4 -> sn.append("D");
                case 5 -> sn.append("E");
                case 6 -> sn.append("F");
                case 7 -> sn.append("G");
                case 8 -> sn.append("H");
                case 9 -> sn.append("I");
                case 10 -> sn.append("J");
                case 11 -> sn.append("K");
                case 12 -> sn.append("L");
            }

            String number = Long.toString(serialNumberDao.getNextId());
            switch (number.length()) {
                case 1 -> sn.append("0000");
                case 2 -> sn.append("000");
                case 3 -> sn.append("00");
                case 4 -> sn.append("0");
            }

            sn.append(number);
            serialNumberDao.insert(sn.toString(), item.getItemInfo().getPartNumber());
            return sn.toString();

        } else {
            throw new IllegalStateException("Unexpected item condition: " + item.getItemCondition());
        }

    }
}
