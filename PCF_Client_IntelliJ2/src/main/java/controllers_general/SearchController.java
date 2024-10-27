package controllers_general;


import communication.SharedStore;
import javafx.event.ActionEvent;
import pcf_client.executables.SearchCoursesItemController;
import pcf_client.executables.SearchUsersItemController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.util.Callback;

import objects.Course;
import objects.User;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    private ObservableList<User> usersObservableList;
    private ObservableList<Course> coursesObservableList;

    private SharedStore sharedStore;
    private String[] searchOptions;
    private String name;
    private String tags;
    private String province;
    private String township;

    public SearchController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private ListView<User> searchMotorUsersListView;
    @FXML
    private ListView<Course> searchMotorCoursesListView;

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;

    @FXML
    private RadioButton coursesRadioButton;
    @FXML
    private RadioButton usersRadioButton;

    @FXML
    private Label nameLabel;
    @FXML
    private Label tagsLabel;
    @FXML
    private Label provinceLabel;
    @FXML
    private Label townshipLabel;

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField tagsTextField;
    @FXML
    private TextField provinceTextField;
    @FXML
    private TextField townshipTextField;

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
    private void switchToCourses() {

        if (!SharedStore.getInstance().getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(AlertType.INFORMATION,
                    "Login Necesario",
                    "No estás Logeado.",
                    "Debes logearte antes de poder acceder a tus Cursos.");
        } else {
            App.setRoot("courses_lists");
        }


    }

    @FXML
    private void switchToInbox() {

        if (!SharedStore.getInstance().getUser().isLogged()) {
            sharedStore.getApp().alertToHuman(AlertType.INFORMATION,
                    "Login Necesario",
                    "No estás Logeado.",
                    "Debes logearte antes de poder acceder a tu Bandeja de Entrada.");
        } else {
            App.setRoot("messages_inbox");
        }

    }

    @FXML
    private void chargeRadioButton() {
        if (coursesRadioButton.isSelected()) {
            nameLabel.setText("Nombre Curso");
            tagsLabel.setText("Categorías de Cursos");
            provinceLabel.setText("Provincia Profesor");
            townshipLabel.setText("Municipio Profesor");
        } else {
            nameLabel.setText("Nombre Usuario");
            tagsLabel.setText("Categorías que enseña");
            provinceLabel.setText("Provincia Usuario");
            townshipLabel.setText("Municipio Usuario");
        }
    }

    public void remember() { // Guardamos las opciones escogidas
        if (coursesRadioButton.isSelected()) {
            searchOptions[0] = "optionCourses";
        } else {
            searchOptions[0] = "optionUsers";
        }
        searchOptions[1] = nameTextField.getText();
        searchOptions[2] = tagsTextField.getText();
        searchOptions[3] = provinceTextField.getText();
        searchOptions[4] = townshipTextField.getText();

        sharedStore.setSearchOptions(searchOptions);
    }

    @FXML
    private void reloadList() {
        name = nameTextField.getText();
        tags = tagsTextField.getText();
        province = provinceTextField.getText();
        township = townshipTextField.getText();

        try {
            reColor(); // Por si había algún error
            if (!name.equals("") && !sharedStore.getApp().stringComponentsValidation(name)) {
                throw new ForbiddenCharacterException("Excepción: Carácter Prohibido.");
            }
            if (!tags.equals("") && !sharedStore.getApp().stringComponentsValidation(tags)) {
                throw new ForbiddenCharacterException("Excepción: Carácter Prohibido.");
            }
            if (!province.equals("") && !sharedStore.getApp().stringComponentsValidation(province)) {
                throw new ForbiddenCharacterException("Excepción: Carácter Prohibido.");
            }
            if (!township.equals("") && !sharedStore.getApp().stringComponentsValidation(township)) {
                throw new ForbiddenCharacterException("Excepción: Carácter Prohibido.");
            }

            String searchName;
            if (name.equals("")) {
                searchName = "none";
            } else {
                searchName = name;
            }
            String searchTags;
            if (tags.equals("")) {
                searchTags = "none";
            } else {
                searchTags = tags;
                searchTags = searchTags.toLowerCase(); // MySql no es case sensitive, peeero... por si acaso, que de sql es mejor no fiarse
                searchTags = searchTags.replaceAll(", ", "#"); // "idiomas, tecnologías" -> "idiomas#tecnologías"
                searchTags = searchTags.replaceAll(",", "#"); // "idiomas,tecnologías" -> "idiomas#tecnologías"
                searchTags = searchTags.replace(" ", "#"); // "idiomas tecnologías" -> "idiomas#tecnologías"
                searchTags = searchTags.replace("##", "#"); // Por si acaso
            }
            String searchProvince;
            if (province.equals("")) {
                searchProvince = "none";
            } else {
                searchProvince = province;
            }
            String searchTownship;
            if (township.equals("")) {
                searchTownship = "none";
            } else {
                searchTownship = township;
            }

            LocalDate localDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String currentDate = localDate.format(dateFormatter);

            String clientMessage;
            if (coursesRadioButton.isSelected()) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(22) // C22
                        + "#" + currentDate
                        + "#" + searchName
                        + "#" + searchProvince
                        + "#" + searchTownship
                        + "#" + searchTags;
            } else {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(21) // C21
                        + "#" + searchName
                        + "#" + searchProvince
                        + "#" + searchTownship
                        + "#" + searchTags;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

            if (coursesRadioButton.isSelected()) {
                LinkedList<Course> list = sharedStore.getCoursesList();
                loadCoursesList(list);
                searchMotorUsersListView.setVisible(false);
                searchMotorCoursesListView.setVisible(true);
            } else {
                LinkedList<User> list = sharedStore.getUsersList();
                loadUsersList(list);
                searchMotorCoursesListView.setVisible(false);
                searchMotorUsersListView.setVisible(true);
            }

            remember(); // Guardamos las opciones escogidas

        } catch (ForbiddenCharacterException fcex) {
            errorNotifications();
        }

    }

    public void reColor() {
        nameLabel.setStyle("-fx-text-fill: black;");
        tagsLabel.setStyle("-fx-text-fill: black;");
        provinceLabel.setStyle("-fx-text-fill: black;");
        townshipLabel.setStyle("-fx-text-fill: black;");
    }

    public void errorNotifications() {

        if (!name.equals("") && !sharedStore.getApp().stringComponentsValidation(name)) {
            nameLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Nombre inválido.", nameLabel.getLayoutX(), nameLabel.getLayoutY() + 90);
        }
        if (!tags.equals("") && !sharedStore.getApp().stringComponentsValidation(tags)) {
            tagsLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Categorías inválidas.", tagsLabel.getLayoutX(), tagsLabel.getLayoutY() + 90);
        }
        if (!province.equals("") && !sharedStore.getApp().stringComponentsValidation(province)) {
            provinceLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Provincia inválida.", provinceLabel.getLayoutX(), provinceLabel.getLayoutY() + 90);
        }
        if (!township.equals("") && !sharedStore.getApp().stringComponentsValidation(township)) {
            townshipLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Municipio inválido.", townshipLabel.getLayoutX(), townshipLabel.getLayoutY() + 90);
        }
    }

    @FXML
    private void looseFocus() {
        // Recargar
        searchMotorUsersListView.getSelectionModel().clearSelection();
    }

    private void loadCoursesList(LinkedList<Course> list) {
        coursesObservableList = FXCollections.observableArrayList();
        coursesObservableList.addAll(list);

        searchMotorCoursesListView.setItems(coursesObservableList);
        searchMotorCoursesListView.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override
            public ListCell<Course> call(ListView<Course> coursesListView) {
                return new SearchCoursesItemController();
            }
        });

        searchMotorCoursesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                sharedStore.setSelectedCourse(newValue);
                App.setRoot("course_perfil");
            }
        });
    }

    public void loadUsersList(LinkedList<User> list) {
        usersObservableList = FXCollections.observableArrayList();
        usersObservableList.addAll(list);

        searchMotorUsersListView.setItems(usersObservableList);
        searchMotorUsersListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> usersListView) {
                return new SearchUsersItemController();
            }
        });

        searchMotorUsersListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4) // "C4" # IdUser
                        + "#" + newValue.getIdUser();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                App.setRoot("user_perfil");
            }
        });
    }

    public void configComponents() {
        nameTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caractéres
        tagsTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
        provinceTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
        townshipTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }
        configComponents();
        searchOptions = sharedStore.getSearchOptions(); // Recuerdo de la Busqueda Anterior

        if (searchOptions[0].equals("optionCourses")) {
            coursesRadioButton.setSelected(true);
            usersRadioButton.setSelected(false);
        } else { // optionUsers
            coursesRadioButton.setSelected(false);
            usersRadioButton.setSelected(true);
        }
        chargeRadioButton();

        nameTextField.setText(sharedStore.getSearchOptions()[1]);
        tagsTextField.setText(sharedStore.getSearchOptions()[2]);
        provinceTextField.setText(sharedStore.getSearchOptions()[3]);
        townshipTextField.setText(sharedStore.getSearchOptions()[4]);

        reloadList();
    }

/*
    public void hoursTextFieldTextFormater() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?([0-9]*)?");

        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<Integer> textFormatter = sharedStore.getApp().getIntegerTextFormatter(filter);
        nameTextField.setTextFormatter(textFormatter);

        TextFormatter<Integer> textFormatter2 = sharedStore.getApp().getIntegerTextFormatter(filter);
        tagsTextField.setTextFormatter(textFormatter2);
    }

    private void looseHoursFocus() {
        TextField[] ts = {nameTextField,tagsTextField};

        for(TextField t: ts) {
            t.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (!newPropertyValue) {
                        if (t.getText().isEmpty()) {
                            t.setText("0");
                        }
                        int allowedPercentage = Integer.parseInt(t.getText());

                        if (allowedPercentage > 100) {
                            t.setText("100");
                        } else if (allowedPercentage < 0) {
                            t.setText("0");
                        }
                    }
                }
            });
        }
    }
 */
}
