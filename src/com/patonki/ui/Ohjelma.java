package com.patonki.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Ohjelma extends Application {
    public static Stage STAGE;
    public static void aloita() {
        // Kutsuu start metodia uudessa javafx threadissä
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

        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        scene.setOnKeyReleased(controller::shortCut);
        primaryStage.getIcons().add(new Image("/book.png"));
        primaryStage.setTitle("Tehtävä destroyer 69");
        primaryStage.show();
    }
}
