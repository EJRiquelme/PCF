package controllers_virtual_class;

import communication.SharedStore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import objects.*;
import pcf_client.executables.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class CorrectHomeworkController implements Initializable {
    private SharedStore sharedStore;
    private ResourceHomework resourceHomework;
    private LinkedHashMap<User, LinkedList<ArchiveHomework>> studentFilesMap;

    private ObservableList<User> studentsObservableList;
    private ObservableList<ArchiveHomework> filesObservableList;

    private String clientMessage;

    public CorrectHomeworkController() {
        this.sharedStore = SharedStore.getInstance();
        this.studentFilesMap = new LinkedHashMap<>();
    }

    @FXML
    private Label courseNameLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label openDateTimeLabel;
    @FXML
    private Label closeDateTimeLabel;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button editHomeworkButton;

    @FXML
    private ListView<User> studentsListView;
    @FXML
    private ListView<ArchiveHomework> filesListView;

    @FXML
    private Button calificateButton;
    @FXML
    private TextField calificationTextField;

    @FXML
    private Button downloadFileButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    void editHomework(ActionEvent event) {
        App.setRoot("vc_create_homework");
    }

    @FXML
    void scoreStudentHomework(ActionEvent event) {
        if (dateTimeControl()) {
            if (studentsListView.getSelectionModel().getSelectedItem() != null) {
                double score = Double.parseDouble(calificationTextField.getText());

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(50) + // ASIGNAR NOTA -> 0.C50, 1.Score, 2.idStudent, 3.idHomework
                        "#" + score +
                        "#" + studentsListView.getSelectionModel().getSelectedItem().getIdUser() +
                        "#" + sharedStore.getSelectedResource().getIdResource();
                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getApp().checkResponseResult()) {
                    User user = studentsListView.getSelectionModel().getSelectedItem();
                    LinkedList<ArchiveHomework> files = studentFilesMap.get(user);
                    studentFilesMap.remove(user);
                    user.setScore(score);
                    studentFilesMap.put(user, files);
                    fillStudentsList();
                }
            }
        } else {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    resourceHomework.getType() + " abierto.",
                    "No se calificar el contenido hasta que no este cerrado u oculto.");
        }
    }

    @FXML
    void downloadFile(ActionEvent event) {
        if (dateTimeControl()) {
            ArchiveHomework archiveHomework = filesListView.getSelectionModel().getSelectedItem();

            if (archiveHomework != null) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(41) // C41
                        + "#" + "homeworkFile"
                        + "#" + archiveHomework.getId()
                        + "#" + archiveHomework.getName();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getApp().checkResponseResult()) {
                    sharedStore.getApp().notificationsToHuman("Descarga finalizada con éxito.");
                }
            }
        } else {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    resourceHomework.getType() + " abierto.",
                    "No se descargar el contenido hasta que no este cerrado u oculto.");
        }
    }

    public boolean dateTimeControl() {
        boolean alright = true;

        if (!resourceHomework.isHidden()) {
            // Dia de cierre "mañana" = No
            if (resourceHomework.getCloseDate().isAfter(LocalDate.now())) {
                alright = false;
            }

            // Dia cierre "hoy", hora "mas tarde" = No
            if (resourceHomework.getCloseDate().isEqual(LocalDate.now()) &&
                    resourceHomework.getCloseTime().isAfter(LocalTime.now())) {
                alright = false;
            }
        }
        return alright;
    }

    public void fillStudentsList() {
        LinkedList<User> unitList = new LinkedList();

        for (Map.Entry<User, LinkedList<ArchiveHomework>> entry : studentFilesMap.entrySet()) { // Recojo los Temas
            unitList.add(entry.getKey());
        }

        studentsObservableList = FXCollections.observableArrayList();
        studentsObservableList.addAll(unitList);

        studentsListView.setItems(studentsObservableList);
        studentsListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() { // Muestro los Temas
            @Override
            public ListCell<User> call(ListView<User> usersListView) {
                return new UsersItemController();
            }
        });

        studentsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                calificationTextField.setText(String.valueOf(newValue.getScore()));
                fillFilesList(studentFilesMap.get(newValue));
            }
        });
    }

    public void fillFilesList(LinkedList<ArchiveHomework> filesList) {
        filesObservableList = FXCollections.observableArrayList();
        filesObservableList.addAll(filesList);

        filesListView.setItems(filesObservableList);
        filesListView.setCellFactory(new Callback<ListView<ArchiveHomework>, ListCell<ArchiveHomework>>() { // Muestro los Archivos seleccionados
            @Override
            public ListCell<ArchiveHomework> call(ListView<ArchiveHomework> usersListView) {
                return new FilesItemController();
            }
        });
    }

    public void doubleTextFormater() {
        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?"); // Para doubles
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };

        TextFormatter<Double> textFormatter = sharedStore.getApp().getDoubleTextFormatter(filter);
        calificationTextField.setTextFormatter(textFormatter);
    }

    private void looseScoreFocus() {
        calificationTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (!newPropertyValue) {
                    double allowedCalifications = Double.parseDouble(calificationTextField.getText());

                    if (allowedCalifications > 10.0) {
                        calificationTextField.setText("10");
                    } else if (allowedCalifications < 0) {
                        calificationTextField.setText("0");
                    }
                }
            }
        });
    }

    public void configComponents() {
        doubleTextFormater();
        looseScoreFocus();
    }

    public void loadData() {
        courseNameLabel.setText(sharedStore.getSelectedCourse().getName());

        resourceHomework = (ResourceHomework) sharedStore.getSelectedResource();

        titleLabel.setText(resourceHomework.getTitleResource());
        String openDate = sharedStore.getConversions().convertLocalDateToString(resourceHomework.getOpenDate());
        String openTime = sharedStore.getConversions().convertLocalTimeToString(resourceHomework.getOpenTime());
        String closeDate = sharedStore.getConversions().convertLocalDateToString(resourceHomework.getCloseDate());
        String closeTime = sharedStore.getConversions().convertLocalTimeToString(resourceHomework.getCloseTime());
        openDateTimeLabel.setText("Inicio: " + openDate + " - " + openTime);
        closeDateTimeLabel.setText("Finalización: " + closeDate + " - " + closeTime);

        studentFilesMap = sharedStore.getStudentFilesMap();
        fillStudentsList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();
        loadData();
    }
}
