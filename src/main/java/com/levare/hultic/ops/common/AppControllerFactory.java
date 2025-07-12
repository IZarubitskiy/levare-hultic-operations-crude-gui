package com.levare.hultic.ops.common;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.joborders.controller.JobOrderController;
import com.levare.hultic.ops.joborders.controller.NewJobOrderController;
import com.levare.hultic.ops.login.LoginController;
import com.levare.hultic.ops.main.MainController;
import com.levare.hultic.ops.tracking.controller.TrackingRecordController;
import com.levare.hultic.ops.users.controller.UserController;
import com.levare.hultic.ops.users.entity.User;
import com.levare.hultic.ops.workorders.controller.NewWorkOrderController;
import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import javafx.util.Callback;

public class AppControllerFactory implements Callback<Class<?>, Object> {

    private User currentUser;

    /** Called by LoginController after successful login */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            if (controllerClass == LoginController.class) {
                return new LoginController(
                        ServiceRegistry.USER_SERVICE,
                        this
                );
            }
            // MainController теперь принимает только фабрику
            if (controllerClass == MainController.class) {
                return new MainController(this);
            }
            if (controllerClass == WorkOrderController.class) {
                return new WorkOrderController(
                        ServiceRegistry.WORK_ORDER_SERVICE,
                        ServiceRegistry.USER_SERVICE,
                        ServiceRegistry.ITEM_SERVICE,
                        this
                );
            }
            if (controllerClass == JobOrderController.class) {
                return new JobOrderController(
                        ServiceRegistry.JOB_ORDER_SERVICE,
                        ServiceRegistry.USER_SERVICE,
                        ServiceRegistry.ITEM_SERVICE,
                        ServiceRegistry.WORK_ORDER_SERVICE,
                        this
                );
            }
            if (controllerClass == NewWorkOrderController.class) {
                return new NewWorkOrderController(
                        ServiceRegistry.WORK_ORDER_SERVICE,
                        ServiceRegistry.ITEM_INFO_SERVICE,
                        ServiceRegistry.ITEM_SERVICE,
                        ServiceRegistry.USER_SERVICE
                );
            }
            if (controllerClass == NewJobOrderController.class) {
                return new NewJobOrderController(
                        ServiceRegistry.JOB_ORDER_SERVICE,
                        ServiceRegistry.ITEM_SERVICE,
                        ServiceRegistry.USER_SERVICE
                );
            }
            if (controllerClass == ItemInfoSelectionController.class) {
                return new ItemInfoSelectionController(
                        ServiceRegistry.ITEM_INFO_SERVICE
                );
            }
            if (controllerClass == UserController.class) {
                return new UserController(
                        ServiceRegistry.USER_SERVICE
                );
            }
            if (controllerClass == TrackingRecordController.class) {
                return new TrackingRecordController(
                        ServiceRegistry.TRACKING_RECORD_SERVICE
                );
            }
            // fallback
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create controller: " + controllerClass, e);
        }
    }
}
