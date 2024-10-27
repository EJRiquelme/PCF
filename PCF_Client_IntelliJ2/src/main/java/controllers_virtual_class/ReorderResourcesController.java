package controllers_virtual_class;

import communication.SharedStore;
import pcf_client.executables.ResourcesItemController;
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
import pcf_client.executables.App;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ReorderResourcesController implements Initializable {

    private SharedStore sharedStore;

    private ObservableList<Resource> oldResourcesObservableList;
    private ObservableList<Resource> newResourcesObservableList;

    private LinkedList<Resource> oldResourcesList;
    private LinkedList<Resource> newResourcesList;

    public ReorderResourcesController() {
        this.sharedStore = SharedStore.getInstance();
        this.newResourcesList = new LinkedList();
        this.oldResourcesList = new LinkedList();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private ListView<Resource> oldOrderListView;

    @FXML
    private ListView<Resource> newOrderListView;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    public void toNewOrder() {
        Resource resource = oldOrderListView.getSelectionModel().getSelectedItem();

        if(resource != null) {
            newResourcesList.add(resource);
            oldResourcesList.remove(resource);

            //System.out.println("Nueva " + newResourcesList.size());
            //System.out.println("Vieja " + oldResourcesList.size());

            loadNewOrder();
            loadOldOrder();
        }
    }

    @FXML
    public void toOldOrder() {
        Resource resource = newOrderListView.getSelectionModel().getSelectedItem();

        if(resource != null) {
            newResourcesList.remove(resource);
            oldResourcesList.add(resource);

            //System.out.println("Nueva " + newResourcesList.size());
            //System.out.println("Vieja " + oldResourcesList.size());

            loadNewOrder();
            loadOldOrder();
        }
    }

    @FXML
    void saveNewOrder(ActionEvent event) {
        if (oldResourcesList.isEmpty()) {
            String clientMessage = sharedStore.getProtocolMessages().getClientArgument(48); // C48

            int newPos = 1;
            for (Resource u : newResourcesList) {
                clientMessage += "#" + u.getIdResource() + "#" + newPos;
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
                    "Recursos aún por asignar.",
                    "No has asignado una nueva posición a todos los Recursos");
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
        newResourcesObservableList = FXCollections.observableArrayList();
        newResourcesObservableList.addAll(newResourcesList);

        newOrderListView.setItems(newResourcesObservableList);
        newOrderListView.setCellFactory(new Callback<ListView<Resource>, ListCell<Resource>>() { // Muestro los Temas
            @Override
            public ListCell<Resource> call(ListView<Resource> usersListView) {
                return new ResourcesItemController();
            }
        });
    }

    public void loadOldOrder() {
        oldResourcesObservableList = FXCollections.observableArrayList();
        oldResourcesObservableList.addAll(oldResourcesList);

        oldOrderListView.setItems(oldResourcesObservableList);
        oldOrderListView.setCellFactory(new Callback<ListView<Resource>, ListCell<Resource>>() { // Muestro los Temas
            @Override
            public ListCell<Resource> call(ListView<Resource> usersListView) {
                return new ResourcesItemController();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        oldResourcesList = sharedStore.getActiveResources();
        loadOldOrder();
    }

}
