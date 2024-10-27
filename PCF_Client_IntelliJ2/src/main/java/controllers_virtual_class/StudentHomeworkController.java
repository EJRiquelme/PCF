package controllers_virtual_class;

import communication.SharedStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import objects.ArchiveHomework;
import objects.ResourceHomework;
import pcf_client.executables.App;
import pcf_client.executables.FilesItemController;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class StudentHomeworkController implements Initializable {
    private SharedStore sharedStore;

    private File file;
    private List<String> listFileTypes; // .pdf, .docx, .odt, etc.

    private LinkedList<ArchiveHomework> filesList;
    private LinkedList<ArchiveHomework> filesToDelete;
    private String clientMessage;

    public StudentHomeworkController() {
        this.sharedStore = SharedStore.getInstance();
        this.filesList = null;
        this.filesToDelete = new LinkedList<>();
        this.clientMessage = "";
    }

    @FXML
    private Label courseNameLabel;

    @FXML
    private Button searchFileButton;
    @FXML
    private Button deleteFileButton;
    @FXML
    private Button uploadFilesButton;

    @FXML
    private Button switchToVCButton;

    @FXML
    private ListView<ArchiveHomework> fileListView;

    @FXML
    private Label openDateTimeLabel;
    @FXML
    private Label closeDateTimeLabel;

    @FXML
    private Label titleLabel;
    @FXML
    private Label presentationLabel;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        sharedStore.setFilesHomework(new LinkedList<>());
        App.setRoot("virtual_class");
    }

    @FXML
    void searchFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        //fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Files", listFiles)); // Tipos de archivos génerico
        file = fc.showOpenDialog(null);

        if(filesList == null){
            filesList = new LinkedList<>();
        }

        boolean upload = true;
        if (file != null) {
            for (ArchiveHomework archiveHomework : filesList) {
                if (archiveHomework.getName().equals(file.getName())) {
                    upload = false;
                }
            }
            if (upload) {
                ArchiveHomework archiveHomework = new ArchiveHomework(-1, file.getPath(), file.getName());
                filesList.add(archiveHomework); // id = -1 -> Archivo a subir
                loadFilesList();
            } else {
                sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                        "ERROR",
                        "Archivo repetido.",
                        "No se puede subir un archivo más de una vez,\n" +
                                "si deseas sustituirlo debes eliminarlo de la lista\n" +
                                "y luego añadirlo a la misma.");
            }
        }
    }

    @FXML
    void deleteSelectedFile(ActionEvent event) {
        ArchiveHomework file = fileListView.getSelectionModel().getSelectedItem();

        if (file != null) {
            filesList.remove(file);

            if (file.getId() != -1) {
                filesToDelete.add(file); // Para el Update
            }

            loadFilesList();
        }
    }

    public void loadFilesList() {

        ObservableList<ArchiveHomework> filesObservableList = FXCollections.observableArrayList();
        filesObservableList.addAll(filesList);

        fileListView.setItems(filesObservableList);
        fileListView.setCellFactory(new Callback<ListView<ArchiveHomework>, ListCell<ArchiveHomework>>() { // Muestro los Archivos seleccionados
            @Override
            public ListCell<ArchiveHomework> call(ListView<ArchiveHomework> usersListView) {
                return new FilesItemController();
            }
        });

    }

    @FXML
    void uploadFiles(ActionEvent event) {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necesaria para la subida de archivos.",
                "¿Esta el " + sharedStore.getSelectedResource().getType() + " completado?\n" +
                        "De no ser así, recuerda que puedes cambiar los archivos subidos\n" +
                        "siempre y cuando el " + sharedStore.getSelectedResource().getType() + " siga abierto.")) {
            if (!filesList.isEmpty() || !filesToDelete.isEmpty()) {
                if (controlDateTime()) {
                    sharedStore.setFilesHomework(filesList); // Para el traspaso

                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(43) + // C43
                            "#" + sharedStore.getSelectedUnit().getIdUnit() + // idUnit
                            "#" + sharedStore.getSelectedResource().getIdResource();  // idRecurso

                    for (ArchiveHomework archiveHomework : filesToDelete) { // Primero los borrados, despues las subidas
                        clientMessage += "#" + archiveHomework.getId() + "#" + archiveHomework.getName();
                    }

                    for (ArchiveHomework archiveHomework : filesList) {
                        if (archiveHomework.getId() == -1) { // Los nuevos se suben, los antiguos no
                            clientMessage += "#" + archiveHomework.getId() + "#" + archiveHomework.getName(); // -1 = Subida
                        }
                    }

                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()
                    sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                    App.setRoot("virtual_class");
                }
            }
        }
    }

    public boolean controlDateTime() {
        boolean alright = false;

        ResourceHomework resourceHomework = (ResourceHomework) sharedStore.getSelectedResource();

        // Dia de apertura "ayer", Dia cierre "mañana"
        if (resourceHomework.getOpenDate().isBefore(LocalDate.now()) &&
                resourceHomework.getCloseDate().isAfter(LocalDate.now())) {
            alright = true;
        }

        // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
        if (resourceHomework.getOpenDate().isBefore(LocalDate.now()) &&
                resourceHomework.getCloseDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseTime().isAfter(LocalTime.now())) {
            alright = true;
        }

        // Apertura hoy y cierre "mañana", ¿hora?
        if (resourceHomework.getOpenDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseDate().isAfter(LocalDate.now()) &&
                resourceHomework.getOpenTime().isBefore(LocalTime.now())) {
            alright = true;
        }

        // Apertura y cierre hoy, ¿hora?
        if (resourceHomework.getOpenDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseDate().isEqual(LocalDate.now()) &&
                resourceHomework.getOpenTime().isBefore(LocalTime.now()) &&
                resourceHomework.getCloseTime().isAfter(LocalTime.now())) {
            alright = true;
        }

        if (!alright) {
            String type = "";
            if (resourceHomework.getType().equals("exercise")) {
                type = "Ejercicio";
            } else if (resourceHomework.getType().equals("control")) {
                type = "Control";
            } else {
                type = "Examen";
            }
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    type + " cerrado.",
                    "No se iniciar el " + type + " hasta que no este abierto.");
        }

        return alright;
    }

    public void configComponents() {
        presentationLabel.setWrapText(true);
    }

    public void loadData() {
        courseNameLabel.setText(sharedStore.getSelectedCourse().getName());

        ResourceHomework resourceHomework = (ResourceHomework) sharedStore.getSelectedResource();
        titleLabel.setText(resourceHomework.getTitleResource());
        String openDate = sharedStore.getConversions().convertLocalDateToString(resourceHomework.getOpenDate());
        String openTime = sharedStore.getConversions().convertLocalTimeToString(resourceHomework.getOpenTime());
        String closeDate = sharedStore.getConversions().convertLocalDateToString(resourceHomework.getCloseDate());
        String closeTime = sharedStore.getConversions().convertLocalTimeToString(resourceHomework.getCloseTime());
        openDateTimeLabel.setText("Inicio: " + openDate + " - " + openTime);
        closeDateTimeLabel.setText("Finalización: " + closeDate + " - " + closeTime);
        presentationLabel.setText(resourceHomework.getPresentation());

        filesList = sharedStore.getFilesHomework();
        loadFilesList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();

        loadData();

    }

}
