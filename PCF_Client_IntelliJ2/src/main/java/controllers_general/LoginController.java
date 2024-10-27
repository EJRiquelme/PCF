package controllers_general;


import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @author william
 */
public class LoginController implements Initializable {

    private final SharedStore sharedStore;

    private String name;
    private String email;
    private String password;

    public LoginController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTextField;

    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;

    @FXML
    private Label passLabel;
    @FXML
    private TextField passField;

    @FXML
    private void switchToSearch() throws IOException {
        sharedStore.getApp().setRoot("search");
    }

    @FXML
    private void switchToRegister() throws IOException {
        sharedStore.getApp().setRoot("user_register");
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        name = nameTextField.getText();
        email = emailTextField.getText();
        password = passField.getText();

        if(checkErrors()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(2) + "#" + name + "#" + email + "#" + password; // C2 # Nombre # Email # Contraseña

            sharedStore.setClientMessage(clientMessage); // notify() al hilo TCP ConnectionToServer
            sharedStore.waitUntilResponse(true); // wait() al Controlador del hilo Gráfico

            if (sharedStore.getApp().checkResponseResult()) {
                App.setRoot("search");
            }
        }
    }

    public boolean emailValidation() { // Formato de email
        return Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email)
                .matches();
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;
        if (name.equals("") || !sharedStore.getApp().stringComponentsValidation(name)) {
            alright = false;
            nameLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Nombre inválido.", nameLabel.getLayoutX(), nameLabel.getLayoutY());
        } else {
            nameLabel.setStyle("-fx-text-fill: black;");
        }

        if (password.equals("") || !sharedStore.getApp().allComponentsValidation(password)) {
            alright = false;
            passLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Contraseña inválida.", passLabel.getLayoutX(), passLabel.getLayoutY());
        } else {
            passLabel.setStyle("-fx-text-fill: black;");
        }

        if (!emailValidation()) {
            alright = false;
            emailLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Email inválido.", emailLabel.getLayoutX(), emailLabel.getLayoutY());
        } else {
            emailLabel.setStyle("-fx-text-fill: black;");
        }
        return alright;
    }

    public void configComponents() {
        nameTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación TextFields
        emailTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
        passField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación PasswordFields
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configComponents();
    }

}
