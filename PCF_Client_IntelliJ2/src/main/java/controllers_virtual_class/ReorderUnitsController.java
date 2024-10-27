package controllers_virtual_class;

import communication.SharedStore;
import pcf_client.executables.UnitsItemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import objects.Resource;
import objects.Unit;
import pcf_client.executables.App;

import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;

public class ReorderUnitsController implements Initializable {

    private SharedStore sharedStore;

    private ObservableList<Unit> oldUnitsObservableList;
    private ObservableList<Unit> newUnitObservableList;

    private LinkedList<Unit> oldUnitList;
    private LinkedList<Unit> newUnitList;

    public ReorderUnitsController() {
        this.sharedStore = SharedStore.getInstance();
        this.newUnitList = new LinkedList();
        this.oldUnitList = new LinkedList();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private ListView<Unit> oldOrderListView;

    @FXML
    private ListView<Unit> newOrderListView;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    public void toNewOrder() {
        Unit unit = oldOrderListView.getSelectionModel().getSelectedItem();

        if(unit != null) {
            newUnitList.add(unit);
            oldUnitList.remove(unit);

            loadNewOrder();
            loadOldOrder();
        }
    }

    @FXML
    public void toOldOrder() {
        Unit unit = newOrderListView.getSelectionModel().getSelectedItem();

        if(unit != null) {
            newUnitList.remove(unit);
            oldUnitList.add(unit);

            loadNewOrder();
            loadOldOrder();
        }
    }

    @FXML
    void saveNewOrder(ActionEvent event) {
        if (oldUnitList.isEmpty()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(28); // C28#IdUser

            int newPos = 1;
            for (Unit u : newUnitList) {
                clientMessage += "#" + u.getIdUnit() + "#" + newPos;
                newPos++;
            }

            sharedStore.setClientMessage(clientMessage);
            sharedStore.waitUntilResponse(true); // wait()
            sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
            chargeVirtualClass();
        } else {
            sharedStore.getApp().alertToHuman(
                    Alert.AlertType.INFORMATION,
                    "ERROR",
                    "Temas aún por asignar.",
                    "No has asignado una nueva posición a todos los Temas");
        }
    }

    public void chargeVirtualClass() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(24) // C24 # IdCourse -> Para el Traspaso de archivos
                + "#" + sharedStore.getSelectedCourse().getIdCourse();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("virtual_class");
    }

    public void loadNewOrder() {
        newUnitObservableList = FXCollections.observableArrayList();
        newUnitObservableList.addAll(newUnitList);

        newOrderListView.setItems(newUnitObservableList);
        newOrderListView.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() { // Muestro los Temas
            @Override
            public ListCell<Unit> call(ListView<Unit> usersListView) {
                return new UnitsItemController();
            }
        });
    }

    public void loadOldOrder() {
        oldUnitsObservableList = FXCollections.observableArrayList();
        oldUnitsObservableList.addAll(oldUnitList);

        oldOrderListView.setItems(oldUnitsObservableList);
        oldOrderListView.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() { // Muestro los Temas
            @Override
            public ListCell<Unit> call(ListView<Unit> usersListView) {
                return new UnitsItemController();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());

        for (Map.Entry<Unit, LinkedList<Resource>> entry : sharedStore.getVirtualClassMap().entrySet()) { // Recojo los Temas
            oldUnitList.add(entry.getKey());
        }
        loadOldOrder();
    }

}
