package com.example.demo;

import com.example.demo.controller.EditController;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.PagingRepository;
import com.example.demo.repository.RepoDB.FriendRequestRepoDB;
import com.example.demo.repository.RepoDB.MessageRepoDB;
import com.example.demo.repository.RepoDB.PrietenieRepoDB;
import com.example.demo.repository.RepoDB.UserRepoDB;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AplicatieDoarListaUtilizatori extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //  PENTRU PAGINA PRINCIPALA LAB 7 CU SOCIAL NETWORK
        UserRepoDB userRepoDB = new UserRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
        UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);
//
//        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("socialnetwork.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//
//        stage.setTitle("Social Network");
//        stage.setScene(scene);
//        EditController controller = fxmlLoader.getController();
//        controller.setUserTaskService( utilizatorService);
//        stage.show();

//        PagingRepository<Long, Utilizator> repoPaginat = new UserRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
//        UtilizatorService utilizatorService = new UtilizatorService(repoPaginat);

        FXMLLoader fxmlLoader = new FXMLLoader(AplicatieDoarListaUtilizatori.class.getResource("socialnetwork.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 650,500);

        EditController controller = fxmlLoader.getController();
        controller.setUserTaskService(utilizatorService);

        stage.setTitle("Social Network");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
