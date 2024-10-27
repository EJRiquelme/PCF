package controllers_general;


import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UpdateDeleteCourseController implements Initializable {

    private SharedStore sharedStore;

    private int idCourse;
    private String title;
    private String shortPresentation;
    private String longPresentation;
    private LocalDate startDate;
    private String startDateString;
    private LocalDate endDate;
    private String endDateString;
    private boolean hidden;
    private int idTeacher;
    private String tags;

    public UpdateDeleteCourseController() {
        this.sharedStore = SharedStore.getInstance();
        this.startDateString = "";
        this.endDateString = "";
    }

    @FXML
    private Label endDateLabel;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label longPrLabel;

    @FXML
    private Label tagsLabel;

    @FXML
    private TextArea longPrTextArea;

    @FXML
    private Label shortPrLabel;

    @FXML
    private TextArea shortPrTextArea;

    @FXML
    private Label strDateLabel;

    @FXML
    private DatePicker strDatePicker;

    @FXML
    private TextField tagsTextField;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField titleTextField;

    @FXML
    private CheckBox publishCheckBox;

    @FXML
    void switchToCourses(ActionEvent event) {
        App.setRoot("courses_lists");
    }

    @FXML
    void selectStrDate(ActionEvent event) {
        startDate = strDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDateString = startDate.format(dateFormatter);
    }

    @FXML
    void selectEndDate(ActionEvent event) {
        endDate = endDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        endDateString = endDate.format(dateFormatter);
    }

    @FXML
    void confirmPublication(ActionEvent event) {
        if (publishCheckBox.isSelected()) {
            if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                    "Confirmación necearia para la publicación.",
                    "Si activas esta opción el Curso será publicado.\n¿Estas seguro de que quieres publicar el Curso?")) {
                publishCheckBox.setSelected(true);
            } else {
                publishCheckBox.setSelected(false);
            }
        }
    }

    @FXML
    void deleteCourse(ActionEvent event) {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necearia para el borrado.",
                "Realizar esta acción eliminará a su vez todo el contenido del Curso.\n¿Estas seguro de que quieres eliminar el curso?")) {
            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(20) +
                    "#" + sharedStore.getSelectedCourse().getIdCourse());
            sharedStore.waitUntilResponse(true);
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("courses_lists");
        }
    }

    @FXML
    void updateCourse(ActionEvent event) {

        this.title = titleTextField.getText();
        this.shortPresentation = shortPrTextArea.getText();
        this.longPresentation = longPrTextArea.getText();

        this.hidden = !publishCheckBox.isSelected(); // Publicado = NoOculto
        this.idTeacher = sharedStore.getUser().getIdUser();
        this.tags = tagsTextField.getText();

        if (checkErrors()) {
            this.tags = tagFormat();

            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(17) // C17 # ...
                    + "#" + sharedStore.getSelectedCourse().getIdCourse() // 1. Id Curso
                    + "#" + title // 2. Titulo
                    + "#" + shortPresentation // 3. Presentación Corta
                    + "#" + longPresentation // 4. Presentación Larga
                    + "#" + startDateString // 5. Fecha Inicio
                    + "#" + endDateString // 6. Fecha Final
                    + "#" + hidden // 7. Oculto
                    + "#" + idTeacher // 8. Id Profesor
                    + "#" + tags); // 9. Tags?
            sharedStore.waitUntilResponse(true);
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            App.setRoot("courses_lists");
        }

    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;

        if (startDate.isAfter(endDate)) {
            alright = false;
            strDateLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Fecha inválida.", strDateLabel.getLayoutX(), strDateLabel.getLayoutY());
            endDateLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Fecha inválida.", endDateLabel.getLayoutX(), endDateLabel.getLayoutY());
        } else {
            if (startDate.isBefore(LocalDate.now()) || startDateString.equals("")) {
                alright = false;
                strDateLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Fecha inválida.", strDateLabel.getLayoutX(), strDateLabel.getLayoutY());
            } else {
                strDateLabel.setStyle("-fx-text-fill: black;");
            }
            if (endDate.isBefore(LocalDate.now()) || endDateString.equals("")) {
                alright = false;
                endDateLabel.setStyle("-fx-text-fill: red;");
                sharedStore.getApp().errorPopup("Fecha inválida.", endDateLabel.getLayoutX(), endDateLabel.getLayoutY());
            } else {
                endDateLabel.setStyle("-fx-text-fill: black;");
            }
        }

        if (title.equals("") || !sharedStore.getApp().stringComponentsValidation(title)) {
            alright = false;
            titleLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Titulo inválido.", titleLabel.getLayoutX(), titleLabel.getLayoutY());
        } else {
            titleLabel.setStyle("-fx-text-fill: black;");
        }

        if (shortPresentation.equals("") || !sharedStore.getApp().stringComponentsValidation(shortPresentation)) {
            alright = false;
            shortPrLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", shortPrLabel.getLayoutX(), shortPrLabel.getLayoutY());
        } else {
            shortPrLabel.setStyle("-fx-text-fill: black;");
        }

        if (!sharedStore.getApp().allComponentsValidation(longPresentation)) {
            alright = false;
            longPrLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", longPrLabel.getLayoutX(), longPrLabel.getLayoutY());
        } else {
            shortPrLabel.setStyle("-fx-text-fill: black;");
        }
        if (!sharedStore.getApp().stringComponentsValidation(tags) && !tags.equals("")) { // Debe poder estar vacío
            alright = false;
            tagsLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", tagsLabel.getLayoutX(), tagsLabel.getLayoutY());
        } else {
            shortPrLabel.setStyle("-fx-text-fill: black;");
        }

        return alright;
    }

    public void addTag() {
        // TODO
        String newTag = sharedStore.getApp().requestString("Nueva Etiqueta", "Introduzca nueva Etiqueta", "Etiqueta:");
        if (!newTag.equals("cancel")) {
            // Añadir a Lista de Etiquetas
        }
    }

    public void removeTag() {
        // Eliminar de Lista de Etiquetas
    }

    public String tagFormat() {
        String tgs = tagsTextField.getText();
        tgs = tgs.toLowerCase(); // MySql no es case sensitive, peeero... por si acaso, que de sql es mejor no fiarse
        tgs = tgs.replace(", ", "#");
        tgs = tgs.replace(" ", "#");
        tgs = tgs.replace(",", "#");
        tgs = tgs.replace("##", "#"); // Por si acaso

        return tgs;
    }

    public void configComponents() {
        shortPrTextArea.setWrapText(true); // Multilinea
        longPrTextArea.setWrapText(true);

        titleTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caractéres
        shortPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 500 ? change : null));
        longPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));

        tagsTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 100 ? change : null));
    }

    public void loadData() {
        strDatePicker.setValue(sharedStore.getSelectedCourse().getStartDate());
        startDate = strDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDateString = startDate.format(dateFormatter);

        endDatePicker.setValue(sharedStore.getSelectedCourse().getEndDate());
        endDate = endDatePicker.getValue();
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        endDateString = endDate.format(dateFormatter);

        titleTextField.setText(sharedStore.getSelectedCourse().getName());
        shortPrTextArea.setText(sharedStore.getSelectedCourse().getShortPresentation());
        longPrTextArea.setText(sharedStore.getSelectedCourse().getLongPresentation());
        publishCheckBox.setSelected(!sharedStore.getSelectedCourse().isHidden());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();
        loadData();
    }

}
