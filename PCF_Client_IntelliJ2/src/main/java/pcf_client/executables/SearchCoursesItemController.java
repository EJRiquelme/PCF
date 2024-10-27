/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pcf_client.executables;

import communication.SharedStore;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import objects.Course;

import java.io.IOException;

/**
 *
 * @author william
 */
public class SearchCoursesItemController extends ListCell<Course> {

    private SharedStore sharedStore;

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label nameLabel;
    @FXML
    private Label presentationLabel;
    @FXML
    private Label provinceDateLabel;
    @FXML
    private Label townshipDateHourLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Course course, boolean empty) {
        super.updateItem(course, empty);

        if (empty || course == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("list_element.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            sharedStore = SharedStore.getInstance();

            nameLabel.setText(course.getName());
            nameLabel.setStyle("-fx-background-color: f8f8ff;"
                    + "-fx-font-weight: bold;");
            presentationLabel.setText(course.getShortPresentation());
            provinceDateLabel.setText(sharedStore.getConversions().convertLocalDateToString(course.getStartDate()));
            townshipDateHourLabel.setText(sharedStore.getConversions().convertLocalDateToString(course.getEndDate()));

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
