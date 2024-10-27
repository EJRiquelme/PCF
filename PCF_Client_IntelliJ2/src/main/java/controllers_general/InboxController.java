package controllers_general;


import communication.SharedStore;
import pcf_client.executables.InboxItemController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import objects.Message;
import pcf_client.executables.App;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 *
 * @author william
 */
public class InboxController implements Initializable {

    private final SharedStore sharedStore;
    private ObservableList<Message> usersObservableList;

    public InboxController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;

    @FXML
    private Button receivedButton;
    @FXML
    private Button sendedButton;
    @FXML
    private ListView messagesListView;

    @FXML
    private void switchToSearch() {
        sharedStore.setComeback("notSent");
        App.setRoot("search");
    }

    @FXML
    private void switchToCourses() {
        sharedStore.setComeback("notSent");
        App.setRoot("courses_lists");
    }

    @FXML
    private void switchToLoginPerfil() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                + "#" + sharedStore.getUser().getIdUser(); // "C4"#IdUser Creo que lo hago en otra parte tambien: REVISAR
        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        sharedStore.setComeback("notSent");
        App.setRoot("user_perfil");
    }

    @FXML
    private void receivedMessages() {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(10) +"#" + sharedStore.getUser().getIdUser()); // C9 # IdUser
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        inboxLabel.setText("Bandeja de Entrada");
        receivedButton.setVisible(false);
        sendedButton.setVisible(true);
        loadInboxList(sharedStore.getInboxReceivedList());
        sharedStore.setComeback("notSent");
    }

    @FXML
    private void sendedMessages() {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(11) +"#" + sharedStore.getUser().getIdUser()); // C10 # IdUser
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        inboxLabel.setText("Bandeja de Salida");
        receivedButton.setVisible(true);
        sendedButton.setVisible(false);
        loadInboxList(sharedStore.getInboxSentList());
        sharedStore.setComeback("sent");
    }

    public void loadInboxList(LinkedList<Message> list) {
        usersObservableList = FXCollections.observableArrayList();
        usersObservableList.addAll(list);

        messagesListView.setItems(usersObservableList);
        messagesListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> usersListView) {
                return new InboxItemController();
            }
        });

        messagesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Message>() {
            @Override
            public void changed(ObservableValue<? extends Message> observable, Message oldValue, Message newValue) {
                sharedStore.setSeeMessage(newValue);
                if (newValue.getIdSender() == sharedStore.getUser().getIdUser()) {
                    sharedStore.setComeback("sent");
                } else {
                    sharedStore.setComeback("notSent");
                }

                App.setRoot("message_read");
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }

        if (sharedStore.getComeback().equals("sent")) {
            sendedButton.setVisible(false);
            receivedButton.setVisible(true);
            sendedMessages();
        } else {
            receivedButton.setVisible(false);
            sendedButton.setVisible(true);
            receivedMessages();
        }

        //receivedMessages();
    }

}