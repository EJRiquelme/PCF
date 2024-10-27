package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import objects.ResourceHomework;
import objects.ResourceTest;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CreateModifyDeleteHomeworkController implements Initializable {

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
    private String percentage;
    private int exercisesPercentage;
    private int controlsPercentage;
    private int examsPercentage;

    private String clientMessage;

    public CreateModifyDeleteHomeworkController() {
        this.sharedStore = SharedStore.getInstance();
        this.openHour = 0;
        this.openMinute = 0;
        this.closeHour = 0;
        this.closeMinute = 0;

        this.idResource = 0;
        this.titleResource = "";
        this.presentationResource = "";
        this.typeResource = "";
        this.order = 0;
        this.isHidden = true;
        this.idUnit = 0;

        this.openDate = LocalDate.now();
        this.openTime = LocalTime.now();
        this.closeDate = LocalDate.now();
        this.closeTime = LocalTime.now();
        this.openDateString = "";
        this.closeDateString = "";
        this.openTimeString = "";
        this.closeTimeString = "";
        this.percentage = "0";

        this.typeResource = "exercise";
        this.exercisesPercentage = sharedStore.getExercisesPercentage();
        this.controlsPercentage = sharedStore.getControlsPercentage();
        this.examsPercentage = sharedStore.getExamsPercentage();

        this.clientMessage = "";
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private TextField titleResourceTextField;

    @FXML
    private Label homeworkTypeLabel;
    @FXML
    private RadioButton exerciseRadioButton;
    @FXML
    private RadioButton controlRadioButton;
    @FXML
    private RadioButton examRadioButton;

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
    private Button deleteHomeworkButton;
    @FXML
    private Button registerHomeworkButton;

    /*
        @FXML
        private ToggleGroup type; // ¿?
    */
    @FXML
    void switchToVirtualClass(ActionEvent event) {
        if(sharedStore.getSelectedResource() == null) {
            App.setRoot("virtual_class");
        } else {
            App.setRoot("vc_correct_homework");
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
    void deleteHomework(ActionEvent event) {
        if (checkErrorsDelete()) {
            if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                    "Confirmación necesaria para el borrado.",
                    "¿Estas seguro de que quieres borrar la Tarea?")) {

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(47) // C47 #
                        + "#" + sharedStore.getSelectedResource().getIdResource() // idRecurso
                        + "#" + sharedStore.getSelectedResource().getType() // tipo
                        + "#" + sharedStore.getSelectedResource().getIdUnit(); // idTema

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getApp().checkResponseResult()) {
                    ResourceHomework resourceHomework = (ResourceHomework) sharedStore.getSelectedResource();

                    if (resourceHomework.getType().equals("exercise")) {
                        sharedStore.setExercisesPercentage(exercisesPercentage);
                    } else if (resourceHomework.getType().equals("control")) {
                        sharedStore.setControlsPercentage(controlsPercentage);
                    } else {
                        sharedStore.setExamsPercentage(examsPercentage);
                    }
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
    void registerHomework(ActionEvent event) {
        titleResource = titleResourceTextField.getText();
        presentationResource = presentationTextArea.getText();

        if (exerciseRadioButton.isSelected()) {
            typeResource = "exercise";
        } else if (controlRadioButton.isSelected()) {
            typeResource = "control";
        } else {
            typeResource = "exam";
        }

        isHidden = hiddenCheckBox.isSelected();
        idUnit = sharedStore.getSelectedUnit().getIdUnit();

        percentage = percentageTextField.getText();

        int openHour = Integer.parseInt(openHourTextField.getText());
        int openMinute = Integer.parseInt(openMinuteTextField.getText());

        int closeHour = Integer.parseInt(closeHourTextField.getText());
        int closeMinute = Integer.parseInt(closeMinuteTextField.getText());

        openTimeString = twoDigits(openHour) + ":" + twoDigits(openMinute) + ":00";
        closeTimeString = twoDigits(closeHour) + ":" + twoDigits(closeMinute) + ":00";

        openTime = sharedStore.getConversions().convertStringToLocalTime(openTimeString);
        closeTime = sharedStore.getConversions().convertStringToLocalTime(closeTimeString);

        if (checkErrors()) {

            if (sharedStore.getSelectedResource() == null) { // INSERT
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(31) // C31
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource
                        + "#" + isHidden
                        + "#" + idUnit
                        + "#" + openDateString
                        + "#" + openTimeString
                        + "#" + closeDateString
                        + "#" + closeTimeString
                        + "#" + percentage;

            } else { // UPDATE
                idResource = sharedStore.getSelectedResource().getIdResource();

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(36) // C36
                        + "#" + idResource
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource
                        + "#" + isHidden
                        + "#" + openDateString
                        + "#" + openTimeString
                        + "#" + closeDateString
                        + "#" + closeTimeString
                        + "#" + percentage;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()

            if (sharedStore.getApp().checkResponseResult()) { // Comprobación de Errores

                if (typeResource.equals("exercise")) {
                    sharedStore.setExercisesPercentage(exercisesPercentage + Integer.parseInt(percentage));
                } else if (typeResource.equals("control")) {
                    sharedStore.setControlsPercentage(controlsPercentage + Integer.parseInt(percentage));
                } else { // exam
                    sharedStore.setExamsPercentage(examsPercentage + Integer.parseInt(percentage));
                }

                if (sharedStore.getSelectedResource() == null) {
                    rechargeVirtualClass();
                    App.setRoot("virtual_class");
                } else {
                    App.setRoot("vc_correct_homework"); // TODO: ARREGLAR POSTERIORMENTE
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
                        "Tarea abierta.",
                        "No se puede eliminar una tarea publicada y abierta.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede eliminar una tarea publicada y abierta.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede eliminar una tarea publicada y abierta.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede eliminar una tarea publicada y abierta.");
            }
        }
        return alright;
    }

    public boolean checkErrors() {
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
                        "Tarea abierta.",
                        "No se puede modificar una tarea publicada y abierta.");
            }

            // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
            if (resourceTest.getOpenDate().isBefore(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede modificar una tarea publicada y abierta.");
            }

            // Apertura hoy y cierre "mañana", ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isAfter(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede modificar una tarea publicada y abierta.");
            }

            // Apertura y cierre hoy, ¿hora?
            if (resourceTest.getOpenDate().isEqual(LocalDate.now()) &&
                    resourceTest.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceTest.getOpenTime().isBefore(LocalTime.now()) &&
                    resourceTest.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Tarea abierta.",
                        "No se puede modificar una tarea publicada y abierta.");
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
        }
        return alright;
    }

    public void integerTextFieldTextFormater() {
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
        percentageTextField.setTextFormatter(textFormatter);

        TextFormatter<Integer> textFormatter2 = sharedStore.getApp().getIntegerTextFormatter(filter);
        openHourTextField.setTextFormatter(textFormatter2);

        TextFormatter<Integer> textFormatter3 = sharedStore.getApp().getIntegerTextFormatter(filter);
        openMinuteTextField.setTextFormatter(textFormatter3);

        TextFormatter<Integer> textFormatter4 = sharedStore.getApp().getIntegerTextFormatter(filter);
        closeHourTextField.setTextFormatter(textFormatter4);

        TextFormatter<Integer> textFormatter5 = sharedStore.getApp().getIntegerTextFormatter(filter);
        closeMinuteTextField.setTextFormatter(textFormatter5);
    }

    @FXML
    private void recalculatePercentage() {
        if (exerciseRadioButton.isSelected()) {
            typeResource = "exercise";
        } else if (controlRadioButton.isSelected()) {
            typeResource = "control";
        } else {
            typeResource = "exam";
        }

        int allowedPercentage = Integer.parseInt(percentageTextField.getText());
        int totalHomeworkPercentage = 0;

        if (typeResource.equals("exercise")) {
            totalHomeworkPercentage = exercisesPercentage;
        } else if (typeResource.equals("control")) {
            totalHomeworkPercentage = controlsPercentage;
        } else { // exam
            totalHomeworkPercentage = examsPercentage;
        }

        if ((totalHomeworkPercentage + allowedPercentage) > 100) {
            int whatsLeft = (totalHomeworkPercentage + allowedPercentage) - 100;
            int finalPercentage = allowedPercentage - whatsLeft;
            percentageTextField.setText(String.valueOf(finalPercentage));
        } else if (allowedPercentage < 0) {
            percentageTextField.setText("0");
        }
    }

    private void loosePercentageFocus() {
        percentageTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    int allowedPercentage = Integer.parseInt(percentageTextField.getText());
                    int totalHomeworkPercentage = 0;

                    if (typeResource.equals("exercise")) {
                        totalHomeworkPercentage = exercisesPercentage;
                    } else if (typeResource.equals("control")) {
                        totalHomeworkPercentage = controlsPercentage;
                    } else { // exam
                        totalHomeworkPercentage = examsPercentage;
                    }

                    if ((totalHomeworkPercentage + allowedPercentage) > 100) {
                        int whatsLeft = (totalHomeworkPercentage + allowedPercentage) - 100;
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

        titleResourceTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caractéres
        presentationTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));

        integerTextFieldTextFormater();
        loosePercentageFocus();
        looseHoursFocus();
        looseMinutesFocus();

        percentageTextField.setText("0");
        openHourTextField.setText("0");
        openMinuteTextField.setText("0");
        closeHourTextField.setText("0");
        closeMinuteTextField.setText("0");
    }

    public void loadData() { // UPDATE
        ResourceHomework resourceHomework = (ResourceHomework) sharedStore.getSelectedResource();

        titleResourceTextField.setText(resourceHomework.getTitleResource());
        presentationTextArea.setText(resourceHomework.getPresentation());

        exerciseRadioButton.setSelected(false);
        if (resourceHomework.getType().equals("exercise")) {
            exerciseRadioButton.setSelected(true);
        } else if (resourceHomework.getType().equals("control")) {
            controlRadioButton.setSelected(true);
        } else {
            examRadioButton.setSelected(true);
        }

        hiddenCheckBox.setSelected(resourceHomework.isHidden());

        percentageTextField.setText(String.valueOf(resourceHomework.getPercentage()));

        exercisesPercentage -= resourceHomework.getPercentage(); // Datos falsos
        controlsPercentage -= resourceHomework.getPercentage();
        examsPercentage -= resourceHomework.getPercentage();

        openDate = resourceHomework.getOpenDate();
        openDatePicker.setValue(openDate);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        openDateString = openDate.format(dateFormatter);

        openTime = resourceHomework.getOpenTime();
        openHourTextField.setText(String.valueOf(openTime.getHour()));
        openMinuteTextField.setText(String.valueOf(openTime.getMinute()));

        closeDate = resourceHomework.getCloseDate();
        closeDatePicker.setValue(closeDate);

        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        closeDateString = closeDate.format(dateFormatter);

        closeTime = resourceHomework.getCloseTime();
        closeHourTextField.setText(String.valueOf(closeTime.getHour()));
        closeMinuteTextField.setText(String.valueOf(closeTime.getMinute()));

        registerHomeworkButton.setText("Guardar cambios");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        configComponents();

        if (sharedStore.getSelectedResource() != null) {
            loadData();
        } else {
            deleteHomeworkButton.setVisible(false);
        }
    }

}
