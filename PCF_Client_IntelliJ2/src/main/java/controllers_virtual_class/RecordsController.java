package controllers_virtual_class;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class RecordsController implements Initializable  {

    @FXML
    private Label recordCourseLabel;

    @FXML
    private ListView<?> recordsListView;

    @FXML
    private ListView<?> studentsListView;

    @FXML
    private Button switchToVAButton;

    @FXML
    void switchToVirtualAula(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

}
