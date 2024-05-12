package com.example.demo.controller;

import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.Utilizator;
import com.example.demo.repository.Page;
import com.example.demo.repository.Pageable;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.ChangeEventType;
import com.example.demo.utils.events.UserTaskChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.demo.utils.observer.Observer;
import com.example.demo.utils.observer.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EditController implements Observer<UserTaskChangeEvent>  {
    private UtilizatorService service;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    public TableView<Utilizator> tableView;
    @FXML
    public TableColumn<Utilizator, Long> tableColumnID;
    @FXML
    public TableColumn<Utilizator, String> tableColumnFN;
    @FXML
    public TableColumn<Utilizator, String> tableColumnLN;
    @FXML
    public TableColumn<Utilizator, String> tableColumnParola;


    @FXML
    public TextField TextID;

    @FXML
    public TextField TextFN;

    @FXML
    public TextField TextLN;

    @FXML
    public Button ExitButton;

    ////////////////////////////////////////

    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    //buton INAINTE INAPOI pt a vedea lista utiliz
    @FXML
    public Button Inainte;

    @FXML
    public Button Urmator;

    @FXML
    public TextField PePagina;

    @FXML
    public Button OK;

    @FXML
    public TextField PAROLA;

    public void setUserTaskService( UtilizatorService utilizatorService){

        this.service = utilizatorService;
        service.addObserver(this);

        //initializeTableDataUser();

    }

    public void initializeTableDataUser(){
        //Iterable<Utilizator> allUsers = service.getAll();
        //List<Utilizator> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        //model.setAll(allUsersList);


        String text = PePagina.getText();
        try
        {
            Integer pageSize = Integer.parseInt(text);
            Page<Utilizator> page = service.findAllPAGINAT(new Pageable(currentPage,pageSize));
            int maxPage = (int) Math.ceil((double) page.getTotalNumarElemente() / pageSize) -1;
            if(currentPage > maxPage)
            {
                currentPage = maxPage;
                page = service.findAllPAGINAT(new Pageable(currentPage,pageSize));
            }

            model.setAll(StreamSupport.stream(page.getElementePePagina().spliterator(),
                    false).collect(Collectors.toList()));
            totalNumberOfElements = page.getTotalNumarElemente();

            Inainte.setDisable(currentPage==0);
            Urmator.setDisable((currentPage+1)*pageSize >= totalNumberOfElements);

        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }


    }

    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableDataUser();
    }


    @FXML
    public void initialize() {
        tableColumnID.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        tableColumnParola.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("password"));
        tableView.setItems(model);
    }


    /////////////////////////////////////////////
    //actiune apasare pe buton inapoi, urmator

    public void onPrevious(ActionEvent actionEvent)
    {
        currentPage--;
        initializeTableDataUser();
    }

    public void onNext(ActionEvent actionEvent)
    {
        currentPage++;
        initializeTableDataUser();
    }


////////////////////////////////////////////////////////////////

    public void handleAddUser(){
        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        String password = PAROLA.getText();
        try {

            //a creat un user, dar acum verifica daca se poate adauga in tabel, in cazul in care nu exista deja
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name, last_name,service.encrypt(password));
            user.setId(id);
            try {
                Optional<Utilizator> addedUser = service.add(user);
                if (addedUser.isPresent())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Exista deja un utilizator cu ID-ul dat!");
                else{
                    update(new UserTaskChangeEvent(ChangeEventType.ADD, user));
                }
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        PAROLA.clear();
    }

    public void handleDeleteUser(){
        try {
            Long id = Long.parseLong(TextID.getText());
            try {
                Optional<Utilizator> deletedUser = service.delete(id);
                if (deletedUser.isEmpty())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.DELETE, deletedUser.get()));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }
        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        PAROLA.clear();
    }

    public void handleUpdateUser(){
        String first_name = TextFN.getText();
        String last_name = TextLN.getText();
        try {
            Long id = Long.parseLong(TextID.getText());
            Utilizator user = new Utilizator(first_name,last_name);
            user.setId(id);
            try {
                Optional<Utilizator> updatedUser = service.update(user);
                if (updatedUser.isEmpty())
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizatorul dat!");
                else update(new UserTaskChangeEvent(ChangeEventType.UPDATE, user));
            }
            catch (Exception e){
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
        }

        TextID.clear();
        TextFN.clear();
        TextLN.clear();
        PAROLA.clear();
    }

    public void handleFindUser()  {
        try {
            Long id = Long.parseLong(TextID.getText());
            Optional<Utilizator> foundUser = service.getEntityById(id);
            if (foundUser.isEmpty()) {
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare! Nu exista utilizator cu ID-ul dat!");
                TextFN.clear();
                TextLN.clear();
            }
            else{
                TextFN.setText(foundUser.get().getFirstName());
                TextLN.setText(foundUser.get().getLastName());
                PAROLA.setText(foundUser.get().getPassword());
            }
        }
        catch (Exception e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "ID invalid! ID-ul trebuie sa fie un numar!");
            TextFN.clear();
            TextLN.clear();
            PAROLA.clear();
        }
    }

    public void handleSelectUser(){
        Utilizator utilizator = (Utilizator) tableView.getSelectionModel().getSelectedItem();

        TextID.setText(utilizator.getId().toString());
        TextFN.setText(utilizator.getFirstName());
        TextLN.setText(utilizator.getLastName());
    }

    public void handleExitButton(){
        Node src = ExitButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

}