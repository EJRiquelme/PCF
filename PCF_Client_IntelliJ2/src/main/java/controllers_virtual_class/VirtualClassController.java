package controllers_virtual_class;

import communication.SharedStore;
import pcf_client.executables.ResourcesItemController;
import pcf_client.executables.UnitsItemController;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import objects.Resource;
import objects.Unit;
import pcf_client.executables.App;


public class VirtualClassController implements Initializable {

    private SharedStore sharedStore;
    private ObservableList<Unit> unitObservableList;
    private ObservableList<Resource> resourceObservableList;

    public VirtualClassController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Button switchToCoursesListButton;
    @FXML
    private Button switchToResource;
    @FXML
    private Button reorderUnitsButton;
    @FXML
    private Button reorderResourcesButton;
    @FXML
    private Button newUnitButton;
    @FXML
    private Button configureButton;
    @FXML
    private Button newHomeworkButton;
    @FXML
    private Button newTestButton;
    @FXML
    private Button uploadArchiveButton;
    @FXML
    private Button uploadLinkButton;
    @FXML
    private Button endCourseButton;
    @FXML
    private Button studentRecordsButton;

    @FXML
    private ListView<Unit> unitsListView;
    @FXML
    private ListView<Resource> resourcesListView;

    @FXML
    void switchToCoursesList(ActionEvent event) {
        App.setRoot("courses_lists");
    }

    @FXML
    void switchToCreateUnit(ActionEvent event) {
        sharedStore.setSelectedUnit(null);
        sharedStore.setSelectedResource(null);
        App.setRoot("vc_create_unit");
    }

    @FXML
    void switchToConfigureUnit(ActionEvent event) {
        if (sharedStore.getSelectedUnit() != null) {
            sharedStore.setSelectedResource(null);
            App.setRoot("vc_create_unit");
        }
    }

    @FXML
    void switchToReorderUnits(ActionEvent event) {
        App.setRoot("vc_reorder_units");
    }

    // CREAR RECURSOS:
    //
    @FXML
    void switchToUploadFile(ActionEvent event) {
        if (sharedStore.getSelectedUnit() != null) { // Si hay una unidad seleccionada
            sharedStore.setSelectedResource(null); // Eliminamos de memoria cualquier recurso seleccionado (Crear != Modificar)
            App.setRoot("vc_create_archive");
        }
    }

    @FXML
    void switchToCreateLink(ActionEvent event) {
        if (unitsListView.getSelectionModel().getSelectedItem() != null) {
            sharedStore.setSelectedResource(null);
            App.setRoot("vc_create_link");
        }
    }

    @FXML
    void switchToCreateHomework(ActionEvent event) {
        if (sharedStore.getSelectedUnit() != null) {
            sharedStore.setSelectedResource(null);
            App.setRoot("vc_create_homework");
        }
    }

    @FXML
    void switchToCreateTest(ActionEvent event) {
        if (sharedStore.getSelectedUnit() != null) {
            sharedStore.setSelectedResource(null);
            App.setRoot("vc_create_test");
        }
    }

    @FXML
    void switchTOReorderResources(ActionEvent event) {
        if (sharedStore.getSelectedUnit() != null) {
            App.setRoot("vc_reorder_resources");
        }
    }

    // CONSULTAR / MODIFICAR / ELIMINAR RECURSOS
    //
    @FXML
    void switchToConsultResource(ActionEvent event) { // Rol Alumno (Y Profesor)
        if (sharedStore.getSelectedResource() != null) {
            String clientMessage = "";

            if (sharedStore.getSelectedResource().getType().equals("link")) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(39) // C39 # idRecurso
                        + "#" + sharedStore.getSelectedResource().getIdResource();
                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                App.setRoot("vc_see_link");

            } else if (sharedStore.getSelectedResource().getType().equals("file")) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(40) // C40 # idRecurso
                        + "#" + sharedStore.getSelectedResource().getIdResource();
                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                App.setRoot("vc_consult_file");

            } else if (sharedStore.getSelectedResource().getType().equals("exam")
                    || sharedStore.getSelectedResource().getType().equals("control")
                    || sharedStore.getSelectedResource().getType().equals("exercise")) {

                if (sharedStore.getUser().getIdUser() == sharedStore.getSelectedCourse().getIdTeacher()) { // TODO para pruebas, cambiar
                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(49) // C49 # idRecurso
                            + "#" + sharedStore.getSelectedResource().getIdResource();
                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()
                    sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                    App.setRoot("vc_correct_homework");
                } else {
                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(42) // C42 # idRecurso
                            + "#" + sharedStore.getSelectedResource().getIdResource();
                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()
                    sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                    App.setRoot("vc_homework_student");
                }

            } else if (sharedStore.getSelectedResource().getType().equals("test")) {
                if (sharedStore.getUser().getIdUser() == sharedStore.getSelectedCourse().getIdTeacher()) {
                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(44) // C44 # idRecurso
                            + "#" + sharedStore.getSelectedResource().getIdResource();
                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()
                    sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
                    App.setRoot("vc_edit_test");
                } else {
                    clientMessage = sharedStore.getProtocolMessages().getClientArgument(45) // C45 # idRecurso # tituloRecurso
                            + "#" + sharedStore.getSelectedResource().getIdResource()
                            + "#" + sharedStore.getSelectedResource().getTitleResource();

                    sharedStore.setClientMessage(clientMessage);
                    sharedStore.waitUntilResponse(true); // wait()
                    if (sharedStore.getApp().checkResponseResult()) {
                        App.setRoot("vc_consult_test");
                    }

                }
            }
        }

    }

    @FXML
    void switchToRecords() {
        App.setRoot("vc_students_records");
    }

    @FXML
    void switchToEndAndScores() {

    }

    public void configComponents() {
        if (sharedStore.getUser().getIdUser() != sharedStore.getSelectedCourse().getIdTeacher()) { // Si es alumno
            endCourseButton.setVisible(false);
            studentRecordsButton.setVisible(false);
            reorderUnitsButton.setVisible(false);
            reorderResourcesButton.setVisible(false);
            newUnitButton.setVisible(false);
            configureButton.setVisible(false);
            newHomeworkButton.setVisible(false);
            newTestButton.setVisible(false);
            uploadArchiveButton.setVisible(false);
            uploadLinkButton.setVisible(false);
        }
    }

    public void loadVirtualClassMap(LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap) {
        LinkedList<Unit> unitList = new LinkedList();

        for (Map.Entry<Unit, LinkedList<Resource>> entry : virtualClassMap.entrySet()) { // Recojo los Temas
            unitList.add(entry.getKey());
        }

        unitObservableList = FXCollections.observableArrayList();
        unitObservableList.addAll(unitList);

        unitsListView.setItems(unitObservableList);
        unitsListView.setCellFactory(new Callback<ListView<Unit>, ListCell<Unit>>() { // Muestro los Temas
            @Override
            public ListCell<Unit> call(ListView<Unit> usersListView) {
                return new UnitsItemController();
            }
        });

        // Arreglo del bug del Tema seleccionado en Memoria
        //
        if (sharedStore.getSelectedUnit() != null) { // Si hay un Tema seleccionado...
            Unit selectedUnit = null;

            for (Unit u : sharedStore.getVirtualClassMap().keySet()) {
                if (u.getIdUnit() == sharedStore.getSelectedUnit().getIdUnit()) { // Comprobamos si dicho Tema pertenece a ESTE curso
                    selectedUnit = u;
                }
            }
            if (selectedUnit != null) {
                unitsListView.getSelectionModel().select(selectedUnit);
                loadResourceList(virtualClassMap.get(selectedUnit));
            } else {
                sharedStore.setSelectedUnit(null); // Y si no pertenece lo eliminamos de memoria
                sharedStore.setSelectedResource(null);
            }
        }

        unitsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Unit>() {
            @Override
            public void changed(ObservableValue<? extends Unit> observable, Unit oldValue, Unit newValue) {
                sharedStore.setSelectedUnit(newValue); // Guardo la Unit que selecciono
                sharedStore.setActiveResources(sharedStore.getVirtualClassMap().get(newValue)); // Guardo en Memoria la lista de Recursos
                loadResourceList(virtualClassMap.get(newValue));
            }
        });
    }

    public void loadResourceList(LinkedList<Resource> list) {
        resourceObservableList = FXCollections.observableArrayList();
        resourceObservableList.addAll(list);

        resourcesListView.setItems(resourceObservableList);
        resourcesListView.setCellFactory(new Callback<ListView<Resource>, ListCell<Resource>>() {
            @Override
            public ListCell<Resource> call(ListView<Resource> usersListView) {
                return new ResourcesItemController();
            }
        });

        // TODO: Arreglo del bug del Recurso seleccionado en Memoria
        //
        if (sharedStore.getSelectedResource() != null) {
            Resource selectedResource = null;

            for (Resource r : list) {
                if (r.getIdResource() == sharedStore.getSelectedResource().getIdResource()) {
                    selectedResource = r;
                }
            }
            if (selectedResource != null) {
                resourcesListView.getSelectionModel().select(selectedResource);
            } else {
                sharedStore.setSelectedResource(null);
            }
        }

        resourcesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Resource>() {
            @Override
            public void changed(ObservableValue<? extends Resource> observable, Resource oldValue, Resource newValue) {
                sharedStore.setSelectedResource(newValue);
                // System.out.println("SIGUE FUNCIONANDO!!!"); // TODO chivato
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        configComponents();
        loadVirtualClassMap(sharedStore.getVirtualClassMap());
    }

}
