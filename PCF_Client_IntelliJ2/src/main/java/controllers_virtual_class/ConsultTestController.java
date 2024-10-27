package controllers_virtual_class;

import communication.SharedStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import objects.ResourceHomework;
import objects.ResourceTest;
import pcf_client.executables.App;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ConsultTestController implements Initializable {
    private SharedStore sharedStore;

    public ConsultTestController() {
        sharedStore = SharedStore.getInstance();
    }

    @FXML
    private Label courseTitleLabel;

    @FXML
    private Label titleResourceLabel;
    @FXML
    private Label presentationLabel;

    @FXML
    private Button switchToVCButton;
    @FXML
    private Button solveTestButton;

    private String clientMessage;

    @FXML
    void switchToVirtualClass(ActionEvent event) {
        App.setRoot("virtual_class");
    }

    @FXML
    void switchToSolveTest(ActionEvent event) {

        if (controlDateTime()) {
            if (sharedStore.getApp().confirmDecision("CONFIRMACIÓN NECESARIA",
                    "Estás a punto de comenzar la resolución de un Test.",
                    "Debido al caracter aleatorio del mismo una vez comenzado\n" +
                            "solo tendrás esta oportunidad.\n" +
                            "¿Estas seguro de que quieres continuar?")) {
                clientMessage = sharedStore.getProtocolMessages().getClientArgument(54) // C54 # idRecurso
                        + "#" + sharedStore.getSelectedResource().getIdResource();

                sharedStore.setClientMessage(clientMessage);
                sharedStore.waitUntilResponse(true); // wait()
                if (sharedStore.getApp().checkResponseResult()) {
                    App.setRoot("vc_solve_test");
                }
            }
        } else {
            sharedStore.getApp().alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "Test cerrado.",
                    "No se puede iniciar el test por ahora.");
        }
    }

    public boolean controlDateTime() {
        boolean alright = false;

        ResourceTest resourceHomework = (ResourceTest) sharedStore.getSelectedResource();

        // Dia de apertura "ayer", Dia cierre "mañana"
        if (resourceHomework.getOpenDate().isBefore(LocalDate.now()) &&
                resourceHomework.getCloseDate().isAfter(LocalDate.now())) {
            alright = true;
        }

        // Dia de apertura "ayer", Dia cierre hoy, ¿Hora cierre pasada?
        if (resourceHomework.getOpenDate().isBefore(LocalDate.now()) &&
                resourceHomework.getCloseDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseTime().isAfter(LocalTime.now())) {
            alright = true;
        }

        // Apertura hoy y cierre "mañana", ¿hora?
        if (resourceHomework.getOpenDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseDate().isAfter(LocalDate.now()) &&
                resourceHomework.getOpenTime().isBefore(LocalTime.now())) {
            alright = true;
        }

        // Apertura y cierre hoy, ¿hora?
        if (resourceHomework.getOpenDate().isEqual(LocalDate.now()) &&
                resourceHomework.getCloseDate().isEqual(LocalDate.now()) &&
                resourceHomework.getOpenTime().isBefore(LocalTime.now()) &&
                resourceHomework.getCloseTime().isAfter(LocalTime.now())) {
            alright = true;
        }

        return alright;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        courseTitleLabel.setText(sharedStore.getSelectedCourse().getName());
        titleResourceLabel.setText(sharedStore.getSelectedResource().getTitleResource());
        presentationLabel.setText(sharedStore.getSelectedResource().getPresentation());


    }
}
