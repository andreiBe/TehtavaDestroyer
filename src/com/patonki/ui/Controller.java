package com.patonki.ui;

import com.patonki.KaavaTiedosto;
import com.patonki.util.FileManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Sisältää käyttöliittymän toiminnallisuuden, eli se mitä tapahtuu kun nappeja painetaan.
 */
public class Controller implements Initializable {
    private final FileManager fileManager = new FileManager();
    public AnchorPane scrollPaneAlue;
    public TextField muuttujatTextField;
    public CustomTextArea codeArea;
    public ListView<String> pohjatListView;
    private String currentFile;
    private boolean muutoksiaTapahtunut = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Kun ikkuna yritetään sulkea
        Ohjelma.STAGE.setOnCloseRequest(e -> {
            avaaTallennaPopUp(); //tarkistaa haluaako käyttäjä tallentaa tiedoston
            System.exit(0); //lopettaa ohjelman
        });
        codeArea = new CustomTextArea(this);
        codeArea.setId("codeArea"); //mahdollistaa css avulla muokkaamisen

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        //Asetetaan scrollPane siten, että se täyttää koko tilan
        AnchorPane.setRightAnchor(scrollPane, 0d);
        AnchorPane.setLeftAnchor(scrollPane, 0d);
        AnchorPane.setTopAnchor(scrollPane, 0d);
        AnchorPane.setBottomAnchor(scrollPane, 0d);
        scrollPaneAlue.getChildren().add(scrollPane);


        //Tekee solut editoitaviksi tuplaklikkaamalla
        pohjatListView.setCellFactory(TextFieldListCell.forListView());
        //Kun käyttäjä muokkaa tekstiä, teksti pitää ehkä tallentaa
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
        });
        muuttujatTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
        });
        pohjatListView.setOnEditCommit(event -> {
            String uusiNimi = event.getNewValue();
            String vanhaNimi = event.getSource().getItems().get(event.getIndex());
            if (fileManager.renameFile(vanhaNimi, uusiNimi)) { //uudelleen nimeäminen onnistui
                currentFile = uusiNimi;
                codeArea.requestFocus(); //focus teksti alueeseen
                pohjatListView.getItems().set(event.getIndex(),uusiNimi);
            } //TODO error message
        });
        pohjatListView.setOnKeyReleased(e -> {
            //Poistetaan jokin listasta
            if (e.getCode() == KeyCode.DELETE) {
                String valittu = getValittuTiedosto();
                if (valittu != null) {
                    //Poistetaan sekä oikea tiedosto että listan kohta
                    fileManager.deleteFile(valittu);
                    pohjatListView.getItems().remove(valittu);
                }
            }
        });
        //Lisätään kaikki tiedostot kansiosta "pohjat" listaan
        String[] tiedostot = fileManager.tiedostotPohjatKansiossa();
        for (String tiedosto : tiedostot) {
            pohjatListView.getItems().add(tiedosto);
        }
        currentFile = fileManager.uniqueFile(); //aluksi avataan nimeämätön tiedosto
        Ohjelma.STAGE.setOnShown(e -> {
            //Focus on aluksi kirjoitusalueella
            codeArea.requestFocus();
        });
    }

    public void lisaaPohja(ActionEvent event) { //Tapahtuu, kun käyttäjä painaa "lisää" nappia
        String uusiTiedosto = fileManager.uniqueFile();
        pohjatListView.getItems().add(uusiTiedosto);
        try {
            fileManager.saveFile(uusiTiedosto, "", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Palauttaa listan solun, joka on valittuna tai null, jos mitään ei olla valittu
    private String getValittuTiedosto() {
        List<String> list = pohjatListView.getSelectionModel().getSelectedItems();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    //Kysyy käyttäjältä haluaako tallentaa nykyisen tiedoston.
    // Kysyy vain jos muutoksia on tapahtunut viimeisimmästä tallennuskerrasta.
    public void avaaTallennaPopUp() {
        if (muutoksiaTapahtunut) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Tallennatko?");
            alert.setContentText("Haluatko tallentaa nykyisen tiedoston: " + currentFile);
            alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                tallennaTiedosto(); //Käyttäjä painoi yes nappia
            }
        }
    }

    public List<String> getMuuttujatAsList() {
        if (muuttujatTextField.getText().isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(muuttujatTextField.getText().split(",")));
    }

    //Tapahtuu, kun listaa klikataan
    public void listViewClicked(MouseEvent event) {
        if (event.getClickCount() == 1) { //ei tuplaklikki
            String tiedostoNimi = getValittuTiedosto();
            if (tiedostoNimi != null) { //käyttäjä klikkaa tiedostoa eikä tyhjää tilaa
                avaaTallennaPopUp(); //kysyy haluaako käyttäjä tallentaa nykyisen tiedoston ennen siirtymistä
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
        //Jos lista ei jostain syystä sisällä tiedostoa lisätään se sinne
        if (!pohjatListView.getItems().contains(tiedosto)) pohjatListView.getItems().add(tiedosto);
        try {
            //tallennetaan tiedosto
            fileManager.saveFile(tiedosto, koodi, muuttujat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new KaavaTiedosto(koodi, muuttujat, tiedosto);
    }

    //Tapahtuu, kun "luo" nappia painetaan
    public void create(ActionEvent event) {
        KaavaTiedosto tiedosto = tallennaTiedosto(); //ensin tiedosto tallennetaan
        //Luodaan avautuva ikkuna ja näytetään se
        TehtavaDestroyer tehtavaDestroyer = new TehtavaDestroyer(tiedosto);
        tehtavaDestroyer.show();
    }

    public void shortCut(KeyEvent e) {
        if (e.isControlDown()) {
            if (e.getCode() == KeyCode.R) {
                create(null);
            }
            if (e.getCode() == KeyCode.E) {
                pohjatListView.requestFocus();
            }
            if (e.getCode() == KeyCode.T) {
                codeArea.requestFocus();
            }
            if (e.getCode() == KeyCode.W) {
                muuttujatTextField.requestFocus();
            }
            if (e.getCode() == KeyCode.S) {
                tallennaTiedosto();
            }
        }
    }
}
