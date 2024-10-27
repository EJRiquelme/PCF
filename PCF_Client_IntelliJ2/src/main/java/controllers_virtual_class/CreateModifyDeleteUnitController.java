package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import objects.Unit;
import pcf_client.executables.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CreateModifyDeleteUnitController implements Initializable {

    private SharedStore sharedStore;

    private int idUnit;
    private String titleUnit;
    private int orderUnit;
    private boolean hiddenUnit;
    private int percentageExercises;
    private int percentageControls;
    private int percentageExams;
    private int percentageTests;
    private int idCourse;

    public CreateModifyDeleteUnitController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleUnitTextField;

    @FXML
    private CheckBox hiddenCheckBox;

    @FXML
    private AnchorPane exercisesAnchorPane;
    @FXML
    private Label percentageExercisesLabel;
    @FXML
    private TextField percentageExercisesTextField;

    @FXML
    private AnchorPane controlsAnchorPane;
    @FXML
    private Label percentageControlsLabel;
    @FXML
    private TextField percentageControlsTextField;

    @FXML
    private AnchorPane examsAnchorPane;
    @FXML
    private Label percentageExamsLabel;
    @FXML
    private TextField percentageExamsTextField;

    @FXML
    private AnchorPane testsAnchorPane;
    @FXML
    private Label percentageTestsLabel;
    @FXML
    private TextField percentageTestsTextField;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button deleteUnitButton;
    @FXML
    private Button registerUnitButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    public void rechargeVirtualClass() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(24) // C24 # IdCourse
                + "#" + sharedStore.getSelectedCourse().getIdCourse();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        if(sharedStore.getApp().checkResponseResult()){
            sharedStore.setSelectedUnit(null);
            sharedStore.setSelectedResource(null);
            App.setRoot("virtual_class");
        }
    }

    @FXML
    void deleteUnit(ActionEvent event) {
        if(sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necesaria para el borrado.",
                "Si activas esta opción el Tema será eliminado,\n" +
                        "y con él todo el contenido del mismo.\n" +
                        "¿Estas seguro de que quieres borrar el Tema?")){
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(27) // C27 # IdTema
                    + "#" + sharedStore.getSelectedUnit().getIdUnit();

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()

            if (sharedStore.getApp().checkResponseResult()) {
                sharedStore.getVirtualClassMap().remove(sharedStore.getSelectedUnit()); // Elimino en el lado Cliente
                sharedStore.setSelectedResource(null); // Por si acaso
                sharedStore.setSelectedUnit(null);
                App.setRoot("virtual_class");
            }
        }
    }

    @FXML
    void registerUnit(ActionEvent event) {
        titleUnit = titleUnitTextField.getText();
        hiddenUnit = hiddenCheckBox.isSelected();
        percentageExercises = Integer.parseInt(percentageExercisesTextField.getText());
        percentageControls = Integer.parseInt(percentageControlsTextField.getText());
        percentageExams = Integer.parseInt(percentageExamsTextField.getText());
        percentageTests = Integer.parseInt(percentageTestsTextField.getText());

        if (checkErrors()) {
            String clientMessage = "";

            if (sharedStore.getSelectedUnit() == null) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(25) // C25 # ...
                        + "#" + titleUnit
                        + "#" + hiddenUnit
                        + "#" + percentageExercises
                        + "#" + percentageControls
                        + "#" + percentageExams
                        + "#" + percentageTests;
            } else {
                idUnit = sharedStore.getSelectedUnit().getIdUnit();

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(26) // C26 # ...
                        + "#" + idUnit
                        + "#" + titleUnit
                        + "#" + hiddenUnit
                        + "#" + percentageExercises
                        + "#" + percentageControls
                        + "#" + percentageExams
                        + "#" + percentageTests;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait

            if (sharedStore.getApp().checkResponseResult()) {
                rechargeVirtualClass();
            }
        }
    }

    public boolean checkErrors() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;

        if (!sharedStore.getVirtualClassMap().isEmpty()) {
            for (Unit unit : sharedStore.getVirtualClassMap().keySet()) {
                if (unit.getTitleUnit().equals(titleUnit) && !sharedStore.getSelectedUnit().getTitleUnit().equals(titleUnit)) {
                    alright = false;
                    titleLabel.setStyle("-fx-text-fill: red;");
                    sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                            "ERROR",
                            "Nombre repetido.",
                            "No puede haber dos Temas con el mismo nombre\nen el mismo curso.");
                }
            }
        }

        if (titleUnitTextField.getText().equals("") || !sharedStore.getApp().stringComponentsValidation(titleUnitTextField.getText())) {
            alright = false;
            titleLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Titulo inválido.", titleLabel.getLayoutX(), titleLabel.getLayoutY());
        } else {
            titleLabel.setStyle("-fx-text-fill: black;");
        }

        int totalPercentage = percentageExercises + percentageControls + percentageExams + percentageTests;
        if (totalPercentage != 100) {
            alright = false;
            percentageExercisesLabel.setStyle("-fx-text-fill: red;");
            percentageControlsLabel.setStyle("-fx-text-fill: red;");
            percentageExamsLabel.setStyle("-fx-text-fill: red;");
            percentageTestsLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Porcentajes inválidos.",
                    "Es preciso que los porcentajes de los componentes del Tema sumen 100.");
        } else{
            percentageExercisesLabel.setStyle("-fx-text-fill: black;");
            percentageControlsLabel.setStyle("-fx-text-fill: black;");
            percentageExamsLabel.setStyle("-fx-text-fill: black;");
            percentageTestsLabel.setStyle("-fx-text-fill: black;");
        }

        return alright;
    }

    public void integerTextFormater() {
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
        percentageExercisesTextField.setTextFormatter(textFormatter);

        TextFormatter<Integer> textFormatter2 = sharedStore.getApp().getIntegerTextFormatter(filter);
        percentageControlsTextField.setTextFormatter(textFormatter2);

        TextFormatter<Integer> textFormatter3 = sharedStore.getApp().getIntegerTextFormatter(filter);
        percentageExamsTextField.setTextFormatter(textFormatter3);

        TextFormatter<Integer> textFormatter4 = sharedStore.getApp().getIntegerTextFormatter(filter);
        percentageTestsTextField.setTextFormatter(textFormatter4);
    }

    private void loosePercentageFocus() {
        TextField[] textFields = {percentageExercisesTextField, percentageControlsTextField, percentageExamsTextField, percentageTestsTextField};

        for (TextField textField : textFields) {
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (!newPropertyValue) {
                        int allowedHours = Integer.parseInt(textField.getText());

                        if (allowedHours > 100) {
                            textField.setText("100");
                        } else if (allowedHours < 0) {
                            textField.setText("0");
                        }
                    }
                }
            });
        }
    }

    public void configComponents() {
        titleUnitTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caractéres
        integerTextFormater();
        loosePercentageFocus();
        percentageExercisesTextField.setText("25");
        percentageControlsTextField.setText("25");
        percentageExamsTextField.setText("25");
        percentageTestsTextField.setText("25");
    }

    public void loadData() {
        Unit unit = sharedStore.getSelectedUnit();
        titleUnitTextField.setText(unit.getTitleUnit());
        hiddenCheckBox.setSelected(unit.isHiddenUnit());
        percentageExercisesTextField.setText(String.valueOf(unit.getPercentageExercises()));
        percentageControlsTextField.setText(String.valueOf(unit.getPercentageControls()));
        percentageExamsTextField.setText(String.valueOf(unit.getPercentageExams()));
        percentageTestsTextField.setText(String.valueOf(unit.getPercentageTests()));

        registerUnitButton.setText("Guardar Cambios");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        configComponents();

        if (sharedStore.getSelectedUnit() != null) {
            loadData();
        } else {
            deleteUnitButton.setVisible(false);
        }
    }

}
