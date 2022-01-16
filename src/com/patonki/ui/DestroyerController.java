package com.patonki.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;

public class DestroyerController {
    public VBox list;
    public Label lab;
    private HashMap<String, TextField> muuttujat = new HashMap<>();
    private ArrayList<TextField> fields = new ArrayList<>();
    public String getValue(String variable) {
        return muuttujat.get(variable).getText();
    }
    public void initializeUi(String[] muuttujat) {
        for (int i = 0; i < muuttujat.length; i++) {
            String muuttuja = muuttujat[i];
            HBox box = new HBox();
            Label lab = new Label(muuttuja + ":");
            lab.setId("muuttujaLabel");
            TextField field = new TextField();
            fields.add(field);
            int finalI = i;
            field.setOnKeyPressed(key -> {
                if (key.getCode()== KeyCode.DOWN) {
                    if (finalI < fields.size()-1) {
                        fields.get(finalI+1).requestFocus();
                    }
                }
                if (key.getCode() == KeyCode.UP) {
                    if (finalI > 0) {
                        fields.get(finalI-1).requestFocus();
                    }
                }
            });
            this.muuttujat.put(muuttuja, field);
            HBox.setHgrow(field, Priority.ALWAYS);
            box.getChildren().addAll(lab, field);
            list.getChildren().add(box);
        }
    }
}
