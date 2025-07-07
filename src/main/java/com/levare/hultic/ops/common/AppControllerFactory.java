package com.levare.hultic.ops.common;

import com.levare.hultic.ops.login.LoginController;
import com.levare.hultic.ops.main.MainController;
import com.levare.hultic.ops.workorders.controller.WorkOrderController;
import com.levare.hultic.ops.workorders.controller.NewWorkOrderController;
import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.users.controller.UserController;
import com.levare.hultic.ops.users.entity.User;
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
            // Login screen needs UserService + factory
            if (controllerClass == LoginController.class) {
                return new LoginController(
                        ServiceRegistry.USER_SERVICE,
                        this
                );
            }
            // MainController needs only the factory
            if (controllerClass == MainController.class) {
                return new MainController(this);
            }
            // Work orders list
            if (controllerClass == WorkOrderController.class) {
                return new WorkOrderController(
                        ServiceRegistry.WORK_ORDER_SERVICE,
                        this
                );
            }
            // New work order form
            if (controllerClass == NewWorkOrderController.class) {
                return new NewWorkOrderController(
                        ServiceRegistry.WORK_ORDER_SERVICE,
                        ServiceRegistry.ITEM_INFO_SERVICE,
                        ServiceRegistry.ITEM_SERVICE
                );
            }
            // ItemInfo selection dialog
            if (controllerClass == ItemInfoSelectionController.class) {
                return new ItemInfoSelectionController(
                        ServiceRegistry.ITEM_INFO_SERVICE
                );
            }
            // User management screen
            if (controllerClass == UserController.class) {
                return new UserController(
                        ServiceRegistry.USER_SERVICE
                );
            }
            // Fallback to default no-arg constructor
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create controller: " + controllerClass, e);
        }
    }
}
