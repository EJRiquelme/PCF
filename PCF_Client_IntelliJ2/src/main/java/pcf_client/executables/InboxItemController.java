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
import objects.Message;

import java.io.IOException;

/**
 *
 * @author william
 */
public class InboxItemController extends ListCell<Message> {

    private SharedStore sharedStore;

    public InboxItemController() {
        this.sharedStore = SharedStore.getInstance();
    }

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
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {

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

            nameLabel.setText(message.getSenderReceiverName());
            presentationLabel.setText(message.getSubject());
            provinceDateLabel.setText(sharedStore.getConversions().convertLocalDateToString(message.getSentDate()));
            townshipDateHourLabel.setText(sharedStore.getConversions().convertLocalTimeToString(message.getSentTime()));

            if (!message.isReaded()) {
                nameLabel.setStyle("-fx-background-color: f2f2f9;"
                        + "-fx-font-weight: bold;");
                presentationLabel.setStyle("-fx-background-color: ececec;"
                        + "-fx-font-weight: bold;");
                provinceDateLabel.setStyle("-fx-background-color: ececec;"
                        + "-fx-font-weight: bold;");
                townshipDateHourLabel.setStyle("-fx-background-color: ececec;"
                        + "-fx-font-weight: bold;");
                /*
                if (message.getType().equals("request")) {
                    nameLabel.setStyle("-fx-text-fill: blue"
                            + "-fx-background-color: f2f2f9;"
                            + "-fx-font-weight: bold;");
                    presentationLabel.setStyle("-fx-text-fill: blue"
                            + "-fx-background-color: ececec;"
                            + "-fx-font-weight: bold;");
                    provinceDateLabel.setStyle("-fx-text-fill: blue"
                            + "-fx-background-color: ececec;"
                            + "-fx-font-weight: bold;");
                    townshipDateHourLabel.setStyle("-fx-text-fill: blue"
                            + "-fx-background-color: ececec;"
                            + "-fx-font-weight: bold;");
                }
                 */
            }

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
