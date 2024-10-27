package controllers_general;


import communication.SharedStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author william
 */
public class SendMessageController implements Initializable {

    private final SharedStore sharedStore;

    private String subject;
    private String message;

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField receiverTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea textTextField;

    public SendMessageController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    public void switchToLoginPerfil() throws IOException {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(4) + "#" + sharedStore.getUser().getIdUser()); // "C4"
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("user_perfil");
    }

    @FXML
    public void switchToCourses() throws IOException {
        App.setRoot("courses_lists");
    }

    @FXML
    public void switchToInbox() throws IOException {
        App.setRoot("messages_inbox");
    }

    @FXML
    public void switchToSearch() throws IOException {
        App.setRoot("search");
    }

    @FXML
    public void switchBack() {
        App.setRoot(sharedStore.getComeback());
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;
        if (!sharedStore.getApp().stringComponentsValidation(subject)) {
            alright = false;
            subjectLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Nombre inválido.", subjectLabel.getLayoutX(), subjectLabel.getLayoutY() + 90);
        } else {
            subjectLabel.setStyle("-fx-text-fill: black;");
        }

        if (!sharedStore.getApp().allComponentsValidation(message) || message.equals("")) {
            alright = false;
            messageLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Contraseña inválida.", messageLabel.getLayoutX(), messageLabel.getLayoutY() + 90);
        } else {
            messageLabel.setStyle("-fx-text-fill: black;");
        }
        return alright;
    }

    public void configComponents() {
        textTextField.setWrapText(true); // Multilinea
        subjectTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 100 ? change : null)); // Limitación de caractéres
        textTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 3000 ? change : null));
    }

    @FXML
    public void sendMessage() throws IOException {

        subject = subjectTextField.getText();
        message = textTextField.getText();

        if (checkErrors()) {

            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String currentDate = localDate.format(dateFormatter);

            LocalTime localTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTime = localTime.format(timeFormatter);

            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(7) // C7 # Asunto # Mensaje # Fecha # Hora # tipoMensaje # idCurso # SenderYo # Destino
                    + "#" + subjectTextField.getText()
                    + "#" + textTextField.getText()
                    + "#" + currentDate
                    + "#" + currentTime
                    + "#" + "message"
                    + "#" + "-1"
                    + "#" + sharedStore.getUser().getIdUser()
                    + "#" + sharedStore.getOtherUser().getIdUser();

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("messages_inbox");
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }
        receiverTextField.setText(sharedStore.getOtherUser().getName());
        configComponents();
    }

}
