package com.example.demo;

import com.example.demo.controller.EditController;
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

public class StartApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //  PENTRU PAGINA PRINCIPALA LAB 7 CU SOCIAL NETWORK
//        UserRepoDB userRepoDB = new UserRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
//        UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);
//
//        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("socialnetwork.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//
//        stage.setTitle("Social Network");
//        stage.setScene(scene);
//        EditController controller = fxmlLoader.getController();
//        controller.setUserTaskService( utilizatorService);
//        stage.show();



        // PENTRU PAGINA PRINCIPALA LAB 8 CU FRIEND REQUEST
        UserRepoDB userRepoDB = new UserRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
        UtilizatorService utilizatorService = new UtilizatorService(userRepoDB);

        PrietenieRepoDB prietenieRepoDB = new PrietenieRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
        //priet service ARE prietenie repo si utiliz repo
        PrietenieService prietenieService = new PrietenieService(prietenieRepoDB,userRepoDB);

        FriendRequestRepoDB friendRequestRepoDB = new FriendRequestRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres");
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepoDB,userRepoDB,prietenieRepoDB);

        MessageRepoDB msg = new MessageRepoDB("jdbc:postgresql://localhost:5434/postgres","postgres","postgres",userRepoDB);
        MessageService msg_srv = new MessageService(msg,userRepoDB);


        FXMLLoader fxmlLoader = new FXMLLoader(StartApplication.class.getResource("paginaLogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 310, 200);

        stage.setTitle("LOG IN");
        stage.setScene(scene);
        LogInEditController controller = fxmlLoader.getController();
        controller.setUserTaskService(friendRequestService,utilizatorService,prietenieService,msg_srv);
        stage.show();

    }



    public static void main(String[] args) {
        launch();
    }
}