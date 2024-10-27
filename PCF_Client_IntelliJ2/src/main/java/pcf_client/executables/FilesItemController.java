package pcf_client.executables;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import objects.ArchiveHomework;

import java.io.IOException;

public class FilesItemController extends ListCell<ArchiveHomework> {

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label fileNameLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(ArchiveHomework file, boolean empty) {
        super.updateItem(file, empty);

        if (empty || file == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("list_element6.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileNameLabel.setText(file.getName());

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}