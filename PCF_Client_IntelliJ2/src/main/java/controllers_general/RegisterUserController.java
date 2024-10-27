package controllers_general;


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
import java.util.regex.Pattern;

public class RegisterUserController implements Initializable {

    private final SharedStore sharedStore;
    private List<String> listFiles; // .jpg, .png, .jpeg, etc.

    private String name;
    private String password;
    private String email;
    private String confirmedPassword;
    private String shortPresentation;
    private String longPresentation;
    private String selectedProvince;
    private String selectedTownShip;

    public RegisterUserController() {
        this.sharedStore = SharedStore.getInstance();
        this.listFiles = new ArrayList();
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
    private void switchToLogin() throws IOException {
        sharedStore.getApp().setRoot("user_login");
    }

    @FXML
    public void searchImage(ActionEvent event) throws MalformedURLException {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("Word Files", listFiles));
        File f = fc.showOpenDialog(null);

        if (f != null) {
            photoPath = f.getPath();
            URL url = f.toURI().toURL();
            photoImageView.setImage(new Image(url.toExternalForm()));

            //System.out.println("La ruta es: " + photoPath); // TODO Chivatos
            //System.out.println("La ruta es: " + f.getName());

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
    public void register(ActionEvent event) {

        name = nameTextField.getText();
        email = emailTextField.getText();
        password = passField.getText();
        confirmedPassword = pass2Field.getText();
        shortPresentation = shortPrTextArea.getText();
        longPresentation = longPrTextArea.getText();
        selectedProvince = provincesComboBox.getSelectionModel().getSelectedItem().toString();
        selectedTownShip = townshipsComboBox.getSelectionModel().getSelectedItem().toString();
        boolean photoExists;

        try {
            // Comprobación de Datos Opcionales
            photoExists = (!photoPath.equals("")); // Operador ternario: "resultado = (condicion)?valor1:valor2;"

            if (longPresentation.equals("")) {
                longPresentation = "noLongPresentation";
            }

            // Comprobación de Datos Obligatorios
            if (name.equals("") || name.equals("Administrador") || name.equals("Cliente Anónimo") || !sharedStore.getApp().stringComponentsValidation(name)) {
                throw new ForbiddenCharacterException("Error: Nombre inválido.");
            }
            if (!emailValidation()) {
                throw new ForbiddenCharacterException("Error: Email inválido.");
            }
            if (password.equals("") || !sharedStore.getApp().allComponentsValidation(password)) { // Acepta carácteres éxtraños (excepto '#')
                throw new ForbiddenCharacterException("Error: Contraseña inválida.");
            }
            if (!confirmedPassword.equals(password)) {
                throw new ForbiddenCharacterException("Error: Confirmación de contraseña erronea.");
            }
            if (shortPresentation.equals("") || !sharedStore.getApp().allComponentsValidation(shortPresentation)) {
                throw new ForbiddenCharacterException("Error: Presentación corta inválida.");
            }
            if (!sharedStore.getApp().allComponentsValidation(longPresentation)) { // Acepta carácteres éxtraños (excepto '#'), además puede estar vacio
                throw new ForbiddenCharacterException("Error: Presentación larga inválida.");
            }

            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(1) + "#" // C1 # noPhoto # Juanito Perez # jp@gmail...
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
                sharedStore.setFilePath(photoPath);
            } else {
                sharedStore.setFilePath("false");
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("search"); // Vuelve a la pantalla de Busquedas

        } catch (ForbiddenCharacterException ex) { // Excepción propia generada para limpiar código
            errorNotifications();
        }

    }

    public boolean emailValidation() { // Formato de email realista
        return Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
                .matcher(email)
                .matches();
    }

    public void errorNotifications() { // Notificaciones de TODOS los errores al Humano

        if (name.equals("") || !sharedStore.getApp().stringComponentsValidation(name)) {
            nameLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Nombre inválido.", nameLabel.getLayoutX(), nameLabel.getLayoutY());
        } else {
            nameLabel.setStyle("-fx-text-fill: black;");
        }

        if (password.equals("") || !sharedStore.getApp().allComponentsValidation(password)) {
            passLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Contraseña inválida.", passLabel.getLayoutX(), passLabel.getLayoutY());
        } else {
            passLabel.setStyle("-fx-text-fill: black;");
        }

        if (!password.equals(confirmedPassword)) {
            pass2Label.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Contraseña no coincidente.", pass2Label.getLayoutX(), pass2Label.getLayoutY());
        } else {
            pass2Label.setStyle("-fx-text-fill: black;");
        }

        if (!emailValidation()) {
            emailLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Email inválido.", emailLabel.getLayoutX(), emailLabel.getLayoutY());
        } else {
            emailLabel.setStyle("-fx-text-fill: black;");
        }

        if (shortPresentation.equals("") || !sharedStore.getApp().allComponentsValidation(shortPresentation)) {
            shortPLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación corta invalida.", shortPLabel.getLayoutX(), shortPLabel.getLayoutY());
        } else {
            shortPLabel.setStyle("-fx-text-fill: black;");
        }

        if (!sharedStore.getApp().allComponentsValidation(longPresentation)) {
            longPLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación larga invalida.", longPLabel.getLayoutX(), longPLabel.getLayoutY());
        } else {
            longPLabel.setStyle("-fx-text-fill: black;");
        }


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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.photoPath = ""; // Restricción de los archivos aceptables en el Filechooser
        this.listFiles.add("*.jpg");
        this.listFiles.add("*.png");
        this.listFiles.add("*.jpeg");

        sharedStore.readJsonFile();
        //sharedStore.readJsonUrl(); // <- Descartado por Lentitud: Al leer de la Web paraliza la aplicación entre 2 y 30 segundos

/*
        TreeMap <Province, TreeSet<Township>> joder = new TreeMap<>();
        TreeSet<Township> meCagoEnLa = new TreeSet<>();
        meCagoEnLa.add(new Township("0000","Me Cago En La..."));
        joder.put(new Province("00","Joder"),meCagoEnLa);
        sharedStore.setProvincesTownships(joder);
*/
        fillProvinceComboBox(); // Relleno de Provincias y Municipios mediante Json
        Province firstProvince = sharedStore.getProvincesTownships().firstKey();
        fillTownshipComboBox(firstProvince);

        configComponents(); // Configuración de componentes (limitación de caractéres, TextAreas multilineales, etc.)


    }
}
