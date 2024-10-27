package controllers_general;


import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pcf_client.executables.App;
import tools.ForbiddenCharacterException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author william
 */
public class RegisterCourseController implements Initializable {

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

    public RegisterCourseController() {
        this.sharedStore = SharedStore.getInstance();
        this.startDateString = "";
        this.endDateString = "";

        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now();
    }

    @FXML
    private Label titleLabel;
    @FXML
    private Label shortPrLabel;
    @FXML
    private Label longPrLabel;
    @FXML
    private Label tagsLabel;

    @FXML
    private Label strDateLabel;
    @FXML
    private Label endDateLabel;

    @FXML
    private TextField titleTextField;
    @FXML
    private TextArea shortPrTextArea;
    @FXML
    private TextArea longPrTextArea;

    @FXML
    private DatePicker strDatePicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField tagsTextField;

    @FXML
    public void selectStrDate(ActionEvent event) {
        startDate = strDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDateString = startDate.format(dateFormatter);
    }

    @FXML
    public void selectEndDate(ActionEvent event) {
        endDate = endDatePicker.getValue();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        endDateString = endDate.format(dateFormatter);
    }

    @FXML
    public void switchToCourses() throws IOException {
        App.setRoot("courses_lists");
    }

    public void configComponents() {
        shortPrTextArea.setWrapText(true); // Multilinea
        longPrTextArea.setWrapText(true);

        titleTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caractéres
        tagsTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 100 ? change : null));
        shortPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 500 ? change : null));
        longPrTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
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

    @FXML
    public void registerCourse() throws IOException { // 0.C13, 1.NombreCurso, 2.PresentaciónCorta, 3.PresentaciónLarga, 4.FechaInicio, 5.FechaFinal, 6.IdProfesor, ¿7.Tag, ¿8.Tag, ¿9.Tag, ¿etc.

        this.title = titleTextField.getText();
        this.shortPresentation = shortPrTextArea.getText();
        this.longPresentation = longPrTextArea.getText();

        this.hidden = false;
        this.idTeacher = sharedStore.getUser().getIdUser();
        this.tags = tagsTextField.getText();

        if (checkErrors()) {

            this.tags = tagFormat();

            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(16) // C16#...
                    + "#" + title
                    + "#" + shortPresentation
                    + "#" + longPresentation
                    + "#" + startDateString
                    + "#" + endDateString
                    + "#" + idTeacher
                    + "#" + tags);
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

        if (!sharedStore.getApp().stringComponentsValidation(title) || title.equals("")) {
            alright = false;
            titleLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Titulo inválido.", titleLabel.getLayoutX(), titleLabel.getLayoutY());
        } else {
            titleLabel.setStyle("-fx-text-fill: black;");
        }

        if (shortPresentation.equals("") || !sharedStore.getApp().allComponentsValidation(shortPresentation)) {
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
            longPrLabel.setStyle("-fx-text-fill: black;");
        }

        if (!sharedStore.getApp().stringComponentsValidation(tags) && !tags.equals("")) { // Debe poder estar vacío
            alright = false;
            tagsLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", tagsLabel.getLayoutX(), tagsLabel.getLayoutY());
        } else {
            tagsLabel.setStyle("-fx-text-fill: black;");
        }
        return alright;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();
    }

}
