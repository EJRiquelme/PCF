package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import objects.ResourceFile;
import pcf_client.executables.App;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateModifyDeleteFileController implements Initializable {

    private SharedStore sharedStore;

    private int idResource;
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int order;
    private boolean isHidden;
    private int idUnit;

    private ResourceFile resourceFile;

    private File file;
    private List<String> listFiles; // .pdf, .docx, .odt, etc.

    private String clientMessage;

    public CreateModifyDeleteFileController() {
        this.sharedStore = SharedStore.getInstance();
        this.file = new File("");
        this.listFiles = new ArrayList();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private TextField titleResourceTextField;

    @FXML
    private Label fileLabel;
    @FXML
    private Button searchFileButton;
    @FXML
    private TextField fileTextField;

    @FXML
    private CheckBox hiddenCheckBox;

    @FXML
    private Label presentationLabel;
    @FXML
    private TextArea presentationTextArea;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button deleteFileButton;
    @FXML
    private Button uploadFileButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
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
    void deleteFile(ActionEvent event) {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necesaria para el borrado.",
                "Si activas esta opción el Archivo será eliminado.\n¿Estas seguro de que quieres borrar el Archivo?")) {

            clientMessage = sharedStore.getProtocolMessages().getClientArgument(47) // C47 # idRecurso
                    + "#" + sharedStore.getSelectedResource().getIdResource() // idRecurso
                    + "#" + sharedStore.getSelectedResource().getType() // tipo
                    + "#" + sharedStore.getSelectedResource().getIdUnit(); // idTema

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

            rechargeVirtualClass();
        }
    }

    @FXML
    void searchFile(ActionEvent event) {
        //try {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Files", listFiles));
        file = chooser.showOpenDialog(null);

        if (file != null) {
            Path path = Paths.get(file.getName());
            //if (Files.size(path) < 100000000) { // TODO: FUCK IT
            fileTextField.setText(file.getName());
            sharedStore.setFilePath(file.getPath());
                /*} else {
                    file = new File("");
                    sharedStore.getApp().alertToHuman(Alert.AlertType.WARNING,
                            "ERROR",
                            "Archivo demasiado grande.",
                            "El tamaño del archivo que se desea subir es excesivo.\n" +
                                    "El límite de tamaño para el traspaso de archivose es de 100mb.");
                }*/
        }

        /*} catch (IOException e) {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Error inesperado.",
                    "Se ha producido un error inesperado.");
            e.printStackTrace();
        }*/
    }

    @FXML
    void uploadFile(ActionEvent event) {
        titleResource = titleResourceTextField.getText();
        presentationResource = presentationTextArea.getText();
        typeResource = "file";
        isHidden = hiddenCheckBox.isSelected();
        idUnit = sharedStore.getSelectedUnit().getIdUnit();

        String fileName = file.getName();

        if (sharedStore.getSelectedResource() == null) { // INSERT
            if (checkErrorsCreate() && checkErrorsUpdate()) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(30) // C30 # tituloRecurso # presentación # tipoRecurso # oculto/no # idUnit # nombreArchivo
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource
                        + "#" + isHidden
                        + "#" + idUnit
                        + "#" + fileName;

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

                rechargeVirtualClass();
            }
        } else { // UPDATE
            if (checkErrorsUpdate()) {
                titleResource = titleResourceTextField.getText();
                presentationResource = presentationTextArea.getText();
                isHidden = hiddenCheckBox.isSelected();

                String newFileName = "noChanges";
                if (!sharedStore.getFilePath().equals("false")) { // Si ha pasado por -> searchFile() -> (f != null)
                    newFileName = file.getName();
                }

                clientMessage = sharedStore.getProtocolMessages().getClientArgument(35) // C35 # idRecurso # tituloRecurso # presentación # oculto/no # nombreArchivo/noChanges
                        + "#" + sharedStore.getSelectedResource().getIdResource()
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + isHidden
                        + "#" + resourceFile.getIdUnit()
                        + "#" + newFileName
                        + "#" + resourceFile.getFileName();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

                resourceFile.setTitleResource(titleResource);
                resourceFile.setPresentation(presentationResource);
                resourceFile.setHidden(isHidden);
                sharedStore.setSelectedResource(resourceFile); // Guardo los cambios en memoria

                if (!sharedStore.getFilePath().equals("false")) { // Si ha pasado por -> searchFile() -> (f != null)
                    resourceFile.setFileName(fileName);
                    sharedStore.setSelectedResource(resourceFile);

                    sharedStore.getApp().notificationsToHuman("Archivo " + fileName + " descargado correctamente.");
                }
                rechargeVirtualClass();
            }
        }
    }

    public boolean checkErrorsCreate() { // Notificaciones de TODOS los errores al Humano
        boolean alright = true;

        if (!file.exists()) {
            alright = false;
            fileLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Archivo no encontrado.", fileLabel.getLayoutX(), fileLabel.getLayoutY());
        } else {
            fileLabel.setStyle("-fx-text-fill: black;");
        }

        return alright;
    }

    public boolean checkErrorsUpdate() {
        boolean alright = true;

        if (titleResource.isEmpty() || !sharedStore.getApp().stringComponentsValidation(titleResource)) {
            alright = false;
            titleResourceLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Título inválido.", titleResourceLabel.getLayoutX(), titleResourceLabel.getLayoutY());
        } else {
            titleResourceLabel.setStyle("-fx-text-fill: black;");
        }

        if (presentationResource.isEmpty() || !sharedStore.getApp().allComponentsValidation(presentationResource)) {
            alright = false;
            presentationLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", presentationLabel.getLayoutX(), presentationLabel.getLayoutY());
        } else {
            presentationLabel.setStyle("-fx-text-fill: black;");
        }

        return alright;
    }

    public void configComponents() {
        this.listFiles.add("*.pdf"); // Tipos de archivos permitidos
        this.listFiles.add("*.docx");
        this.listFiles.add("*.odt");

        presentationTextArea.setWrapText(true);

        titleResourceTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null));
        presentationTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
    }

    public void loadData() {
        resourceFile = (ResourceFile) sharedStore.getSelectedResource();

        System.out.println(resourceFile.getFileName()); // TODO CHIVATO

        titleResourceTextField.setText(resourceFile.getTitleResource());
        presentationTextArea.setText(resourceFile.getPresentation());
        hiddenCheckBox.setSelected(resourceFile.isHidden());

        fileTextField.setText(resourceFile.getFileName());

        uploadFileButton.setText("Guardar cambios");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        configComponents();

        if (sharedStore.getSelectedResource() != null) { // Si hay un recurso seleccionado -> Modificar
            loadData();
        } else {
            deleteFileButton.setVisible(false);
        }
    }
}
