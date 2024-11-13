package com.qlthuvien.controller;

import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;


public class BorrowReturnManagementController {
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
