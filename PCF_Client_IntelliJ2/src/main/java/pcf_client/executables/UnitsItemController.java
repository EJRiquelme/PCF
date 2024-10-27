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
import objects.Unit;

import java.io.IOException;

/**
 *
 * @author william
 */
public class UnitsItemController extends ListCell<Unit> {

    @FXML
    private HBox graphicHBox;

    @FXML
    private Label titleLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(Unit unit, boolean empty) {
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

            titleLabel.setText(unit.getTitleUnit());

            if (unit.isHiddenUnit()) {
                titleLabel.setStyle("-fx-background-color: f2f2f9;"
                        + "-fx-text-fill: grey;");
            }

            setText(null);
            setGraphic(graphicHBox);
        }
    }
}
