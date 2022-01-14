package com.patonki.ui;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextArea koodiTextArea;
    public TextField muuttujatTextField;
    public ListView<String> pohjatListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pohjatListView.setCellFactory(TextFieldListCell.forListView());

        pohjatListView.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                koodiTextArea.requestFocus();
            }
        });
    }
    public void lisaaPohja(ActionEvent event) {
        pohjatListView.getItems().add("Nimeämätön pohja");
    }
    private String getValittuTiedosto() {
        List<String> list = pohjatListView.getSelectionModel().getSelectedItems();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    public void listViewClicked(MouseEvent event) {
        if (event.getClickCount() == 1) {
            String tiedosto = getValittuTiedosto();
            if (tiedosto != null) System.out.println(tiedosto);
        }
    }
    public void create(MouseEvent event) {
        String koodi = koodiTextArea.getText();
        String muuttujat = muuttujatTextField.getText();
        String tiedosto = getValittuTiedosto();

    }
}
