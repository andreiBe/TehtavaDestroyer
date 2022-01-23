package com.patonki.ui;

import com.patonki.Instruction;
import com.patonki.KaavaTiedosto;
import com.patonki.Kirjoittaja;
import com.patonki.KoodiParser;
import com.patonki.util.KeyListener;
import com.patonki.virheet.ParserException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Sisältää käyttöliittymän ja toiminnallisuuden macron ajamiseen näppäin komennolla
 */
public class TehtavaDestroyer {
    private static final boolean[] usedKeys = new boolean[10]; //f napit, jotka ovat jo jonkin toisen makron käytössä
    private static final Listener listener = new Listener(); //kuuntelee näppäinten painalluksia
    private final KaavaTiedosto tiedosto; //tiedosto, jota suoritetaan
    private final Stage ikkuna; //käyttöliittymä ikkuna
    private int key; // f-nappi, jolla macro ajetaan esim f1
    //Käyttöliittymän controlleri
    private DestroyerController controller;
    //Jos käyttäjä painaa controllia ja oikeata f-nappia macro ajetaan
    private final KeyListener<Integer> keyListener = (key, ctrlPressed) -> {
        if (ctrlPressed && key == this.key) {
            execute();
        }
    };

    public TehtavaDestroyer(KaavaTiedosto kaavaTiedosto) {
        ikkuna = new Stage();
        ikkuna.setAlwaysOnTop(true); //pysyy ikkunoiden päällä eikä jää taakse
        ikkuna.setOnCloseRequest(e -> usedKeys[key - 58] = false);
        ikkuna.setTitle("Tehtävä destoyer 69");
        ikkuna.getIcons().add(new Image("/book.png"));
        //Ladataan fxml tiedosto
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
        String[] muuttujat = kaavaTiedosto.getMuuttujat().split(",");
        if (kaavaTiedosto.getMuuttujat().isEmpty()) muuttujat = new String[0];
        //Luo teksti ruudut joihin käyttäjä voi kirjoittaa
        controller.initializeUi(muuttujat, key - 58);
        //aloittaa kuuntelun
        TehtavaDestroyer.listener.addKeyListener(keyListener);
        ikkuna.setOnCloseRequest(e -> {
            //Ei kuunnella enää napin painalluksia muuten macron suoritus alkaisi edelleen vaikka
            //ikkuna on kiinni
            TehtavaDestroyer.listener.removeKeyListener(keyListener);
            usedKeys[key - 58] = false; //näppäintä voi nyt käyttää jokin muu macro
        });
    }

    //Palauttaa f-napin, joka ei ole käytössä missään muussa auki olevassa macrossa
    private static int getUniqueKey() {
        for (int i = 1; i < 10; i++) {
            if (!usedKeys[i]) {
                usedKeys[i] = true;
                return 58 + i; //59 == f1
            }
        }
        //f-napit loppuu kesken
        throw new IllegalArgumentException("Too many opened windows!");
    }

    private void execute() {
        String[] muuttujat = tiedosto.getMuuttujatArray();
        String[] arvot = new String[muuttujat.length]; //arvot, jotka käyttäjä antoi
        for (int i = 0; i < muuttujat.length; i++) {
            String value = controller.getValue(muuttujat[i]);
            arvot[i] = value;
        }
        //Parser muuttaa tekstin listaksi ohjeita, jotka Kirjoittaja voi suorittaa
        KoodiParser parser = new KoodiParser();
        List<Instruction> ohjeet;
        try {
            ohjeet = parser.parse(tiedosto.getKoodi(), muuttujat, arvot);
            //Suoritetaan ohjeet
            Kirjoittaja kirjoittaja = new Kirjoittaja();
            kirjoittaja.teeTehtava(ohjeet);
        } catch (ParserException e) {
            e.printStackTrace();
            Ohjelma.error(e);
        }
    }

    public void show() {
        ikkuna.show();
    }

    public void test() {
        controller.test(new String[] {"9","4"});
        execute();
    }
}
