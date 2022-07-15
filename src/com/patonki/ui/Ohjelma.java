package com.patonki.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Avaa ui ikkunan ja lataa sen elementit fxml tiedostosta.
 * Luokka: {@link Controller} hoitaa käyttöliittymän toiminnallisuuden.
 */
public class Ohjelma extends Application {
    public static Stage STAGE;
    public static Controller CONTROLLER;

    public static final Logger logger = LogManager.getLogger(Ohjelma.class);

    public static void aloita() {
        // Kutsuu start metodia luoden uuden javafx threadin
        launch(Ohjelma.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        STAGE = primaryStage;
        // Fxml tiedosto sisältää ui elementit
        FXMLLoader fxml = new FXMLLoader(Ohjelma.class.getResource("/ui.fxml"));
        //Huom. fxml tiedoston Controlleriksi on määritelty luokka Controller, jonka initialize metodia
        //kutsutaan tässä vaiheessa
        // Root elementti
        Parent parent = fxml.load();

        Controller controller = fxml.getController();
        CONTROLLER = controller;
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        //Ohjataan eventit Controllerille
        scene.setOnKeyReleased(controller::shortCut);

        primaryStage.getIcons().add(new Image("/book.png"));
        primaryStage.setTitle("Tehtävä destroyer 69");
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }
    //luokkametodi, jolla voi näyttää ilmoituksen, joka näyttää käyttäjälle virheen
    public static void error(Exception e) {
        Platform.runLater(() -> {
            logger.log(Level.TRACE, "Caught error: "+e.getMessage());
            e.printStackTrace();

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();

            logger.log(Level.TRACE,exceptionAsString);

            Stage alert = new Stage();
            BorderPane root = new BorderPane(new TextArea(exceptionAsString));
            alert.setScene(new Scene(root));
            alert.setTitle("Error!");
            alert.setAlwaysOnTop(true);
            alert.showAndWait();
        });
    }
}
