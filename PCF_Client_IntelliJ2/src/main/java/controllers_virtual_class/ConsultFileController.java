package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import objects.ResourceFile;
import pcf_client.executables.App;

import java.net.URL;
import java.util.ResourceBundle;


public class ConsultFileController implements Initializable {

    private SharedStore sharedStore;

    private ResourceFile resourceFile;

    private String clientMessage;

    public ConsultFileController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseNameLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private Label presentationLabel;
    @FXML
    private Label fileNameLabel;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button updateFileButton;
    @FXML
    private Button downloadFileButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    public void updateFile() {
        App.setRoot("vc_create_archive");
    }

    @FXML
    public void downloadFile(ActionEvent event) {
        clientMessage = sharedStore.getProtocolMessages().getClientArgument(41) // C41 # IdRecurso -> Para el Traspaso de archivos
                + "#" + "file"
                + "#" + resourceFile.getIdResource()
                + "#" + resourceFile.getFileName();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        if(sharedStore.getApp().checkResponseResult()){
            sharedStore.getApp().notificationsToHuman("Descarga finalizada con éxito.");
        }
    }

    public void loadData() {
        courseNameLabel.setText(sharedStore.getSelectedCourse().getName());
        resourceFile = (ResourceFile) sharedStore.getSelectedResource();
        titleResourceLabel.setText(resourceFile.getTitleResource());
        presentationLabel.setText(resourceFile.getPresentation());
        fileNameLabel.setText("Archivo: " + resourceFile.getFileName());
    }

    public void configComponents() {
        presentationLabel.setWrapText(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configComponents();
        loadData();

        if (sharedStore.getUser().getIdUser() != sharedStore.getSelectedCourse().getIdTeacher()) {
            updateFileButton.setVisible(false);
        }
    }
}
