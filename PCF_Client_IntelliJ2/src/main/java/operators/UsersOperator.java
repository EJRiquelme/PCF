package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import javafx.scene.image.Image;
import objects.User;
import tools.ResponseResult;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class UsersOperator {

    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    private User user;

    private int idUser;
    private boolean hasPhoto;
    private String name;
    private String email;
    private String province;
    private String township;
    private String shortPresentation;
    private String longPresentation;

    public UsersOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    // REGISTRO ACEPTADO (Y "COMPLETADO" [DATOS BASE: Nombre, email, contraseña...])
    //
    //
    public void registrationAccepted(String[] serverArguments) { // S1 # idUser
        try {
            if (!sharedStore.getFilePath().equals("false")) { // Envia la Fotografía en caso de tenerla
                connectionToServer.sendArchive(sharedStore.getFilePath());
            }
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Archivo no encontrado.",
                        "No se ha podido localizar el archivo que se pretende enviar."));
            } else {
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error en la comunicación.",
                        "Se ha producido un error inesperado durante el envio."));
            }
        }

        sharedStore.getUser().setIdUser(Integer.parseInt(serverArguments[1])); // Login en el lado Cliente
        sharedStore.getUser().setLogged(true);

        listener = new ListenServer(sharedStore, connectionToServer.getServerHost(), connectionToServer.getServerPort()); // Inicia el hilo de escucha al Servidor por UDP
        listener.start();

        finalArguments = connectionToServer.receiveServerResponse().split("#"); // REGISTRO Y LOGIN FINALIZADO CON ÉXITO
    }

    // LOGIN ACEPTADO -> 0.S2, 1.IdUser, 2.hasPhoto?, 3.Nombre, 4.Email, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga, 9.MensajesNoLeídos
    //
    //
    public void loginAccepted(String[] serverArguments) {
        idUser = Integer.parseInt(serverArguments[1]);
        hasPhoto = Boolean.parseBoolean(serverArguments[2]);
        name = serverArguments[3];
        email = serverArguments[4];
        province = serverArguments[5];
        township = serverArguments[6];
        shortPresentation = serverArguments[7];
        longPresentation = serverArguments[8];
        int noReadMessages = Integer.parseInt(serverArguments[9]);

        user = new User(idUser, hasPhoto, name, email, province, township, shortPresentation, longPresentation); // Datos Usuario
        sharedStore.setUser(user);
        sharedStore.getUser().setLogged(true); // Logeado en el lado Cliente

        sharedStore.setNoReadMessages(noReadMessages);

        listener = new ListenServer(sharedStore, connectionToServer.getServerHost(), connectionToServer.getServerPort()); // Inicia el hilo de escucha al Servidor por UDP
        listener.start();
    }

    // UPDATE USER -> 0.S5, 1.sendPhoto?
    //
    //
    public void updateProfile(String[] serverArguments) {
        try {
            if (Boolean.parseBoolean(serverArguments[1])) { // Envia la Fotografía en caso de haberse cambiado
                connectionToServer.sendArchive(sharedStore.getFilePath());
            }
        } catch (IOException ex) {
            if (ex instanceof FileNotFoundException) { // Archivo no encontrado
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Archivo no encontrado.",
                        "No se ha podido localizar el archivo que se pretende enviar."));
            } else {
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error en la comunicación.",
                        "Se ha producido un error inesperado durante el envio."));
            }
        }
        finalArguments = connectionToServer.receiveServerResponse().split("#"); // 0.S5.0, 1.IdUser, 2.hasPhoto?, 3.Nombre, 4.Email, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga

        if (finalArguments[0].equals("S5.0")) {
            idUser = Integer.parseInt(finalArguments[1]);
            hasPhoto = Boolean.parseBoolean(finalArguments[2]);
            name = finalArguments[3];
            email = finalArguments[4];
            province = finalArguments[5];
            township = finalArguments[6];
            shortPresentation = finalArguments[7];
            longPresentation = finalArguments[8];

            sharedStore.setUser(new User(idUser, hasPhoto, name, email, province, township, shortPresentation, longPresentation)); // Actualizamos la información
            sharedStore.getUser().setLogged(true); // Por si acaso

        } else if (finalArguments[0].equals("S5.1")) {
            sharedStore.setResponseResult(new ResponseResult("alert",
                    "error",
                    "ERROR",
                    "Error: Error en la Base de Datos.",
                    "Se ha producido un error inesperado durante la actualización."));
        }
    }

    // VER PERFIL -> 0.S4, 1.HasPhoto?
    //
    //
    public void viewProfile(String[] serverArguments) {
        if (serverArguments[1].equals("true")) {
            byte[] buffer = connectionToServer.receiveArchivePreview();
            sharedStore.setPhoto(new Image(new ByteArrayInputStream(buffer)));
        }

        finalArguments = connectionToServer.receiveServerResponse().split("#"); // 0.S4.0, 1.IdUser, 2.hasPhoto?, 3.Nombre, 4.Email, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga

        idUser = Integer.parseInt(finalArguments[1]);
        hasPhoto = Boolean.parseBoolean(finalArguments[2]);
        name = finalArguments[3];
        email = finalArguments[4];
        province = finalArguments[5];
        township = finalArguments[6];
        shortPresentation = finalArguments[7];
        longPresentation = finalArguments[8];

        sharedStore.setOtherUser(new User(idUser, hasPhoto, name, email, province, township, shortPresentation, longPresentation));
    }

    // Motor de Busqueda -> Usuarios
    //
    //
    public void searchUsers(String[] serverArguments) {
        sharedStore.setUsersList(new LinkedList<User>()); // Reinicio la lista
        int usersQuantity = Integer.parseInt(serverArguments[1]); // Cantidad Usuarios recibidos

        for (int i = 0; i < usersQuantity; i++) { // Lectura de los Usuarios, uno a uno
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idUser = Integer.parseInt(finalArguments[0]);
            hasPhoto = Boolean.parseBoolean(finalArguments[1]);
            name = finalArguments[2];
            email = finalArguments[3];

            province = finalArguments[4];
            township = finalArguments[5];
            shortPresentation = finalArguments[6];
            longPresentation = finalArguments[7];

            user = new User(idUser, hasPhoto, name, email, province, township, shortPresentation, longPresentation);
            sharedStore.getUsersList().add(user); // Almacenamos a los usuarios buscados
        }
        connectionToServer.receiveServerResponse(); // S17.0 -> Fin del envio
    }

    // LOGOUT
    //
    public void logout(String[] serverArguments) {
        sharedStore.setUser(new User()); // Reseteamos el usuario
        sharedStore.getUser().setLogged(false); // "Cerramos sesión"
        sharedStore.setListening(false); // Condición de cierre del hilo de escucha
    }
}
