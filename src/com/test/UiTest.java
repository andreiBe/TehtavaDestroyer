package com.test;

import com.patonki.KaavaTiedosto;
import com.patonki.Main;
import com.patonki.ui.Ohjelma;
import com.patonki.ui.TehtavaDestroyer;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;

public class UiTest {
    //Ohjelma avautuu ilman ongelmia
    @Test
    void main() throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.exit();
                timer.cancel();
            }
        },4000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                KaavaTiedosto tiedosto = new KaavaTiedosto("jou+a+b","a,b");
                Platform.runLater(() -> {
                    TehtavaDestroyer destroyer = new TehtavaDestroyer(tiedosto);
                    destroyer.show();
                    destroyer.test();
                    Ohjelma.CONTROLLER.muuttujatTextField.setText("a,b");
                    Ohjelma.CONTROLLER.koodiEditori.setText("a+b Laske(a+b)");
                });
            }
        },1000);
        Main.main(new String[0]);
    }
}
