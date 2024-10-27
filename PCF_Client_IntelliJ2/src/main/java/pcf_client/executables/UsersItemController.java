package pcf_client.executables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import objects.Unit;
import objects.User;

import java.io.IOException;

public class UsersItemController extends ListCell<User> {

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label titleLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(User unit, boolean empty) {
        super.updateItem(unit, empty);

        if (empty || unit == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("list_element3.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            titleLabel.setText(unit.getName());

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}