package controllers_general;


import communication.SharedStore;
import pcf_client.executables.CoursesListItemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import objects.Course;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 *
 * @author william
 */
public class CoursesListController implements Initializable {

    private final SharedStore sharedStore;

    private ObservableList<Course> myCoursesObservableList;
    private ObservableList<Course> coursesIRecieveObservableList;

    public CoursesListController() {
        this.sharedStore = SharedStore.getInstance();
    }

    @FXML
    private ListView<Course> myCoursesListView;
    @FXML
    private ListView<Course> coursesReceivedListView;

    @FXML
    private Label inboxLabel;
    @FXML
    private Label loginPerfilLabel;

    @FXML
    private void switchToSearch() {
        App.setRoot("search");
    }

    @FXML
    private void switchToInbox() {
        App.setRoot("messages_inbox");
    }

    @FXML
    private void switchToLoginPerfil() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(4) // "C4" # IdUser
                + "#" + sharedStore.getUser().getIdUser();
        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("user_perfil");
    }

    @FXML
    public void switchToRegisterCourse() {
        App.setRoot("course_register");
    }

    public void openCourse() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(23) // "C23" # IdCourse
                + "#" + sharedStore.getSelectedCourse().getIdCourse();
        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
    }

    @FXML
    public void switchToVirtualClassStudent() {
        if (coursesReceivedListView.getSelectionModel().getSelectedItem() != null
                && coursesReceivedListView.getSelectionModel().getSelectedItems().size() == 1) {
            Course course = coursesReceivedListView.getSelectionModel().getSelectedItem();

            if (course.getStartDate().isBefore(LocalDate.now()) || course.getStartDate().isEqual(LocalDate.now())) { // Si fechaInicio <= Hoy
                if (course.getEndDate().isAfter(LocalDate.now()) || course.getEndDate().isEqual(LocalDate.now())) { // Y fechaFinal >= Hoy
                    sharedStore.setSelectedCourse(course);
                    if (course.isClosed()) {
                        openCourse(); // Apertura de Curso Automática
                    }
                    chargeVirtualClass(); // Cargo el Curso en el Lado Servidor

                } else { // Si fechaFinal < Hoy
                    sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                            "CURSO FINALIZADO",
                            "El Curso ya ha terminado.",
                            "Recibirás un correo con tu Nota del Curso una vez el Profesor haga las correcciones oportunas.");
                }
            } else { // Si fechaInicio > Hoy
                if (course.isClosed()) { // Y el Curso esta Cerrado
                    sharedStore.getApp().alertToHuman(Alert.AlertType.INFORMATION,
                            "CURSO CERRADO",
                            "El Curso aun no ha comenzado.",
                            "La fecha de inicio del Curso " + course.getName()
                                    + " es " + sharedStore.getConversions().convertLocalDateToString(course.getStartDate()));
                } else {
                    sharedStore.setSelectedCourse(course);
                    chargeVirtualClass(); // Cargo el Curso en el Lado Servidor
                }
            }

        }
    }

    public void showCoursesIRecieve(LinkedList<Course> list) {
        coursesIRecieveObservableList = FXCollections.observableArrayList();
        coursesIRecieveObservableList.addAll(list);

        coursesReceivedListView.setItems(coursesIRecieveObservableList);
        coursesReceivedListView.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override
            public ListCell<Course> call(ListView<Course> coursesListView) {
                return new CoursesListItemController();
            }
        });
    }

    @FXML
    public void updateCourse() {
        if (myCoursesListView.getSelectionModel().getSelectedItem() != null
                && myCoursesListView.getSelectionModel().getSelectedItems().size() == 1) {
            sharedStore.setSelectedCourse(null); // Por si acaso
            Course course = myCoursesListView.getSelectionModel().getSelectedItem();
            sharedStore.setSelectedCourse(course);
            App.setRoot("course_update_delete");
        }
    }

    public void chargeVirtualClass() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(24) // C24 # IdCourse
                + "#" + sharedStore.getSelectedCourse().getIdCourse();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
        App.setRoot("virtual_class");
    }

    @FXML
    public void switchToVirtualClassTeacher() {
        if (myCoursesListView.getSelectionModel().getSelectedItem() != null
                && myCoursesListView.getSelectionModel().getSelectedItems().size() == 1) {
            Course course = myCoursesListView.getSelectionModel().getSelectedItem();
            sharedStore.setSelectedCourse(course);
            chargeVirtualClass(); // Cargo el Curso en el Lado Servidor
        }
    }

    public void loadData() {
        String clientMessage = sharedStore.getProtocolMessages().getClientArgument(18) // C18#IdUser Cursos Impartidos
                + "#" + sharedStore.getUser().getIdUser();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores

        clientMessage = sharedStore.getProtocolMessages().getClientArgument(19) // C19#IdUser Cursos Recibiendo
                + "#" + sharedStore.getUser().getIdUser();

        sharedStore.setClientMessage(clientMessage);
        sharedStore.waitUntilResponse(true); // wait()
        sharedStore.getApp().checkResponseResult(); // Comprobación de Errores
    }

    @FXML
    public void noChangeColor() { // SI DA TIEMPO
        /*
        if (myCoursesListView.getSelectionModel().getSelectedItem() != null) {
           myCoursesListView.getSelectionModel().getSelectedItem();
        }
         */
    }

    public void showMyCourses(LinkedList<Course> list) {
        myCoursesObservableList = FXCollections.observableArrayList();
        myCoursesObservableList.addAll(list);

        myCoursesListView.setItems(myCoursesObservableList);
        myCoursesListView.setCellFactory(new Callback<ListView<Course>, ListCell<Course>>() {
            @Override
            public ListCell<Course> call(ListView<Course> coursesListView) {
                return new CoursesListItemController();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginPerfilLabel.setText(sharedStore.getUser().getName());
        if (sharedStore.getNoReadMessages() > 0) {
            inboxLabel.setText("Bandeja de Entrada (" + sharedStore.getNoReadMessages() + ")");
        }

        loadData();
        showCoursesIRecieve(sharedStore.getCoursesListStudent());
        showMyCourses(sharedStore.getCoursesListTeacher());
    }
}