package controllers_general;


import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CoursePerfilController implements Initializable {

    private SharedStore sharedStore;

    public CoursePerfilController() {
        this.sharedStore = SharedStore.getInstance();
    }
    @FXML
    private Label loginPerfilLabel;
    @FXML
    private Label inboxLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private TextArea shortPrTextArea;
    @FXML
    private TextArea longPrTextArea;

    @FXML
    private Label initDateLabel;
    @FXML
    private Label endDateLabel;

    @FXML
    private Button sendRequestButton;

    @FXML
    private void switchToLoginPerfil() {
        if (!SharedStore.getInstance().getUser().isLogged()) {
            App.setRoot("user_login");
        } else {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                    + "#" + sharedStore.getUser().getIdUser(); // "C4"#IdUser
            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("user_perfil");
        }
    }

    @FXML
    void switchToCourses(MouseEvent event) {
        if (!SharedStore.getInstance().getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION, "Login Necesario", "No estás Logeado.", "Debes logearte antes de poder acceder a tus Cursos.");
        } else {
            App.setRoot("courses_lists");
        }
    }

    @FXML
    void switchToInbox(MouseEvent event) {
        if (!SharedStore.getInstance().getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION, "Login Necesario", "No estás Logeado.", "Debes logearte antes de poder acceder a tu Bandeja de Entrada.");
        } else {
            App.setRoot("messages_inbox");
        }
    }

    @FXML
    void switchToSearch(MouseEvent event) {
        App.setRoot("search");
    }

    @FXML
    void sendRequest(ActionEvent event) {
        if (!SharedStore.getInstance().getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                    "Login Necesario",
                    "No estás Logeado.",
                    "Debes logearte antes de poder enviar una Solicitud.");
        } else {
            if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                    "Confirmación necesaria para el envio.",
                    "Nos alegra que hayas optado por " + sharedStore.getSelectedCourse().getName() + ".\nPor favor confirma que realmente deseas acceder al Curso.")) {
                LocalDate localDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String currentDate = localDate.format(dateFormatter);

                LocalTime localTime = LocalTime.now();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String currentTime = localTime.format(timeFormatter);

                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(8) // C8 # Asunto # Mensaje # Fecha # Hora # tipoMensaje # idCurso # IdYo # Profesor
                        + "#" + "Solicitud para el Curso " + sharedStore.getSelectedCourse().getName()
                        + "#" + "El usuario " + sharedStore.getUser().getName() + " desea aplicar para una plaza en el Curso " + sharedStore.getSelectedCourse().getName() + "."
                        + "#" + currentDate
                        + "#" + currentTime
                        + "#" + "request"
                        + "#" + sharedStore.getSelectedCourse().getIdCourse()
                        + "#" + sharedStore.getUser().getIdUser()
                        + "#" + sharedStore.getSelectedCourse().getIdTeacher();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                App.setRoot("search");
            }
        }
    }

    public void configComponents() {
        shortPrTextArea.setWrapText(true); // TextAreas multilineas
        longPrTextArea.setWrapText(true);
    }

    public void loadData() {
        nameLabel.setText(sharedStore.getSelectedCourse().getName());
        shortPrTextArea.setText(sharedStore.getSelectedCourse().getShortPresentation());
        longPrTextArea.setText(sharedStore.getSelectedCourse().getLongPresentation());

        initDateLabel.setText(sharedStore.getConversions().convertLocalDateToString(sharedStore.getSelectedCourse().getStartDate()));
        endDateLabel.setText(sharedStore.getConversions().convertLocalDateToString(sharedStore.getSelectedCourse().getEndDate()));

        if (!sharedStore.getUser().isLogged()
                || sharedStore.getUser().getIdUser() == sharedStore.getSelectedCourse().getIdTeacher()) { // Si el usuario No esta logeado o es el Profesor
            sendRequestButton.setVisible(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }
        configComponents();
        loadData();
    }

}
