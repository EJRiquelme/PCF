package pcf_client.executables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import objects.TestQuestion;
import objects.Unit;

import java.io.IOException;

public class QuestionsItemController  extends ListCell<String> {

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label titleLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(String question, boolean empty) {
        super.updateItem(question, empty);

        if (empty || question == null) {
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

            titleLabel.setText(question);

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
