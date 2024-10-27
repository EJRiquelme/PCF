package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import objects.Resource;
import objects.ResourceTest;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CreateModifyDeleteTestController implements Initializable {

    private SharedStore sharedStore;

    private int idResource;
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int order;
    private boolean isHidden;
    private int idUnit;

    private LocalDate openDate;
    private LocalTime openTime;
    private LocalDate closeDate;
    private LocalTime closeTime;

    private String openDateString;
    private String closeDateString;
    private String openTimeString;
    private String closeTimeString;

    private int openHour;
    private int openMinute;
    private int closeHour;
    private int closeMinute;
    private String questionsQuantity;
    private String percentage;
    private int testsPercentage;

    private String clientMessage;

    public CreateModifyDeleteTestController() {
        this.sharedStore = SharedStore.getInstance();

        this.openHour = 0;
        this.openMinute = 0;
        this.closeHour = 0;
        this.closeMinute = 0;

        this.openDate = LocalDate.now();
        this.closeDate = LocalDate.now();

        this.openDateString = "";
        this.closeDateString = "";
        this.openTimeString = "";
        this.closeTimeString = "";

        this.percentage = "0";
        this.testsPercentage = sharedStore.getTestsPercentage();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private TextField titleResourceTextField;

    @FXML
    private Label questionsLabel;
    @FXML
    private TextField questionsTextField;

    @FXML
    private CheckBox hiddenCheckBox;

    @FXML
    private Label percentageLabel;
    @FXML
    private TextField percentageTextField;

    @FXML
    private AnchorPane openAnchorPane;

    @FXML
    private Label openDateLabel;
    @FXML
    private DatePicker openDatePicker;

    @FXML
    private Label openTimeLabel;
    @FXML
    private TextField openHourTextField;
    @FXML
    private TextField openMinuteTextField;

    @FXML
    private AnchorPane closeAnchorPane;

    @FXML
    private Label closeDateLabel;
    @FXML
    private DatePicker closeDatePicker;

    @FXML
    private Label closeTimeLabel;
    @FXML
    private TextField closeHourTextField;
    @FXML
    private TextField closeMinuteTextField;

    @FXML
    private Label presentationLabel;
    @FXML
    private TextArea presentationTextArea;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button deleteTestButton;
    @FXML
    private Button registerTestButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();

        if (sharedStore.getSelectedResource() == null) {
            App.setRoot("virtual_class");
        } else {
            int totalTestsPercentage = sharedStore.getTestsPercentage();
            sharedStore.setTestsPercentage(totalTestsPercentage + resourceTest.getPercentage());
            App.setRoot("vc_edit_test");
        }
    }

    public void rechargeVirtualClass() {
        clientMessage = sharedStore.getProtocolMessages().getClientArgument(24) // C24 # IdCourse -> Recargamos el Aula Virtual
                + "#" + sharedStore.getSelectedCourse().getIdCourse();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("virtual_class");
    }

    @FXML
    void deleteTest(ActionEvent event) {
        if (checkErrorsDelete()) {
            if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                    "Confirmación necesaria para el borrado.",
                    "Si activas esta opción el Test será eliminado,\n" +
                            "y con él todo registro de los alumnos que lo hayan rellenado.\n" +
                            "¿Estas seguro de que quieres borrar el Test?")) {

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(47) // C47 # idRecurso # tipoRecurso
                        + "#" + sharedStore.getSelectedResource().getIdResource()
                        + "#" + typeResource;

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()

                if (sharedStore.getApp().checkResponseResult()) {
                    sharedStore.setTestsPercentage(testsPercentage);
                    sharedStore.setSelectedResource(null);

                    rechargeVirtualClass();
                }
            }
        }
    }

    @FXML
    public void selectOpenDate(ActionEvent event) {
        openDate = openDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        openDateString = openDate.format(dateFormatter);
    }

    @FXML
    public void selectCloseDate(ActionEvent event) {
        closeDate = closeDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        closeDateString = closeDate.format(dateFormatter);
    }

    public String twoDigits(int num) {
        String hourMinute = String.valueOf(num);
        if (hourMinute.length() == 1) {
            hourMinute = "0" + hourMinute;
        }
        return hourMinute;
    }

    @FXML
    public void newTestControl(ActionEvent event) {
        if (sharedStore.getSelectedResource() == null) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                    "Imposible realizar este cambio",
                    "No se puede publicar un test vacío.",
                    "Para publicar el test este deberá tener al menos una pregunta.");
            if (!hiddenCheckBox.isSelected()) {
                hiddenCheckBox.setSelected(true);
            }
        } else {
            ResourceTest test = (ResourceTest) sharedStore.getSelectedResource();
            if (test.getTest().isEmpty()) {
                sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                        "Imposible realizar este cambio",
                        "No se puede publicar un test vacío.",
                        "Para publicar el test este deberá tener un mínimo de " + questionsQuantity + " preguntas.");
                if (!hiddenCheckBox.isSelected()) {
                    hiddenCheckBox.setSelected(true);
                }
            } else if (test.getTest().size() != test.getQuestionsQuantity()) {
                sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                        "Imposible realizar este cambio",
                        "No se puede publicar un test incompleto.",
                        "Para publicar el test este deberá tener un mínimo de " + questionsQuantity + " preguntas.");
                if (!hiddenCheckBox.isSelected()) {
                    hiddenCheckBox.setSelected(true);
                }
            }
        }
    }

    @FXML
    void registerTest(ActionEvent event) {
        titleResource = titleResourceTextField.getText();
        presentationResource = presentationTextArea.getText();
        typeResource = "test";
        isHidden = hiddenCheckBox.isSelected();
        idUnit = sharedStore.getSelectedUnit().getIdUnit();

        questionsQuantity = questionsTextField.getText();

        percentage = percentageTextField.getText();

        int openHour = Integer.parseInt(openHourTextField.getText());
        int openMinute = Integer.parseInt(openMinuteTextField.getText());

        int closeHour = Integer.parseInt(closeHourTextField.getText());
        int closeMinute = Integer.parseInt(closeMinuteTextField.getText());

        System.out.println(openHourTextField.getText());
        System.out.println(openMinuteTextField.getText());

        System.out.println(closeHourTextField.getText());
        System.out.println(closeMinuteTextField.getText());

        openTimeString = twoDigits(openHour) + ":" + twoDigits(openMinute) + ":00";
        closeTimeString = twoDigits(closeHour) + ":" + twoDigits(closeMinute) + ":00";

        System.out.println(openTimeString);
        System.out.println(closeTimeString);

        openTime = sharedStore.getConversions().convertStringToLocalTime(openTimeString);
        closeTime = sharedStore.getConversions().convertStringToLocalTime(closeTimeString);

        if (checkErrors()) {

            if (sharedStore.getSelectedResource() == null) { // INSERT
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(32) // C32
                        //+ "#" + idResource // No existe aun
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource
                        + "#" + isHidden
                        + "#" + idUnit
                        + "#" + questionsQuantity
                        + "#" + openDateString
                        + "#" + openTimeString
                        + "#" + closeDateString
                        + "#" + closeTimeString
                        + "#" + percentage;

            } else { // UPDATE
                idResource = sharedStore.getSelectedResource().getIdResource();

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(37) // C37
                        + "#" + idResource
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource // Es necesario xk updateResource es generico, y para Tareas es necesario
                        + "#" + isHidden
                        //+ "#" + idUnit // No es necesario
                        + "#" + questionsQuantity
                        + "#" + openDateString
                        + "#" + openTimeString
                        + "#" + closeDateString
                        + "#" + closeTimeString
                        + "#" + percentage;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()

            if (sharedStore.getApp().checkResponseResult()) { // Comprobación de Errores
                int totalTestsPercentage = sharedStore.getTestsPercentage();
                sharedStore.setTestsPercentage(totalTestsPercentage + Integer.parseInt(percentage));
                if (sharedStore.getSelectedResource() == null) {
                    rechargeVirtualClass();
                    App.setRoot("virtual_class");
                } else {
                    App.setRoot("vc_edit_test");
                }
            }
        }

    }

    public boolean checkErrorsDelete() {
        boolean alright = true;

        if (!sharedStore.getSelectedResource().isHidden()) {
            ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();

            // Dia de apertura "ayer", Dia cierre "mañana"
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede eliminar un test publicado y abierto.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede eliminar un test publicado y abierto.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede eliminar un test publicado y abierto.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede eliminar un test publicado y abierto.");
            }
        }
        return alright;
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;

        // Si el Alumno puede interactuar...
        //
        if (sharedStore.getSelectedResource() != null && !sharedStore.getSelectedResource().isHidden() && !isHidden) {
            ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();

            // Dia de apertura "ayer", Dia cierre "mañana"
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Test abierto.",
                        "No se puede modificar un test publicado y abierto.");
            }
            if (!alright) {
                loadData();
            }
        }

        if (alright) {
            if (titleResourceTextField.getText().equals("") || !sharedStore.getApp().stringComponentsValidation(titleResourceTextField.getText())) {
                alright = false;
                titleResourceLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Nombre inválido.", titleResourceLabel.getLayoutX(), titleResourceLabel.getLayoutY());
            } else {
                titleResourceLabel.setStyle("-fx-text-fill: black;");
            }

            if (presentationTextArea.getText().equals("") || !sharedStore.getApp().allComponentsValidation(presentationTextArea.getText())) {
                alright = false;
                presentationLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Presentación inválida.", presentationLabel.getLayoutX(), presentationLabel.getLayoutY());
            } else {
                presentationLabel.setStyle("-fx-text-fill: black;");
            }

            if (openDateString.equals("") || openDate.isBefore(LocalDate.now())) {
                alright = false;
                openDateLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Fecha inválida.",
                        openAnchorPane.getLayoutX() + openDateLabel.getLayoutX(),
                        openAnchorPane.getLayoutY() + openDateLabel.getLayoutY());
            } else {
                openDateLabel.setStyle("-fx-text-fill: black;");
            }

            if (closeDateString.equals("") || openDate.isAfter(closeDate)) {
                alright = false;
                closeDateLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Fecha inválida.",
                        closeAnchorPane.getLayoutX() + closeDateLabel.getLayoutX(),
                        closeAnchorPane.getLayoutY() + closeDateLabel.getLayoutY());
            } else {
                closeDateLabel.setStyle("-fx-text-fill: black;");
            }

            if (openDate.equals(closeDate) && openTime.equals(closeTime) || openDate.equals(closeDate) && openTime.isAfter(closeTime)) {
                alright = false;
                openTimeLabel.setStyle("-fx-text-fill: red;");
                closeTimeLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Hora inválida.",
                        openAnchorPane.getLayoutX() + openTimeLabel.getLayoutX(),
                        openAnchorPane.getLayoutY() + openTimeLabel.getLayoutY());
                sharedStore.getApp().errorPopup("Hora inválida.",
                        closeAnchorPane.getLayoutX() + closeTimeLabel.getLayoutX(),
                        closeAnchorPane.getLayoutY() + closeTimeLabel.getLayoutY());
            } else {
                openTimeLabel.setStyle("-fx-text-fill: black;");
                closeTimeLabel.setStyle("-fx-text-fill: black;");
            }

            if (sharedStore.getSelectedResource() != null) {
                ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();
                int questionQuantityInteger = Integer.parseInt(questionsQuantity);
                if (!isHidden && questionQuantityInteger > resourceTest.getTest().size()) { // La cantidad modificada debe existir
                    alright = false;
                    sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                            "ERROR",
                            "Test incompleto.",
                            "Para poder publicar el test este necesita\n" +
                                    "tenter un mínimo de " + questionsQuantity + " preguntas.");

                    hiddenCheckBox.setSelected(true);
                }
            }
        }

        return alright;
    }

    public void integerTextFormater() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?([0-9]*)?");
        // Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?"); // Para doubles
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<Integer> textFormatter = sharedStore.getApp().getIntegerTextFormatter(filter);
        questionsTextField.setTextFormatter(textFormatter);

        TextFormatter<Integer> textFormatter2 = sharedStore.getApp().getIntegerTextFormatter(filter);
        percentageTextField.setTextFormatter(textFormatter2);

        TextFormatter<Integer> textFormatter3 = sharedStore.getApp().getIntegerTextFormatter(filter);
        openHourTextField.setTextFormatter(textFormatter3);

        TextFormatter<Integer> textFormatter4 = sharedStore.getApp().getIntegerTextFormatter(filter);
        openMinuteTextField.setTextFormatter(textFormatter4);

        TextFormatter<Integer> textFormatter5 = sharedStore.getApp().getIntegerTextFormatter(filter);
        closeHourTextField.setTextFormatter(textFormatter5);

        TextFormatter<Integer> textFormatter6 = sharedStore.getApp().getIntegerTextFormatter(filter);
        closeMinuteTextField.setTextFormatter(textFormatter6);
    }

    private void looseQuestionsQuantityFocus() {
        questionsTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    if (questionsTextField.getText().isEmpty()) {
                        questionsTextField.setText("1");
                    }
                    int questionsQuantity = Integer.parseInt(questionsTextField.getText());

                    if (questionsQuantity > 50) {
                        questionsTextField.setText("50");
                    } else if (questionsQuantity < 1) {
                        questionsTextField.setText("1");
                    }
                }
            }
        });
    }

    private void loosePercentageFocus() {
        percentageTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    int allowedPercentage = Integer.parseInt(percentageTextField.getText());
                    int totalTestsPercentage = testsPercentage;

                    if ((totalTestsPercentage + allowedPercentage) > 100) {
                        int whatsLeft = (totalTestsPercentage + allowedPercentage) - 100;
                        int finalPercentage = allowedPercentage - whatsLeft;
                        percentageTextField.setText(String.valueOf(finalPercentage));
                    } else if (allowedPercentage < 0) {
                        percentageTextField.setText("0");
                    }
                }
            }
        });
    }

    private void looseHoursFocus() {
        TextField[] textFields = {openHourTextField, closeHourTextField};

        for (TextField textField : textFields) {
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (!newPropertyValue) {
                        int allowedHours = Integer.parseInt(textField.getText());

                        if (allowedHours > 23) {
                            textField.setText("23");
                        } else if (allowedHours < 0) {
                            textField.setText("0");
                        }
                    }
                }
            });
        }
    }

    private void looseMinutesFocus() {
        TextField[] textFields = {openMinuteTextField, closeMinuteTextField};

        for (TextField textField : textFields) {
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (!newPropertyValue) {
                        int allowedMinutes = Integer.parseInt(textField.getText());

                        if (allowedMinutes > 59) {
                            textField.setText("59");
                        } else if (allowedMinutes < 0) {
                            textField.setText("0");
                        }
                    }
                }
            });
        }
    }

    public void configComponents() {
        presentationTextArea.setWrapText(true);

        titleResourceTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 25 ? change : null)); // Limitación de caracteres en Título y Presentación
        presentationTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));

        if (sharedStore.getSelectedResource() != null) { // Si es UPDATE
            ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();
            int totalTestsPercentage = sharedStore.getTestsPercentage();
            sharedStore.setTestsPercentage(totalTestsPercentage - resourceTest.getPercentage());
        }

        integerTextFormater();
        looseQuestionsQuantityFocus();
        loosePercentageFocus();
        looseHoursFocus();
        looseMinutesFocus();

        questionsTextField.setText("1"); // ¿¡:S!?
        percentageTextField.setText("0");
        openHourTextField.setText("0");
        openMinuteTextField.setText("0");
        closeHourTextField.setText("0");
        closeMinuteTextField.setText("0");
    }

    public void loadData() { // UPDATE
        ResourceTest resourceTest = (ResourceTest) sharedStore.getSelectedResource();

        titleResourceTextField.setText(resourceTest.getTitleResource());
        presentationTextArea.setText(resourceTest.getPresentation());

        questionsQuantity = String.valueOf(resourceTest.getQuestionsQuantity());
        questionsTextField.setText(String.valueOf(resourceTest.getQuestionsQuantity()));

        hiddenCheckBox.setSelected(resourceTest.isHidden());

        percentageTextField.setText(String.valueOf(resourceTest.getPercentage()));

        testsPercentage -= resourceTest.getPercentage();

        openDate = resourceTest.getOpenDate();
        openDatePicker.setValue(openDate);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        openDateString = openDate.format(dateFormatter);

        openTime = resourceTest.getOpenTime();
        openHourTextField.setText(String.valueOf(openTime.getHour()));
        openMinuteTextField.setText(String.valueOf(openTime.getMinute()));

        closeDate = resourceTest.getCloseDate();
        closeDatePicker.setValue(closeDate);

        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        closeDateString = closeDate.format(dateFormatter);

        closeTime = resourceTest.getCloseTime();
        closeHourTextField.setText(String.valueOf(closeTime.getHour()));
        closeMinuteTextField.setText(String.valueOf(closeTime.getMinute()));

        registerTestButton.setText("Guardar cambios");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        configComponents();

        if (sharedStore.getSelectedResource() != null) { // UPDATE
            loadData();
        } else {
            deleteTestButton.setVisible(false);
        }
    }
}
