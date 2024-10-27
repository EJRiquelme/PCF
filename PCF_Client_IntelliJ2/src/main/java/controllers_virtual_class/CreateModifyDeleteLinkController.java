package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import objects.ResourceLink;
import pcf_client.executables.App;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class CreateModifyDeleteLinkController implements Initializable {

    private SharedStore sharedStore;

    private int idResource;
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int order;
    private boolean isHidden;
    private int idUnit;

    private String url;

    private String clientMessage;

    public CreateModifyDeleteLinkController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private TextField titleResourceTextField;

    @FXML
    private CheckBox hiddenCheckBox;

    @FXML
    private Label linkLabel;
    @FXML
    private TextField linkTextField;

    @FXML
    private Label presentationLabel;
    @FXML
    private TextArea presentationTextArea;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button deleteLinkButton;
    @FXML
    private Button registerLinkButton;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    public void rechargeVirtualClass() {
        clientMessage = sharedStore.getProtocolMessages().getClientArgument(24) // C24 # IdCourse
                + "#" + sharedStore.getSelectedCourse().getIdCourse();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("virtual_class");
    }

    @FXML
    void deleteLink(ActionEvent event) {
        if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                "Confirmación necesaria para el borrado.",
                "Si activas esta opción el Enlace será eliminado.\n¿Estas seguro de que quieres borrar el Enlace?")) {

            clientMessage = sharedStore.getProtocolMessages().getClientArgument(47) // C47 # idRecurso # tipoRecurso
                    + "#" + sharedStore.getSelectedResource().getIdResource()
                    + "#" + typeResource;

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            if(sharedStore.getApp().checkResponseResult()) {
                rechargeVirtualClass();
            }
        }
    }

    @FXML
    void registerLink(ActionEvent event) {
        titleResource = titleResourceTextField.getText();
        presentationResource = presentationTextArea.getText();
        typeResource = "link";
        isHidden = hiddenCheckBox.isSelected();
        idUnit = sharedStore.getSelectedUnit().getIdUnit();

        url = linkTextField.getText();

        if (checkErrors()) {

            String urlToServer = url.replaceAll("#","<>");

            if (sharedStore.getSelectedResource() == null) { // INSERT
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(29) // C29 # datos...
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + typeResource
                        + "#" + isHidden
                        + "#" + idUnit
                        + "#" + urlToServer;
            } else { // UPDATE
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(34) // C34 # idRecurso # datos...
                        + "#" + sharedStore.getSelectedResource().getIdResource()
                        + "#" + titleResource
                        + "#" + presentationResource
                        + "#" + isHidden
                        + "#" + urlToServer;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

            rechargeVirtualClass();
        }
    }

    public boolean checkErrors() {
        boolean alright = true;

        if (titleResource.isEmpty() || !sharedStore.getApp().stringComponentsValidation(titleResource)) {
            alright = false;
            titleResourceLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Título inválido.", titleResourceLabel.getLayoutX(), titleResourceLabel.getLayoutY());
        } else {
            titleResourceLabel.setStyle("-fx-text-fill: black;");
        }

        if (!urlValidation()) {
            alright = false;
            linkLabel.setStyle("-fx-text-fill: red;");
            sharedStore.getApp().errorPopup("Presentación inválida.", linkLabel.getLayoutX(), linkLabel.getLayoutY());
        } else {
            linkLabel.setStyle("-fx-text-fill: black;");
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

    public boolean urlValidation() {
        return Pattern.compile("^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$")
                .matcher(url)
                .matches();
    }

    public void configComponents() {
        presentationTextArea.setWrapText(true);

        titleResourceTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 50 ? change : null)); // Limitación de caracteres en Título y Presentación
        linkTextField.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
        presentationTextArea.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 1000 ? change : null));
    }

    public void loadData() { // UPDATE
        ResourceLink resourceLink = (ResourceLink) sharedStore.getSelectedResource();

        titleResourceTextField.setText(resourceLink.getTitleResource());
        presentationTextArea.setText(resourceLink.getPresentation());
        hiddenCheckBox.setSelected(resourceLink.isHidden());

        String urlFromServer = resourceLink.getUrlLink().replaceAll("<>","#");
        linkTextField.setText(urlFromServer);

        registerLinkButton.setText("Guardar cambios");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());

        if (sharedStore.getSelectedResource() != null) { // UPDATE
            loadData();
        } else {
            deleteLinkButton.setVisible(false); // Si lo esta creando, no puede eliminarlo
        }
    }
}
