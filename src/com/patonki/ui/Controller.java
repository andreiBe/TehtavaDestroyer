package com.patonki.ui;

import com.patonki.KaavaTiedosto;
import com.patonki.FileManager;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public ScrollPane scrollPane;
    public TextField muuttujatTextField;
    public CustomTextArea codeArea;
    public ListView<String> pohjatListView;
    private final FileManager fileManager = new FileManager();
    private String currentFile;
    private boolean muutoksiaTapahtunut = false;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Ohjelma.STAGE.setOnCloseRequest(e -> {
            avaaTallennaPopUp();
            System.exit(0);
        });
        codeArea = new CustomTextArea(this);
        codeArea.setId("codeArea");
        scrollPane.setContent(codeArea);
        //Tekee solut editoitaviksi tuplaklikkaamalla
        pohjatListView.setCellFactory(TextFieldListCell.forListView());
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
        });
        muuttujatTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
        });
        pohjatListView.setOnKeyReleased(e -> {
            //Kun listan solu on nimetty, focus siirtyy koodin kirjoitus alueeseen
            if (e.getCode() == KeyCode.ENTER) {
                String uusiNimi = getValittuTiedosto();
                String vanhaNimi = currentFile;
                fileManager.renameFile(vanhaNimi,uusiNimi);
                currentFile = uusiNimi;
                codeArea.requestFocus();
            }
            if (e.getCode() == KeyCode.DELETE) {
                String valittu = getValittuTiedosto();
                if (valittu != null) {
                    fileManager.deleteFile(valittu);
                    pohjatListView.getItems().remove(valittu);
                }
            }
        });
        //Lisätään kaikki tiedostot pohjat kansiosta listaan
        String[] tiedostot = fileManager.initialize();
        for (String tiedosto : tiedostot) {
            pohjatListView.getItems().add(tiedosto);
        }
        currentFile = fileManager.uniqueFile();
        Ohjelma.STAGE.setOnShown(e -> {
            codeArea.requestFocus();
        });
    }
    public void lisaaPohja(ActionEvent event) { //Tapahtuu, kun käyttäjä painaa "lisää" nappia
        pohjatListView.getItems().add("Nimeämätön pohja");
    }
    //Palauttaa listan solun, joka on valittuna tai null, jos mitään ei olla valittu
    private String getValittuTiedosto() {
        List<String> list = pohjatListView.getSelectionModel().getSelectedItems();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
    public void avaaTallennaPopUp() {
        if (muutoksiaTapahtunut) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Tallennatko?");
            alert.setContentText("Haluatko tallentaa nykyisen tiedoston: "+currentFile);
            alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                tallennaTiedosto();
            }
        }
    }
    public String[] getMuuttujat() {
        return muuttujatTextField.getText().split(",");
    }
    //Tapahtuu, kun listaa klikataan
    public void listViewClicked(MouseEvent event) {
        if (event.getClickCount() == 1) { //ei tuplaklikki
            String tiedostoNimi = getValittuTiedosto();
            if (tiedostoNimi != null) {
                avaaTallennaPopUp();
                try {
                    KaavaTiedosto tiedosto = fileManager.readFile(tiedostoNimi);
                    codeArea.setText(tiedosto.getKoodi());
                    muuttujatTextField.setText(tiedosto.getMuuttujat());
                    muutoksiaTapahtunut = false;
                    currentFile = tiedostoNimi;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private KaavaTiedosto tallennaTiedosto() {
        String koodi = codeArea.getText();
        String muuttujat = muuttujatTextField.getText();
        String tiedosto = currentFile;
        muutoksiaTapahtunut = false;
        if (!pohjatListView.getItems().contains(tiedosto)) pohjatListView.getItems().add(tiedosto);
        try {
            //tallennetaan tiedosto
            fileManager.saveFile(tiedosto,koodi,muuttujat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new KaavaTiedosto(koodi,muuttujat,tiedosto);
    }
    //Tapahtuu, kun "luo" nappia painetaan
    public void create(ActionEvent event) {
        KaavaTiedosto tiedosto = tallennaTiedosto();
        TehtavaDestroyer tehtavaDestroyer = new TehtavaDestroyer(tiedosto);
        tehtavaDestroyer.show();
    }

    public void shortCut(KeyEvent e) {
        if (e.isControlDown()) {
            if (e.getCode()==KeyCode.R) {
                create(null);
            }
            if (e.getCode()==KeyCode.E) {
                pohjatListView.requestFocus();
            }
            if (e.getCode() == KeyCode.T) {
                codeArea.requestFocus();
            }
            if (e.getCode()==KeyCode.W) {
                muuttujatTextField.requestFocus();
            }
        }
    }
}
