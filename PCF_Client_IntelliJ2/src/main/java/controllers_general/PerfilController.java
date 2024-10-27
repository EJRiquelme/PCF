package controllers_general;


import communication.SharedStore;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pcf_client.executables.App;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author william
 */
public class PerfilController implements Initializable {

    private final SharedStore sharedStore;

    public PerfilController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label nameLabel;
    @FXML
    private Label mailLabel;

    @FXML
    private Label provinceLabel;
    @FXML
    private Label townshipLabel;

    @FXML
    private TextArea shortPrTextArea;
    @FXML
    private TextArea longPrTextArea;

    @FXML
    private Button editButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button invitationButton;

    @FXML
    private void switchToSearch() {
        App.setRoot("search");
    }

    @FXML
    private void switchToInbox() {
        if (!sharedStore.getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION, "Login Necesario", "No estás Logeado.", "Debes logearte antes de poder acceder a tu Bandeja de Entrada.");
        } else {
            App.setRoot("messages_inbox");
        }
    }

    @FXML
    private void switchToCourses() {
        if (!sharedStore.getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION, "Login Necesario", "No estás Logeado.", "Debes logearte antes de poder acceder a tus Cursos.");
        } else {
            App.setRoot("courses_lists");
        }
    }

    @FXML
    private void switchToLoginPerfil() {
        if (sharedStore.getUser().isLogged()) { // Si Usuario esta Logeado
            if (sharedStore.getOtherUser().getIdUser() != sharedStore.getUser().getIdUser()) { // Y NO es el perfil del Usuario
                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4)
                        + "#" + sharedStore.getUser().getIdUser(); // "C4"#IdUser
                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                App.setRoot("user_perfil");
            }
        } else {
            App.setRoot("user_login");
        }
    }

    @FXML
    private void editPerfil() throws IOException {
        App.setRoot("user_update_delete");
    }

    @FXML
    private void logout() throws IOException {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(3)); // "C3"
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("search");
    }

    @FXML
    private void sendMessage() throws IOException {
        sharedStore.setComeback("user_perfil");
        if (sharedStore.getUser().isLogged()) {
            App.setRoot("message_send");
        } else {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                    "Login Necesario",
                    "No estás Logeado.",
                    "Debes logearte antes de poder enviar mensajes a otros Usuarios.");
        }
    }

    private void showImage() {
        Image img = sharedStore.getPhoto();
        if (img != null) { // Por si los Acasos
            photoImageView.setImage(img);
            centerImage();
        }
    }

    public void centerImage() { // Centrar Imagen
        Image img = photoImageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = photoImageView.getFitWidth() / img.getWidth();
            double ratioY = photoImageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            photoImageView.setX((photoImageView.getFitWidth() - w) / 2);
            photoImageView.setY((photoImageView.getFitHeight() - h) / 2);
        }
    }

    @FXML
    void sendInvitation() {
        String courseName = sharedStore.getApp().requestString("Enviar Invitación",
                "Escribe el titulo del curso al que deseas invitar a " + sharedStore.getOtherUser().getName(),
                "Titulo Curso:");
        if(!courseName.equals("cancel")) {
            if (!SharedStore.getInstance().getUser().isLogged()) {
                sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                        "Login Necesario",
                        "No estás Logeado.",
                        "Debes logearte antes de poder enviar una Invitación.");
            } else {
                LocalDate localDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String currentDate = localDate.format(dateFormatter);

                LocalTime localTime = LocalTime.now();
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String currentTime = localTime.format(timeFormatter);

                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(9) // C8 # Asunto # Mensaje # Fecha # Hora # tipoMensaje # idCurso # IdYo # Profesor
                        + "#" + courseName
                        + "#" + "Invitación para el Curso " + courseName
                        + "#" + "El usuario " + sharedStore.getUser().getName() + " te invita a una plaza en el Curso " + courseName + "."
                        + "#" + currentDate
                        + "#" + currentTime
                        + "#" + "invitation"
                        + "#" + sharedStore.getUser().getIdUser()
                        + "#" + sharedStore.getOtherUser().getIdUser();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait

                if(sharedStore.getApp().checkResponseResult()){
                    App.setRoot("search");
                }
            }
        }
    }

    public void configComponents() {
        shortPrTextArea.setWrapText(true); // TextAreas multilineas
        longPrTextArea.setWrapText(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }
        configComponents();

        if (sharedStore.getOtherUser().hasPhoto()) { // <- Info
            showImage();
        }
        nameLabel.setText(sharedStore.getOtherUser().getName());
        provinceLabel.setText(sharedStore.getOtherUser().getProvince());
        townshipLabel.setText(sharedStore.getOtherUser().getTownship());
        shortPrTextArea.setText(sharedStore.getOtherUser().getShortPresentation());

        String longPr = sharedStore.getOtherUser().getLongPresentation();
        if (longPr == null || longPr.equals("null")) {
            longPrTextArea.setText("");
        } else {
            longPrTextArea.setText(longPr);
        }

        if (sharedStore.getOtherUser().getIdUser() == sharedStore.getUser().getIdUser()) { // Si es el Perfil del Usuario
            loginPerfilLabel.setStyle("-fx-background-color: #6CAAE8;" + "-fx-font-weight: bold;");
            mailLabel.setText(sharedStore.getOtherUser().getEmail());
            sendMessageButton.setVisible(false);
            invitationButton.setVisible(false);
        } else {
            editButton.setVisible(false);
            logoutButton.setVisible(false);
            mailLabel.setVisible(false);
        }

    }

}
