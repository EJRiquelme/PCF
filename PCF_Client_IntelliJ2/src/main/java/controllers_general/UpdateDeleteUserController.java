package controllers_general;


import communication.ProtocolMessages;
import communication.SharedStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import objects.User;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;
import tools.Province;
import tools.Township;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class UpdateDeleteUserController implements Initializable {

    private final SharedStore sharedStore;

    private final ProtocolMessages messages;
    private List<String> lstFiles;

    private String name;
    private String password;
    private String email;
    private String confirmedPassword;
    private String shortPresentation;
    private String longPresentation;
    private String selectedProvince;
    private String selectedTownShip;

    public UpdateDeleteUserController() {
        this.sharedStore = SharedStore.getInstance();
        this.messages = new ProtocolMessages();
        this.lstFiles = new ArrayList();
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
    private ImageView photoImageView;
    private String photoPath;

    @FXML
    private Label passLabel;
    @FXML
    private PasswordField passField;
    @FXML
    private Label pass2Label;
    @FXML
    private PasswordField pass2Field;

    @FXML
    private ComboBox provincesComboBox;
    @FXML
    private ComboBox townshipsComboBox;
    @FXML
    private Label shortPLabel;
    @FXML
    private Label longPLabel;
    @FXML
    private TextArea shortPrTextArea;
    @FXML
    private TextArea longPrTextArea;

    @FXML
    private void switchToPerfil() throws IOException {
        App.setRoot("user_perfil");
    }

    @FXML
    public void searchImage(ActionEvent event) throws MalformedURLException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("Word Files", lstFiles));
        File f = fc.showOpenDialog(null);

        if (f != null) {
            photoPath = f.getPath();
            URL url = f.toURI().toURL();
            photoImageView.setImage(new Image(url.toExternalForm()));
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

    public void configComponents() {
        shortPrTextArea.setWrapText(true); // TextAreas multilineas
        longPrTextArea.setWrapText(true);
        shortPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 500 ? change : null)); // Limitación de caractéres
        longPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));

        nameTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación TextFields
        emailTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));

        passField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación PasswordFields
        pass2Field.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
    }

    @FXML
    public void register(ActionEvent event) throws IOException {
        name = nameTextField.getText();
        email = emailTextField.getText();
        password = passField.getText();
        confirmedPassword = pass2Field.getText();
        shortPresentation = shortPrTextArea.getText();
        longPresentation = longPrTextArea.getText();
        selectedProvince = provincesComboBox.getSelectionModel().getSelectedItem().toString();
        selectedTownShip = townshipsComboBox.getSelectionModel().getSelectedItem().toString();
        boolean photoExists;

        // Comprobación de Datos Opcionales
        photoExists = (!photoPath.equals("")); // Operador ternario: "resultado = (condicion)?valor1:valor2;"

        if (longPresentation.equals("")) {
            longPresentation = "null";
        }

        if (checkErrors()) {

            String clientMessage = messages.getClientArgument(5) + "#" // C5#noPhoto#Juanito Perez#jp@gmail...
                    + sharedStore.getUser().getIdUser() + "#"
                    + photoExists + "#"
                    + name + "#"
                    + email + "#"
                    + password + "#"
                    + selectedProvince + "#"
                    + selectedTownShip + "#"
                    + shortPresentation + "#"
                    + longPresentation;

            sharedStore.setUser(new User(photoExists, name, email, selectedProvince, selectedTownShip, shortPresentation, longPresentation)); // Preparación del Login en el lado Cliente

            if (photoExists) { // En caso de tener fotografía la prepara para enviarsela al Servidor, en caso contrario no
                //App.connectionToServer.updatingPhoto(photoPath);
                sharedStore.setFilePath(photoPath);
            } else {
                //App.connectionToServer.updatingPhoto("noPhoto");
                sharedStore.setFilePath("false");
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("search"); // Vuelve a la pantalla de Busquedas
        }

    }

    @FXML
    public void emailNotification() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ERROR");
        alert.setHeaderText("No es posible modificar el email.");
        alert.setContentText("Por motivos de seguridad e integridad de los archivos no es posible modificar la dirección de correo electrónico, "
                + "si desea cambiar de email deberá dar de baja la cuenta y crear una nueva.");
        alert.showAndWait();
    }

    @FXML
    public void deleteWarning() { // Para la baja
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necearia para el borrado.",
                "Realizar esta acción eliminará a su vez los Cursos que hayas creado.\n¿Estas seguro de que quieres eliminar la cuenta?")) {
            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(6)
                    + "#" + sharedStore.getUser().getEmail());
            sharedStore.waitUntilResponse(true); // wait()
            if(sharedStore.getApp().checkResponseResult()) {
                sharedStore.setUser(new User()); // Reseteamos el usuario
                sharedStore.getUser().setLogged(false); // "Cerramos sesión"
                sharedStore.setListening(false); // Condición de cierre del hilo de escucha
                App.setRoot("search"); // Vuelve a la pantalla de Busquedas
            }
        }
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

        if (!password.equals(confirmedPassword)) {
            alright = false;
            pass2Label.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Contraseña no coincidente.", pass2Label.getLayoutX(), pass2Label.getLayoutY());
        } else {
            pass2Label.setStyle("-fx-text-fill: black;");
        }

        if (shortPresentation.equals("") || !sharedStore.getApp().stringComponentsValidation(shortPresentation)) {
            alright = false;
            shortPLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación corta invalida.", shortPLabel.getLayoutX(), shortPLabel.getLayoutY());
        } else {
            shortPLabel.setStyle("-fx-text-fill: black;");
        }

        if (!sharedStore.getApp().allComponentsValidation(longPresentation)) {
            alright = false;
            longPLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación larga invalida.", longPLabel.getLayoutX(), longPLabel.getLayoutY());
        } else {
            longPLabel.setStyle("-fx-text-fill: black;");
        }

        return alright;
    }

    @FXML
    public void select(ActionEvent event) {
        String selected = provincesComboBox.getSelectionModel().getSelectedItem().toString();
        Set<Province> provinceList = sharedStore.getProvincesTownships().keySet();
        for (Province p : provinceList) {
            if (p.getName().equals(selected)) {
                fillTownshipComboBox(p);
            }
        }
    }

    public void fillProvinceComboBox() { // SharedStore -> ReadJson
        ObservableList<String> provinceList = FXCollections.observableArrayList(); // Colección de JavaFX: Con soporte para notificaciones de cambios, e invalidaciones.
        TreeMap<Province, TreeSet<Township>> provinceTownships = sharedStore.getProvincesTownships();

        for (Map.Entry<Province, TreeSet<Township>> entry : provinceTownships.entrySet()) {
            provinceList.add(entry.getKey().getName());
        }

        if (provinceList != null && !provinceList.isEmpty()) {
            provincesComboBox.setItems(provinceList);
            provincesComboBox.getSelectionModel().selectFirst();
        }
    }

    public void fillTownshipComboBox(Province province) { // SharedStore -> ReadJson
        ObservableList<String> townshipList = FXCollections.observableArrayList();
        TreeSet<Township> townships = sharedStore.getTownships(province);

        for (Township m : townships) {
            townshipList.add(m.getName());
        }

        if (townshipList != null && !townshipList.isEmpty()) {
            townshipsComboBox.setItems(townshipList);
            townshipsComboBox.getSelectionModel().selectFirst();
        }
    }

    private void showImage() {
        Image img = sharedStore.getPhoto();
        if (img != null) { // Por si los acasos
            photoImageView.setImage(img);
            centerImage();
        }
    }

    public void fillBaseData() {
        if (sharedStore.getUser().hasPhoto()) { // <- Info
            showImage();
        }
        nameTextField.setText(sharedStore.getUser().getName());

        provincesComboBox.getSelectionModel().select(sharedStore.getUser().getProvince());
        ;
        townshipsComboBox.getSelectionModel().select(sharedStore.getUser().getTownship());
        ;

        shortPrTextArea.setText(sharedStore.getUser().getShortPresentation());

        String longPr = sharedStore.getUser().getLongPresentation();
        if (longPr == null || longPr.equals("null")) {
            longPrTextArea.setText("");
        } else {
            longPrTextArea.setText(longPr);
        }

        emailTextField.setText(sharedStore.getUser().getEmail());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.photoPath = ""; // Restricción de los archivos aceptables en el Filechooser
        lstFiles.add("*.jpg");
        lstFiles.add("*.png");
        lstFiles.add("*.jpeg");

        sharedStore.readJsonFile();
        //sharedStore.readJsonUrl(); // <- Descartado por Lentitud: Al leer de la Web paraliza la aplicación entre 2 y 30 segundos

        fillProvinceComboBox(); // Relleno de Provincias y Municipios mediante Json
        Province firstProvince = sharedStore.getProvincesTownships().firstKey();
        fillTownshipComboBox(firstProvince);

        configComponents(); // Configuración de componentes (limitación de caractéres, TextAreas multilineales, etc.)
        fillBaseData();
    }
}
