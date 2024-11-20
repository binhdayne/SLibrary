package com.qlthuvien.controller_admin;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;


public class BorrowReturnManagementController {

    /**
     * Khoi tao cua BorroReturnManagementController.
     */
    public void initialize() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#1a73e8"));
        shadow.setRadius(10);

//        for (Tab tab : tabPane.getTabs()) {
//            tab.setOnMouseEntered(e -> tab.setEffect(shadow));
//            tab.setOnMouseExited(e -> tab.setEffect(null));
//        }
   }
}
