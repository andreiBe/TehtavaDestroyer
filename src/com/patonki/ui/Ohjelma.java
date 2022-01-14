package com.patonki.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Ohjelma extends Application
{
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(Ohjelma.class.getResource("/ui.fxml"));
        Parent parent = fxml.load();
        controller = fxml.getController();
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("/book.png"));
        primaryStage.setTitle("Tehtävä destroyer 69");
        primaryStage.show();
    }
    public static void aloita() {
        launch(Ohjelma.class);
    }
}
