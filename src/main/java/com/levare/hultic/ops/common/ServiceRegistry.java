package com.levare.hultic.ops.common;

import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.dao.SerialNumberDao;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.items.service.ItemServiceImpl;
import com.levare.hultic.ops.joborders.dao.JobOrderDao;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.joborders.service.JobOrderServiceImpl;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.users.service.UserServiceImpl;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import com.levare.hultic.ops.workorders.service.WorkOrderServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;

public final class ServiceRegistry {
    public static final Connection CONNECTION;
    public static final UserDao USER_DAO;
    public static final ItemInfoDao ITEM_INFO_DAO;
    public static final ItemDao ITEM_DAO;
    public static final SerialNumberDao SERIAL_NUMBER_DAO;
    public static final WorkOrderDao WORK_ORDER_DAO;
    public static final JobOrderDao JOB_ORDER_DAO;

    public static final UserService USER_SERVICE;
    public static final ItemInfoService ITEM_INFO_SERVICE;
    public static final ItemService ITEM_SERVICE;
    public static final WorkOrderService WORK_ORDER_SERVICE;
    public static final JobOrderService JOB_ORDER_SERVICE;

    static {
        try {
            // 1) Создаём соединение и инициализируем схему
            CONNECTION = ConnectionFactory.getConnection();
            DatabaseInitializer.initialize(CONNECTION);

            // 2) DAO
            USER_DAO = new UserDao(CONNECTION);
            ITEM_INFO_DAO = new ItemInfoDao(CONNECTION);
            ITEM_DAO = new ItemDao(CONNECTION, ITEM_INFO_DAO);
            SERIAL_NUMBER_DAO = new SerialNumberDao(CONNECTION);
            JOB_ORDER_DAO = new JobOrderDao(CONNECTION, USER_DAO);
            WORK_ORDER_DAO = new WorkOrderDao(CONNECTION, USER_DAO);

            // 3) Сервисы
            USER_SERVICE = new UserServiceImpl(USER_DAO);
            ITEM_INFO_SERVICE = new ItemInfoServiceImpl(ITEM_INFO_DAO);
            ITEM_SERVICE = new ItemServiceImpl(ITEM_DAO, ITEM_INFO_SERVICE, SERIAL_NUMBER_DAO);
            WORK_ORDER_SERVICE = new WorkOrderServiceImpl(WORK_ORDER_DAO, USER_SERVICE, ITEM_SERVICE);
            JOB_ORDER_SERVICE = new JobOrderServiceImpl(JOB_ORDER_DAO, WORK_ORDER_SERVICE, ITEM_SERVICE);

        } catch (SQLException e) {
            throw new ExceptionInInitializerError("ServiceRegistry init failed: " + e.getMessage());
        }
    }

    private ServiceRegistry() { /* no instances */ }
}
