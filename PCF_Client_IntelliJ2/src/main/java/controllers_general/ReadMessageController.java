package controllers_general;


import communication.SharedStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import objects.User;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author william
 */
public class ReadMessageController implements Initializable {

    private final SharedStore sharedStore;

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;
    @FXML
    private Label senderReceiverLabel;

    @FXML
    private TextField senderTextField;
    @FXML
    private TextField subjectTextField;
    @FXML
    private TextArea textTextField;

    @FXML
    private Button acceptRequestButton;
    @FXML
    private Button deleteMessageButton;
    @FXML
    private Button replyMessageButton;

    public ReadMessageController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    public void switchToSearch() {
        sharedStore.setComeback("notSent");
        App.setRoot("search");
    }

    @FXML
    public void switchToCourses() {
        sharedStore.setComeback("notSent");
        App.setRoot("courses_lists");
    }

    @FXML
    public void switchToLoginPerfil() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                + "#" + sharedStore.getUser().getIdUser(); // "C4"#IdUser Creo que lo hago en otra parte tambien: REVISAR
        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        sharedStore.setComeback("notSent");
        App.setRoot("user_perfil");
    }

    @FXML
    public void switchToInbox() {
        App.setRoot("messages_inbox");
    }

    @FXML
    public void acceptRequest() {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necearia para aceptar la Solicitud.",
                "Por favor confirme que desea aceptar la Solicitud al Curso.")) {

            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String currentDate = localDate.format(dateFormatter);

            LocalTime localTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTime = localTime.format(timeFormatter);

            String messageToServer = "";

            if(sharedStore.getSeeMessage().getType().equals("request")) {
                messageToServer = sharedStore.getProtocolMessages().getClientArgument(13) // C13 # ...
                        + "#" + sharedStore.getSeeMessage().getIdSender() // 1. Id Solicitante
                        + "#" + sharedStore.getSeeMessage().getIdTeachersCourse() // 2. Id Curso
                        + "#" + sharedStore.getSeeMessage().getIdMessage() // 3. Id Mensaje (Para el Borrado)
                        + "#" + sharedStore.getSeeMessage().getIdReceiver() // 4. Id Profesor
                        + "#Solicitud Aceptada." // 5. Asunto
                        + "#Se ha aceptado una solicitud a tu nombre, concediendote una plaza en el Curso. Revisa tus Cursos para acceder al mismo." // 6. Mensaje
                        + "#" + currentDate // 7. Fecha
                        + "#" + currentTime; // 8. Hora
            } else { // Invitaciones
                messageToServer = sharedStore.getProtocolMessages().getClientArgument(14) // C14 # ...
                        + "#" + sharedStore.getSeeMessage().getIdReceiver() // 1. Id Profesor
                        + "#" + sharedStore.getSeeMessage().getIdTeachersCourse() // 2. Id Curso
                        + "#" + sharedStore.getSeeMessage().getIdMessage() // 3. Id Mensaje (Para el Borrado)
                        + "#" + sharedStore.getSeeMessage().getIdSender() // 4. Id Alumno
                        + "#Invitación Aceptada." // 5. Asunto
                        + "#Se ha aceptado una invitación a uno de tus cursos, concediendole una plaza en el mismo. Puedes comprobarlo en la Seccion \"Registros Alumnos\" del Curso." // 6. Mensaje
                        + "#" + currentDate // 7. Fecha
                        + "#" + currentTime; // 8. Hora
            }

            sharedStore.setClientMessage(messageToServer);
            sharedStore.waitUntilResponse(true);
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("courses_lists");
        }
    }

    @FXML
    public void deleteMessage() {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necearia para el borrado.",
                "¿Estas seguro de que quieres borrar este Mensaje?")) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(15) // C15 # IdMensaje # isSender
                    + "#" + sharedStore.getSeeMessage().getIdMessage();
            if (sharedStore.getUser().getIdUser() == sharedStore.getSeeMessage().getIdSender()) {
                clientMessage += "#" + true;
            } else {
                clientMessage += "#" + false;
            }
            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait

            if (sharedStore.getApp().checkResponseResult()) {
                sharedStore.setSeeMessage(null);
                App.setRoot("messages_inbox");
            }
        }
    }

    @FXML
    public void replyMessage() {
        sharedStore.setComeback("message_read");
        User otherUser = new User(sharedStore.getSeeMessage().getIdSender(), sharedStore.getSeeMessage().getSenderReceiverName());
        sharedStore.setOtherUser(otherUser);
        App.setRoot("message_send");
    }

    public void read() {
        senderTextField.setText(sharedStore.getSeeMessage().getSenderReceiverName());
        subjectTextField.setText(sharedStore.getSeeMessage().getSubject());
        textTextField.setText(sharedStore.getSeeMessage().getText());

        if (sharedStore.getUser().getIdUser() != sharedStore.getSeeMessage().getIdSender()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(12)// Lee Mensaje en la BBDD -> C12 # IdMensaje
                    + "#" + sharedStore.getSeeMessage().getIdMessage();
            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        }
    }

    public void configComponents(){
        textTextField.setWrapText(true);

        if (!sharedStore.getSeeMessage().isReaded()) { // Lee Mensaje en el Lado Cliente
            sharedStore.getSeeMessage().setReaded(true);
            sharedStore.noReadMessagesMinusOne(); // Si no estaba leido -1
        }

        if (sharedStore.getComeback().equals("sent")) {
            inboxLabel.setText("Bandeja de Salida");
        } else {
            if (sharedStore.getNoReadMessages() > 0) {
                inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
            }
        }

        if (sharedStore.getSeeMessage().getType().equals("message")) {
            acceptRequestButton.setVisible(false);
            replyMessageButton.setVisible(true);
        } else if (sharedStore.getSeeMessage().getType().equals("request")) {
            replyMessageButton.setVisible(false);
            acceptRequestButton.setVisible(true);
        } else { // Invitación
            replyMessageButton.setVisible(false);
            acceptRequestButton.setVisible(true);
            acceptRequestButton.setText("Aceptar Invitación");
        }

        if (sharedStore.getUser().getIdUser() == sharedStore.getSeeMessage().getIdSender()) {
            senderReceiverLabel.setText("Destinatario");
            replyMessageButton.setText("Enviar otro Mensaje");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        configComponents();
        read();
    }

}
