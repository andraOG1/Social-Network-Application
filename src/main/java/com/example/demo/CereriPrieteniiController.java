package com.example.demo;

import com.example.demo.controller.alert.FriendRequestActionAlert;
import com.example.demo.controller.alert.MessageActionAlert;
import com.example.demo.controller.alert.UserActionsAlert;
import com.example.demo.domain.*;
import com.example.demo.service.FriendRequestService;
import com.example.demo.service.MessageService;
import com.example.demo.service.PrietenieService;
import com.example.demo.service.UtilizatorService;
import com.example.demo.utils.events.*;
import com.example.demo.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CereriPrieteniiController implements Observer<UserTaskChangeEvent> {

    private Long id_utiliz;
    private UtilizatorService service;

    private PrietenieService servicePriet;

    private FriendRequestService friendRequestService;

    private MessageService serviceMessage;


    //aici e pt modelul prietenilor ai unui utilizator si pt MESAJELE INTRE UTILIZ
    ObservableList<Utilizator> modelPrieteni = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelRequesturi = FXCollections.observableArrayList();

    private ObservableList<Message> modelMessage = FXCollections.observableArrayList();

    @FXML
    public TextField IDut;

    @FXML
    public TextField TextPrenume;

    @FXML
    public TextField TextNume;

    //tabelele pentru prietenii utilizatorului si pt requesturile lui
    @FXML
    public TableView<Utilizator> tabelaPrieteni;
    @FXML
    public TableColumn<Utilizator,String> tableColumnFN;
    @FXML
    public TableColumn<Utilizator,String> tableColumnLN;

    @FXML
    public TableView<Utilizator> tabelaRequesturi;
    @FXML
    public TableColumn<Utilizator,String> tableColumnFN2;
    @FXML
    public TableColumn<Utilizator,String > tableColumnLN2;

    @FXML
    public TableView<Utilizator> tabelaFriendsOfUser;

    @FXML
    public TableColumn<Utilizator,String> tableColumnFN3;

    @FXML
    public TableColumn<Utilizator,String> tableColumnLN3;

    //text fieldurile in care scri numele prenumele persoanei de adaugat
    @FXML
    public TextField text_fn;
    @FXML
    public TextField text_ln;

    //lista in care apar mesajele intre utiliz
    @FXML
    ListView<Message> listOfMessages;

    @FXML
    public TextField message;  //caseta in care introduc un mesaj de la tastatura

    @FXML
    public Button trmMesaj;


    //butoane
    @FXML
    public Button IesireBtn;

    @FXML
    public Button add;

    @FXML
    public Button acceptarePrieten;
    @FXML
    public Button refuzarePrieten;


    public void setUserTaskService(FriendRequestService friendRequestService ,UtilizatorService utilizatorService, PrietenieService servicePriet,MessageService serviceMessage ,Long id_utiliz)
    {
        this.id_utiliz = id_utiliz;
        this.friendRequestService = friendRequestService;
        this.service = utilizatorService;
        this.servicePriet = servicePriet;
        this.serviceMessage = serviceMessage;

        service.addObserver(this);
        servicePriet.addObserver(this);
        friendRequestService.addObserver(this);
        serviceMessage.addObserver(this);

        IDut.setText(String.valueOf(id_utiliz));

        Optional<Utilizator> foundUser = service.getEntityById(id_utiliz);
        TextPrenume.setText(foundUser.get().getFirstName());
        TextNume.setText(foundUser.get().getLastName());

        initializeTableFriends();
        initializeTableFriendRequests();

    }

    public void update(UserTaskChangeEvent taskChangeEvent) {
        initializeTableFriends();
        initializeTableFriendRequests();
    }

    public void initializeTableFriends(){

        List<Long> prieteni = service.get_friends(id_utiliz);

        Iterable<Utilizator> allFriendsUser = prieteni.stream()
                .map(x -> {return service.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<Utilizator> totiPrietenii = StreamSupport.stream(allFriendsUser.spliterator(), false).toList();
        modelPrieteni.setAll(totiPrietenii);
    }

    public void initializeTableFriendRequests()
    {
        List<Long> cereriPriet = friendRequestService.getFriendRequestIds(id_utiliz);
        Iterable<Utilizator> allCereriUser = cereriPriet.stream()
                .map(x->{return  service.getEntityById(x).get();})
                .collect(Collectors.toList());

        List<Utilizator> toateCererilePrietenii = StreamSupport.stream(allCereriUser.spliterator(),false).toList();
        modelRequesturi.setAll(toateCererilePrietenii);
    }

    @FXML
    public void initialize()
    {
        //aici setez numele coloanelor
        //!! "firstName" trebuie pus identic cu cum am pus campul si in Utilizator  !!!
        tableColumnFN.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableColumnLN.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tabelaPrieteni.setItems(modelPrieteni);

        tableColumnFN2.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableColumnLN2.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tabelaRequesturi.setItems(modelRequesturi);

        //aici punem in tabela noastra pe prietenii unui utiliz
        tableColumnFN3.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableColumnLN3.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tabelaFriendsOfUser.setItems(modelPrieteni);

        //!!!!!!!!!!!!
        tabelaFriendsOfUser.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /////////////////////////////////////////////////////////////////--


    public void acceptPrietenie()
    {
        //cand apas pe un utilizator din tabela request, il salvez intr un nou utilizator
        Utilizator utilizator = tabelaRequesturi.getSelectionModel().getSelectedItem();

        //vreau sa creez prietenia intre utiliz logat si cel selectat
        Tuple<Long,Long> ids = new Tuple<>(utilizator.getId(),id_utiliz);

        //si practic eu trebuie sa pun id_utiliz + celalat id intr o pereche de prietenie
        //acum punem in relatiePrietenie id urile celor 2 utiliz
        RelatiePrietenie relatiePrietenie = friendRequestService.getEntityById(ids).get();

        //acum actualizam raspunsul la cererea de prietenie in APPROVED
        relatiePrietenie.setStatus(StatusFriendRequest.APPROVED);

        //dam un update ca sa stim ce s a intamplat cu prietenia
        friendRequestService.update(relatiePrietenie);

        //adaugam prietenia in lista de prietenii
        Prietenie prietenie = new Prietenie(LocalDateTime.now());
        prietenie.setId(ids);
        servicePriet.add(prietenie);

        //type data oldData
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD,relatiePrietenie));
        update(new PrietenieTaskChangeEvent(ChangeEventType.ADD,prietenie));
    }


    public void refuzPrietenie()
    {
        //selectez un utilizator din tabela de requesturi
        Utilizator utilizator = tabelaRequesturi.getSelectionModel().getSelectedItem();

        //iau perechea de id uri id_utiliz + id-ul pe care il refuzam
        Tuple<Long,Long> ids = new Tuple<>(utilizator.getId(),id_utiliz);

        //punem cele 2 id uri grupate
        RelatiePrietenie relatiePrietenie = friendRequestService.getEntityById(ids).get();
        relatiePrietenie.setStatus(StatusFriendRequest.REJECTED);

        friendRequestService.update(relatiePrietenie);
        friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD,relatiePrietenie));

        update(new FriendRequestTaskChangeEvent(ChangeEventType.DELETE,relatiePrietenie));

    }

    public void adaugarePrieten()
    {
        //in text_fn si text_ln bagam prenume nume la care vrem sa i dam cerere
        String first_name = text_fn.getText();
        String last_name = text_ln.getText();

        if(first_name == "" || last_name == "")
        {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR,"Error!", "Ambele campuri trebuiesc completate!");
            return;
        }

        Optional<Utilizator> utilizator = service.cautareSerivce(last_name,first_name);
        if(utilizator.isEmpty())
        {
            FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error!","Utilizatorul introdus nu exista!");
            return;
        }

        StatusFriendRequest statusFriendRequest = StatusFriendRequest.PENDING;
        RelatiePrietenie relatiePrietenie = new RelatiePrietenie(statusFriendRequest);

        //!!!!!!!!!!!!!!!!!! aici am pus id urile in relatie !!!!!!!!!!!!!!!!
        relatiePrietenie.setId(new Tuple<>(id_utiliz,utilizator.get().getId()));

        try{
            Optional<RelatiePrietenie> sentRequest = friendRequestService.add(relatiePrietenie);
            if(sentRequest.isPresent()) //daca deja s a mai trimis odata cererea
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Exista deja o cerere trimisa catre utilizatorul respectiv");
            else
            {
                //daca cererea nu a mai fost trimisa deja, o trimitem
                FriendRequestActionAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes!", "Cererea a fost trimisa cu succes!");

                //NOTIFY SI UPDATE
                friendRequestService.notifyObservers(new FriendRequestTaskChangeEvent(ChangeEventType.ADD,relatiePrietenie));
                Platform.runLater(()->update(new FriendRequestTaskChangeEvent(ChangeEventType.ADD,relatiePrietenie)));
            }
        }
        catch (Exception e)
        {
            FriendRequestActionAlert.showMessage(null,Alert.AlertType.ERROR,"Error",e.getMessage());
        }
        text_fn.clear();
        text_ln.clear();
    }

    ///////////////////////////////////////////////////////////////////
    // ACUM PENTRU TAB UL CU CHAT

    private void incarcareListaMesaje(Long userIdFrom, Long userIdTo)
    {
        //le golim ca de ex poate aveam o conv cu cineva si acum am o conv cu altcnv
        //si nu vrem sa arate mesajele dintre alti oameni
        listOfMessages.getItems().clear();
        modelMessage.clear();

        for(Message msg : serviceMessage.getMessagesBetweenTwoUsers(userIdFrom,userIdTo))
        {
            modelMessage.add(msg);
        }

        listOfMessages.setItems(modelMessage);
    }

    public void trimitereMesaj()
    {
        List<Utilizator> prieteni = tabelaFriendsOfUser.getSelectionModel().getSelectedItems();

        if(prieteni.isEmpty())
        {
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error", "Trebuie selectat un om.");
            return;
        }

        String text = message.getText(); //iau textul din TextField
        if(text.isEmpty())
        {
            MessageActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Mesajul nu poate fi null.");
            return;
        }

        List<Utilizator> utilizatori = prieteni.stream()
                .map(x->{return  service.cautareSerivce(x.getLastName(),x.getFirstName()).get();})
                .toList();
        utilizatori.forEach(x->
        { serviceMessage.addMessage(id_utiliz,x.getId(),text); });

        if(utilizatori.size() == 1)
        {
            //aici neaparat get(0) !!!
            incarcareListaMesaje(id_utiliz,utilizatori.get(0).getId());
            serviceMessage.notifyObservers(new MessageTaskChangeEvent(ChangeEventType.ADD,null));
            Platform.runLater(()-> update(new MessageTaskChangeEvent(ChangeEventType.ADD, null)));
        }

        message.clear();
    }

    public void selectareMaiMulti()
    {
        List<Utilizator> utilizators = tabelaFriendsOfUser.getSelectionModel().getSelectedItems();
        if(utilizators.size()==1)
        {
            Utilizator utilizator = service.cautareSerivce(utilizators.get(0).getLastName(),utilizators.get(0).getFirstName()).get();
            incarcareListaMesaje(id_utiliz,utilizator.getId());
        }
        else
        {
            listOfMessages.getItems().clear();
            modelMessage.clear();
        }
    }

    public void Iesire()
    {
        Node src = IesireBtn;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }
}
