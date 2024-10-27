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
import objects.Resource;
import objects.ResourceHomework;

import java.io.IOException;


/**
 * @author william
 */
public class ResourcesItemController extends ListCell<Resource> {

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label titleLabel;
    @FXML
    private Label typeLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Resource resource, boolean empty) {
        super.updateItem(resource, empty);

        if (empty || resource == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("list_element4.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String type = resource.getType();
            if (type.equals("exercise") || type.equals("control") || type.equals("exam")) {
                if (type.equals("exercise")) {
                    typeLabel.setText("Tarea:");
                } else if (type.equals("control")) {
                    typeLabel.setText("Control:");
                } else {
                    typeLabel.setText("examen:");
                }
            } else if (type.equals("archive")) {
                typeLabel.setText("Archivo:");
            } else if (type.equals("link")) {
                typeLabel.setText("Enlace:");
            } else if (type.equals("test")) {
                typeLabel.setText("Test:");
            }

            titleLabel.setText(resource.getTitleResource());

            if (resource.isHidden()) {
                typeLabel.setStyle("-fx-background-color: f4f4f4;"
                        + "-fx-text-fill: grey;");
                titleLabel.setStyle("-fx-background-color: f8f8ff;"
                        + "-fx-text-fill: grey;");
            }

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
