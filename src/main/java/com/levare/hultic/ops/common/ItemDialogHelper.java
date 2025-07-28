package com.levare.hultic.ops.common;

import com.levare.hultic.ops.iteminfos.controller.ItemInfoSelectionController;
import com.levare.hultic.ops.iteminfos.entity.ItemInfo;
import com.levare.hultic.ops.iteminfos.service.ItemInfoService;
import com.levare.hultic.ops.items.entity.Item;
import com.levare.hultic.ops.items.entity.ItemCondition;
import com.levare.hultic.ops.items.entity.ItemStatus;
import com.levare.hultic.ops.items.service.ItemService;
import com.levare.hultic.ops.workorders.entity.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

/**
 * Вспомогательные диалоги для работы с ItemInfo/Item.
 */
public final class ItemDialogHelper {

    private ItemDialogHelper() {}

    /**
     * Открывает диалог выбора ItemInfo, затем создаёт Item с заданными параметрами
     * и вызывает колбэк с этим Item.
     *
     * @param title            заголовок окна выбора
     * @param itemInfoService  сервис для ItemInfo
     * @param itemService      сервис для Item
     * @param ownership        Client для создаваемого Item
     * @param condition        ItemCondition для создаваемого Item
     * @param status           ItemStatus для создаваемого Item
     * @param callback         получатель созданного Item
     * @throws Exception при ошибке открытия диалога
     */
    public static void selectInfoAndCreateItem(
            String title,
            ItemInfoService itemInfoService,
            ItemService itemService,
            Client ownership,
            ItemCondition condition,
            ItemStatus status,
            Consumer<Item> callback
    ) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                ItemDialogHelper.class.getResource("/fxml/item_info_selection.fxml")
        );
        loader.setControllerFactory(cls -> {
            if (cls == ItemInfoSelectionController.class) {
                return new ItemInfoSelectionController(itemInfoService);
            }
            try {
                return cls.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        Parent root = loader.load();
        Stage dlg = new Stage();
        dlg.initModality(Modality.APPLICATION_MODAL);
        dlg.setTitle(title);
        dlg.setScene(new Scene(root));
        dlg.showAndWait();

        ItemInfo info = loader.<ItemInfoSelectionController>getController().getSelectedItem();
        if (info == null) return;

        Item it = new Item();
        it.setItemInfo(info);
        it.setOwnership(ownership);
        it.setItemCondition(condition);
        it.setItemStatus(status);
        it.setJobOrderId(null);
        it.setComments("");
        it.setSerialNumber(itemService.generateSerialNumber(it));

        Item created = itemService.create(it);
        callback.accept(created);
    }
}
