package com.patonki.ui;

import com.patonki.KaavaTiedosto;
import com.patonki.util.TiedostoManager;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Sisältää käyttöliittymän toiminnallisuuden, eli se mitä tapahtuu kun nappeja painetaan.
 */
public class Controller implements Initializable {
    private final TiedostoManager tiedostoManager = new TiedostoManager();
    public AnchorPane scrollPaneAlue; //aluejohon koodieditori laitetaan
    public TextField muuttujatTextField; //tekstiruutu johon muuttujat laitetaan
    public CustomTextArea koodiEditori; //koodieditori
    public ListView<String> pohjatListView; //vasemmalla oleva tiedosto lista
    private String currentFile; //tällähetkellä avattu tiedosto
    //Onko tämänhetkiseen tiedostoon tehty muutoksia viime tallentamisen jälkeen
    private boolean muutoksiaTapahtunut = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Kun ikkuna yritetään sulkea
        Ohjelma.STAGE.setOnCloseRequest(e -> {
            avaaTallennaPopUp(); //tarkistaa haluaako käyttäjä tallentaa tiedoston
            System.exit(0); //lopettaa ohjelman
        });
        koodiEditori = new CustomTextArea(this);
        koodiEditori.setId("codeArea"); //mahdollistaa css avulla muokkaamisen

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(koodiEditori);
        //Asetetaan scrollPane siten, että se täyttää koko tilan
        AnchorPane.setRightAnchor(scrollPane, 0d);
        AnchorPane.setLeftAnchor(scrollPane, 0d);
        AnchorPane.setTopAnchor(scrollPane, 0d);
        AnchorPane.setBottomAnchor(scrollPane, 0d);
        scrollPaneAlue.getChildren().add(scrollPane);


        //Tekee solut editoitaviksi tuplaklikkaamalla
        pohjatListView.setCellFactory(TextFieldListCell.forListView());
        //Kun käyttäjä muokkaa tekstiä, teksti pitää ehkä tallentaa
        koodiEditori.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
        });
        muuttujatTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            muutoksiaTapahtunut = true;
            //päivitetään koodieditori, jotta uudet muuttujat voidaan värittää
            koodiEditori.updateHighlighting();
        });
        //Uudelleen nimeäminen
        pohjatListView.setOnEditCommit(event -> {
            String uusiNimi = event.getNewValue();
            String vanhaNimi = event.getSource().getItems().get(event.getIndex());
            if (tiedostoManager.renameFile(vanhaNimi, uusiNimi)) { //uudelleen nimeäminen onnistui
                setCurrentFile(uusiNimi);
                koodiEditori.requestFocus(); //focus teksti alueeseen
                pohjatListView.getItems().set(event.getIndex(),uusiNimi);
            } else {
                Ohjelma.error(new Exception("Uudelleen nimeäminen epäonnistui!"));
            }
        });
        pohjatListView.setOnKeyReleased(e -> {
            //Poistetaan jokin listasta
            if (e.getCode() == KeyCode.DELETE) {
                String valittu = getValittuTiedosto();
                if (valittu != null) {
                    //Poistetaan sekä oikea tiedosto että listan kohta
                    if (tiedostoManager.deleteFile(valittu)) {
                        pohjatListView.getItems().remove(valittu);
                    } else {
                        Ohjelma.error(new Exception("Tiedoston poistaminen ei onnistunut!"));
                    }
                }
            }
        });
        //Lisätään kaikki tiedostot kansiosta "pohjat" listaan
        String[] tiedostot = tiedostoManager.tiedostotPohjatKansiossa();
        for (String tiedosto : tiedostot) {
            pohjatListView.getItems().add(tiedosto);
        }
        setCurrentFile(tiedostoManager.uniqueFile()); //aluksi avataan nimeämätön tiedosto
        Ohjelma.STAGE.setOnShown(e -> {
            //Focus on aluksi kirjoitusalueella
            koodiEditori.requestFocus();
        });
    }

    public void lisaaPohja(ActionEvent event) { //Tapahtuu, kun käyttäjä painaa "lisää" nappia
        String uusiTiedosto = tiedostoManager.uniqueFile();
        pohjatListView.getItems().add(uusiTiedosto);
        try {
            tiedostoManager.saveFile(uusiTiedosto, "", "");
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
    //Ei tallenna lmath tiedostoja
    public void avaaTallennaPopUp() {
        if (muutoksiaTapahtunut && !currentFile.endsWith(".mla")) {
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
        if (muuttujatTextField.getText().trim().isEmpty()) return new ArrayList<>();
        String[] muuttujat = muuttujatTextField.getText().split(",");
        for (int i = 0; i < muuttujat.length; i++) {
            muuttujat[i] = muuttujat[i].trim();
        }
        return new ArrayList<>(Arrays.asList(muuttujat));
    }

    //Tapahtuu, kun listaa klikataan
    public void listViewClicked(MouseEvent event) {
        if (event.getClickCount() == 1) { //ei tuplaklikki
            String tiedostoNimi = getValittuTiedosto();
            if (tiedostoNimi != null) { //käyttäjä klikkaa tiedostoa eikä tyhjää tilaa
                avaaTallennaPopUp(); //kysyy haluaako käyttäjä tallentaa nykyisen tiedoston ennen siirtymistä
                try {
                    KaavaTiedosto tiedosto = tiedostoManager.readFile(tiedostoNimi);

                    koodiEditori.setText(tiedosto.getKoodi());
                    muuttujatTextField.setText(tiedosto.getMuuttujat());
                    muutoksiaTapahtunut = false;
                    setCurrentFile(tiedostoNimi);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void setCurrentFile(String value) {
        koodiEditori.setEditable(!value.endsWith(".lma"));
        this.currentFile = value;
    }
    private KaavaTiedosto tallennaTiedosto() {
        String tiedosto = currentFile;
        if (tiedosto.endsWith(".lma")) {
            Ohjelma.error(new Exception("Lmath tiedostoja ei voi muokata tai tallentaa"));
        }
        String koodi = koodiEditori.getText();
        String muuttujat = muuttujatTextField.getText().replace(" ","");
        muutoksiaTapahtunut = false;
        //Jos lista ei jostain syystä sisällä tiedostoa lisätään se sinne
        if (!pohjatListView.getItems().contains(tiedosto)) pohjatListView.getItems().add(tiedosto);
        try {
            //tallennetaan tiedosto
            tiedostoManager.saveFile(tiedosto, koodi, muuttujat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new KaavaTiedosto(koodi, muuttujat);
    }

    //Tapahtuu, kun "luo" nappia painetaan
    public void create(ActionEvent event) {
        KaavaTiedosto tiedosto = tallennaTiedosto(); //ensin tiedosto tallennetaan
        //Luodaan avautuva ikkuna ja näytetään se
        TehtavaDestroyer tehtavaDestroyer = new TehtavaDestroyer(tiedosto);
        tehtavaDestroyer.show();
    }
    public void avaaTiedosto(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Teksti tiedostot","*.txt","*.lma")
        );
        File file = fileChooser.showOpenDialog(Ohjelma.STAGE);
        if (file == null) return;
        if (tiedostoManager.avaaTiedosto(file.getAbsolutePath())) {
            pohjatListView.getItems().add(file.getName());
        } else {
            Ohjelma.error(new Exception("Tiedoston avaaminen epäonnistui!"));
        }
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
                koodiEditori.requestFocus();
            }
            if (e.getCode() == KeyCode.W) {
                muuttujatTextField.requestFocus();
            }
            if (e.getCode() == KeyCode.S) {
                tallennaTiedosto();
            }
            //Hei koodin tutkija olet löytänyt ohjelman easter egg ominaisuuden
            if (e.getCode() == KeyCode.SPACE && e.isShiftDown()) {
                String latex = koodiEditori.getText();
                latex = latex.replaceAll("\n", Matcher.quoteReplacement("\\\\"));
                latex = latex.replaceAll("(?<!\\\\) ",Matcher.quoteReplacement("\\ "));
                TeXFormula formula = new TeXFormula(latex);
                java.awt.Image awtImage = formula.createBufferedImage(TeXConstants.STYLE_TEXT, 20, java.awt.Color.BLACK, null);
                Image fxImage = SwingFXUtils.toFXImage((BufferedImage) awtImage, null);
                ImageView view = new ImageView(fxImage);
                Stage stage = new Stage();
                stage.setScene(new Scene(new VBox(view))); stage.show();
            }
        }
    }
}
