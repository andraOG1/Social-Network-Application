package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AddAplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AddAplication.class.getResource("prieteniiUnuiUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 840, 400);
        stage.setTitle("FRIENDS");
        stage.setScene(scene);
        stage.show();
    }


}