package com.patonki.ui;

import com.patonki.Instruction;
import com.patonki.KaavaTiedosto;
import com.patonki.Kirjoittaja;
import com.patonki.KoodiParser;
import com.patonki.util.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TehtavaDestroyer {
    private static boolean[] usedKeys = new boolean[11];
    private final Stage ikkuna;
    private int key;
    private DestroyerController controller;
    private KaavaTiedosto tiedosto;
    private static int getUniqueKey() {
        for (int i = 1; i < 10; i++) {
            if (!usedKeys[i]) {
                usedKeys[i] = true;
                return 58 + i; //59 == f1
            }
        }
        throw new IllegalArgumentException("Too many opened windows!");
    }
    public TehtavaDestroyer(KaavaTiedosto kaavaTiedosto) {
        ikkuna = new Stage();
        ikkuna.setAlwaysOnTop(true);
        ikkuna.setOnCloseRequest(e -> usedKeys[key-58] = false);
        ikkuna.setTitle("Tehtävä destoyer 69");
        ikkuna.getIcons().add(new Image("/book.png"));
        FXMLLoader fxml = new FXMLLoader(TehtavaDestroyer.class.getResource("/destroyer.fxml"));
        try {
            Parent parent = fxml.load();
            controller = fxml.getController();
            Scene scene = new Scene(parent);
            ikkuna.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tiedosto = kaavaTiedosto;
        key = getUniqueKey();
        controller.lab.setText("Press f"+(key-58)+" to execute");
        String[] muuttujat = kaavaTiedosto.getMuuttujat().split(",");
        controller.initializeUi(muuttujat);
        Listener listener = new Listener((key,cntr) -> {
            if (cntr && key == this.key) {
                execute();
            }
        });
        new Thread(listener).start();
    }
    private void execute() {
        KoodiParser parser = new KoodiParser();
        String[] muuttujat = tiedosto.getMuuttujat().split(",");
        String[] arvot = new String[muuttujat.length];
        for (int i = 0; i < muuttujat.length; i++) {
            String value = controller.getValue(muuttujat[i]);
            arvot[i] = value;
        }
        List<Instruction> ohjeet = parser.parse(tiedosto.getKoodi(),muuttujat,arvot);
        Kirjoittaja kirjoittaja = new Kirjoittaja();
        kirjoittaja.teeTehtava(ohjeet);
    }
    public void show() {
        ikkuna.show();
    }
}
