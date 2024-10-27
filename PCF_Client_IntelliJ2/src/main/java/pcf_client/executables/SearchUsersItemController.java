/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pcf_client.executables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import objects.User;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author william
 */
public class SearchUsersItemController extends ListCell<User> {

    private User user;

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
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {

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

            this.user = user;

            nameLabel.setText(user.getName());
            nameLabel.setStyle("-fx-background-color: f8f8ff;" + "-fx-font-weight: bold;");
            presentationLabel.setText(user.getShortPresentation());
            provinceDateLabel.setText(user.getProvince());
            townshipDateHourLabel.setText(user.getTownship());

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
