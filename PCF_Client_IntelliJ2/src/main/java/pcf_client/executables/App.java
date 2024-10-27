package pcf_client.executables;

import communication.ConnectionToServer;
import communication.SharedStore;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import tools.ResponseResult;

import java.io.*;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class App extends Application {
    public static FXMLLoader fxmlLoader;
    public static Scene scene;
    private String SERVER_HOST; // Comando ver I.P. Linux: "hostname -I"
    private final int SERVER_PORT = 4445;
    public static ConnectionToServer connectionToServer;
    public SharedStore sharedStore;

    public App() {
        this.sharedStore = SharedStore.getInstance();
        this.SERVER_HOST = readIP();
        this.SERVER_HOST = "127.0.0.1"; // TODO: Comentar para conectar por IP
    }

    @Override
    public void start(Stage stage) throws IOException {
        sharedStore.setApp(this); // Para los Controllers

        connectionToServer = new ConnectionToServer(SERVER_HOST, SERVER_PORT); // Se conecta al Servidor.
        connectionToServer.start(); // Crucemos los dedos

        scene = new Scene(loadFXML("search"));
        //scene = new Scene(loadFXML("vc_solve_test"));
        //stage.setTitle("Hello!");
        App.scene.getStylesheets().add(getClass().getResource("fx-listcell.css").toExternalForm());

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() { // Desconexión con el Servidor antes de cerrar
            @Override
            public void handle(WindowEvent e) {
                System.out.println("BYE!!!");

                sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(0)); // Finaliza ConnectionToServer y ClientServiceThread (Lado Servidor)
                sharedStore.setListening(false); // Para finalizar ListenServer

                sharedStore.waitUntilResponse(true); // <- wait() hasta que se envie el "C0" al Servidor

                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void setRoot(String fxml) {
        try {
            scene.setRoot(loadFXML(fxml));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public boolean allComponentsValidation(String text) { // El único carácter prohibido para el Humano en Carácteres de comunicación: (Cliente <-#-> Servidor)
        boolean alright = true;

        if (text.contains("#") || text.contains("<<<<<<<<<<") || text.contains(">>>>>>>>>>")) {
            alright = false;
        }

        return alright;
    }

    public boolean stringComponentsValidation(String text) {
        if (!text.isEmpty()) {
            if (text.charAt(0) == ' ' || text.charAt(0) == ',' || text.charAt(0) == '.') {
                return false;
            }
        }
        return text.matches("^[a-zA-ZÀ-úÜüÛû,.0-9\\s]+$");
    }

    public boolean integerComponentsValidation(String text) {
        return text.matches("^[0-9\\s]+$");
    }

    public TextFormatter<Integer> getIntegerTextFormatter(UnaryOperator<TextFormatter.Change> filter) {
        StringConverter<Integer> converter = new StringConverter<Integer>() {

            @Override
            public Integer fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0;
                } else {
                    return Integer.valueOf(s);
                }
            }

            @Override
            public String toString(Integer d) {
                return d.toString();
            }
        };

        return new TextFormatter<>(converter, 1, filter);
    }

    public TextFormatter<Double> getDoubleTextFormatter(UnaryOperator<TextFormatter.Change> filter) {
        StringConverter<Double> converter = new StringConverter<>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        return new TextFormatter<>(converter, 0.0, filter);
    }

    public boolean checkResponseResult() { // Errores, advertencias, notificaciones, etc.
        boolean alright = true;

        if (!sharedStore.isAnswered()) { // Hilo Timer
            communicationError();
        }

        if (sharedStore.getResponseResult().getResultType().equals("notification")) {
            notificationsToHuman(sharedStore.getResponseResult().getContent());
        }

        if (sharedStore.getResponseResult().getResultType().equals("alert")) {

            switch (sharedStore.getResponseResult().getAlertType()) {
                case "error":
                    alertToHuman(Alert.AlertType.ERROR,
                            sharedStore.getResponseResult().getTitle(),
                            sharedStore.getResponseResult().getHeader(),
                            sharedStore.getResponseResult().getContent());
                    alright = false;
                    break;
                case "information":
                    alertToHuman(Alert.AlertType.INFORMATION,
                            sharedStore.getResponseResult().getTitle(),
                            sharedStore.getResponseResult().getHeader(),
                            sharedStore.getResponseResult().getContent());
                    break;
            }
        }
        sharedStore.setResponseResult(new ResponseResult()); // Reset tras mostrarlo
        return alright;
    }

    public String requestString(String title, String heather, String content) {
        TextInputDialog textInputDialog = new TextInputDialog();

        textInputDialog.setGraphic(null);
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(heather);
        textInputDialog.setContentText(content);

        Optional<String> options = textInputDialog.showAndWait();

        TextField textField = textInputDialog.getEditor();
        String result = null;

        if (options.isPresent()) {
            if (!textField.getText().isEmpty()) {
                result = textField.getText();
            }
        } else{
            return "cancel";
        }
        return result;
    }

    public boolean confirmDecision(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public void notificationsToHuman(String message) { // Notificaciones de errores al Humano
        Stage stage = (Stage) App.scene.getWindow();
        Popup popup = new Popup();

        Label label = new Label(message);
        label.setStyle(" -fx-background-color: white;");
        popup.getContent().add(label);

        popup.setAutoHide(true);
        stage.setScene(App.scene);
        popup.show(stage);
    }

    public void alertToHuman(Alert.AlertType type, String notificationTitle, String notificationHeader, String notificationContent) { // Alertas
        Alert alert = new Alert(type);
        alert.setTitle(notificationTitle);
        alert.setHeaderText(notificationHeader);
        alert.setContentText(notificationContent);
        alert.showAndWait();
    }

    public void errorPopup(String errorMessage, double relativeWidth, double relativeHeight) { // "formato de email incorrecto", etc.
        Stage stage = (Stage) App.scene.getWindow();
        Popup popup = new Popup();

        Label label = new Label(errorMessage);
        label.setStyle(" -fx-background-color: yellow;");
        popup.getContent().add(label);

        double labelXpositionToScreen = stage.getX() + relativeWidth;
        double labelYpositionToScreen = stage.getY() + relativeHeight + 100;

        popup.setX(labelXpositionToScreen);
        popup.setY(labelYpositionToScreen);

        popup.setAutoHide(true);
        stage.setScene(App.scene);
        popup.show(stage);
    }

    public void communicationError() { // TimerThread
        alertToHuman(Alert.AlertType.ERROR,
                "ERROR",
                "Error de comunicación.",
                "Por motivos que escapan a nuestro control el Servidor está tardando demasiado en responder.\n"
                        + "Compruebe su conexión a Internet, y si no, sugerimos que cierre la aplicación y pruebe a conectarse más adelante.");
    }

    public String readIP() {
        try {
            String path = "/home/william/proyectoIP"; // <- Dirección IP del Servidor

            BufferedReader br = new BufferedReader(new FileReader(path));

            String ip = "";
            while ((ip = br.readLine()) != null) {
                return ip;
            }
        } catch (FileNotFoundException e) {
            alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "No es posible conocer la IP",
                    "No encuentro el archivo chivato donde debería estar la IP escrita.");
        } catch (IOException e) {
            alertToHuman(Alert.AlertType.ERROR,
                    "ERROR",
                    "No es posible conocer la IP",
                    "Ha ocurrido un error durante la lectura del archivo donde esta la IP escrita.");
        }
        return "mierda";
    }

    public static void main(String[] args) {
        launch();
    }
}