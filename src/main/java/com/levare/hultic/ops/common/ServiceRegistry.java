package com.levare.hultic.ops.common;

import com.levare.hultic.ops.iteminfos.dao.ItemInfoDao;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.iteminfos.service.ItemInfoServiceImpl;
import com.levare.hultic.ops.items.dao.ItemDao;
import com.levare.hultic.ops.items.dao.SerialNumberDao;
import com.levare.hultic.ops.items.dao.ItemHistoryDao;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.items.service.ItemServiceImpl;
import com.levare.hultic.ops.joborders.dao.JobOrderDao;
import com.levare.hultic.ops.joborders.service.JobOrderService;
import com.levare.hultic.ops.joborders.service.JobOrderServiceImpl;
import com.levare.hultic.ops.tracking.dao.TrackingRecordDao;
import com.levare.hultic.ops.tracking.service.TrackingRecordService;
import com.levare.hultic.ops.tracking.service.TrackingRecordServiceImpl;
import com.levare.hultic.ops.users.dao.UserDao;
import com.levare.hultic.ops.users.service.UserService;
import com.levare.hultic.ops.users.service.UserServiceImpl;
import com.levare.hultic.ops.workorders.dao.WorkOrderDao;
import com.levare.hultic.ops.workorders.service.WorkOrderService;
import com.levare.hultic.ops.workorders.service.WorkOrderServiceImpl;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;

@UtilityClass
public class ServiceRegistry {
    Connection CONNECTION;
    UserDao USER_DAO;
    ItemInfoDao ITEM_INFO_DAO;
    ItemDao ITEM_DAO;
    SerialNumberDao SERIAL_NUMBER_DAO;
    ItemHistoryDao SERIAL_NUMBER_HISTORY_DAO;
    WorkOrderDao WORK_ORDER_DAO;
    JobOrderDao JOB_ORDER_DAO;
    TrackingRecordDao TRACKING_RECORD_DAO;

    UserService USER_SERVICE;
    ItemInfoService ITEM_INFO_SERVICE;
    ItemService ITEM_SERVICE;
    WorkOrderService WORK_ORDER_SERVICE;
    JobOrderService JOB_ORDER_SERVICE;
    TrackingRecordService TRACKING_RECORD_SERVICE;
    ExcelTemplateService EXCEL_SERVICE;

    static {
        try {
            CONNECTION               = ConnectionFactory.getConnection();
            DatabaseInitializer.initialize(CONNECTION);

            USER_DAO                 = new UserDao(CONNECTION);
            ITEM_INFO_DAO            = new ItemInfoDao(CONNECTION);
            ITEM_DAO                 = new ItemDao(CONNECTION, ITEM_INFO_DAO);
            SERIAL_NUMBER_DAO        = new SerialNumberDao(CONNECTION);
            SERIAL_NUMBER_HISTORY_DAO= new ItemHistoryDao(CONNECTION);
            WORK_ORDER_DAO           = new WorkOrderDao(CONNECTION, USER_DAO);
            JOB_ORDER_DAO            = new JobOrderDao(CONNECTION, USER_DAO);
            TRACKING_RECORD_DAO      = new TrackingRecordDao(CONNECTION);

            USER_SERVICE             = new UserServiceImpl(USER_DAO);
            ITEM_INFO_SERVICE        = new ItemInfoServiceImpl(ITEM_INFO_DAO);
            TRACKING_RECORD_SERVICE  = new TrackingRecordServiceImpl(TRACKING_RECORD_DAO);
            ITEM_SERVICE             = new ItemServiceImpl(
                    ITEM_DAO,
                    ITEM_INFO_SERVICE,
                    SERIAL_NUMBER_DAO,
                    SERIAL_NUMBER_HISTORY_DAO,
            );
            JOB_ORDER_SERVICE        = new JobOrderServiceImpl(
                    JOB_ORDER_DAO,
                    ITEM_SERVICE,
                    TRACKING_RECORD_SERVICE
            );
            WORK_ORDER_SERVICE       = new WorkOrderServiceImpl(
                    WORK_ORDER_DAO,
                    USER_SERVICE,
                    ITEM_SERVICE,
                    JOB_ORDER_SERVICE,
                    TRACKING_RECORD_SERVICE
            );
            EXCEL_SERVICE            = new ExcelTemplateService();

        } catch (SQLException e) {
            throw new ExceptionInInitializerError("ServiceRegistry init failed: " + e.getMessage());
        }
    }
}
