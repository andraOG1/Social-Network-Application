package com.example.demo;

import com.example.demo.CereriPrieteniiController;
import com.example.demo.controller.alert.LoginActionAlert;
import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.Utilizator;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import com.example.demo.AddAplication;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;


public class LogInEditController {

    private UtilizatorService service;

    private PrietenieService servicePriet;

    private FriendRequestService serviceFriendRequest;

    private MessageService messageService;

    @FXML
    public Button LogIn;

    @FXML
    public TextField TextIDD;

    @FXML
    public Button ExitBtn;

    @FXML
    public TextField Parola;

    public void setUserTaskService(FriendRequestService serviceFriendRequest ,UtilizatorService utilizatorService, PrietenieService servicePriet, MessageService messageService){

        this.serviceFriendRequest = serviceFriendRequest;
        this.service = utilizatorService;
        this.servicePriet = servicePriet;
        this.messageService = messageService;

    }


    public void btnLogIn()
    {
        String password = TextIDD.getText();
        String parolaREALA = Parola.getText();
        if (password.isEmpty()) {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! IDul nu poate sa fie nul!");
            TextIDD.clear();
            return;
        }
        if (parolaREALA.isEmpty()) {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Parola  nu poate sa fie nula!");
            Parola.clear();
            return;
        }
        try {
            long id = Long.parseLong(password);

            service.tryLogin(password,parolaREALA);


            Optional<Utilizator> utilizator = service.getEntityById(id);
            if(utilizator.isPresent())
            {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(AddAplication.class.getResource("prieteniiUnuiUser.fxml"));
                try {
                    Scene scene = new Scene(fxmlLoader.load(), 840, 400);
                    stage.setScene(scene);

                    CereriPrieteniiController addController = fxmlLoader.getController();
                    addController.setUserTaskService(serviceFriendRequest,service,servicePriet,messageService,id);
                    stage.show();
                }
                catch(IOException e){
                    LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
                }
                TextIDD.clear();
                return;
            }
            else {
                LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Nu am gasit nici un utilizator cu aceasta parola!");

            }
            TextIDD.clear();
        }
        catch(Exception err)
        {
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", err.getMessage());
        }
    }


    public void btnExit(){
        Node src = ExitBtn;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }
}
