package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.web.WebView;
import objects.ResourceLink;
import pcf_client.executables.App;

public class ConsultLinkWebViewController implements Initializable {

    private SharedStore sharedStore;

    public ConsultLinkWebViewController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Button switchToVCButton;

    @FXML
    private Label titleLabel;

    @FXML
    private WebView vc_web_view;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());

        ResourceLink resourceLink = (ResourceLink) sharedStore.getSelectedResource();
        titleLabel.setText(resourceLink.getTitleResource());

        String urlFromServer = resourceLink.getUrlLink().replaceAll("<>","#");
        vc_web_view.getEngine().load(urlFromServer);
    }
}
