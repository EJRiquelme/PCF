package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import objects.ResourceLink;
import pcf_client.executables.App;

import java.net.URL;
import java.util.ResourceBundle;


public class ConsultLinkController implements Initializable {

    private SharedStore sharedStore;

    public ConsultLinkController() {
        sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleLabel;
    @FXML
    private Label linkLabel;
    @FXML
    private Label presentationLabel;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button updateLinkButton;

    @FXML
    void updateLink(ActionEvent event) {
        App.setRoot("vc_create_link");
    }

    @FXML
    void openWeb() {
        App.setRoot("vc_web_view");
    }

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());

        ResourceLink resourceLink = (ResourceLink) sharedStore.getSelectedResource();
        titleLabel.setText(resourceLink.getTitleResource());
        linkLabel.setText(resourceLink.getUrlLink());
        presentationLabel.setText(resourceLink.getPresentation());

        if(sharedStore.getUser().getIdUser() != sharedStore.getSelectedCourse().getIdTeacher()){
            updateLinkButton.setVisible(false);
        }
    }
}
