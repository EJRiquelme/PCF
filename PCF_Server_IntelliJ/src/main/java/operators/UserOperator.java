package operators;

import objects.User;
import pcf_server.ClientServiceThread;
import pcf_server.NotificationsThread;
import pcf_server.SharedObject;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class UserOperator {
    private ClientServiceThread serviceThread;
    private SharedObject sharedObject;

    private User user;
    private int idUser;
    private boolean photo;
    private String name;
    private String email;
    private String password;
    private String province;
    private String township;
    private String shortPresentation;
    private String longPresentation;

    private String messageToClient;

    public UserOperator(ClientServiceThread serviceThread, SharedObject sharedObject) {
        this.serviceThread = serviceThread;
        this.sharedObject = sharedObject;
    }

    // PETICIÓN DE REGISTRO -> 0.C1, 1.photo?, 2.Nombre, 3.Email, 4.Contraseña, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga
    //
    public String registerUser(String[] clientArguments) {
        photo = clientArguments[1].equals("true");
        name = clientArguments[2];
        email = clientArguments[3];
        password = serviceThread.generateHash(clientArguments[4]);
        province = clientArguments[5];
        township = clientArguments[6];
        shortPresentation = clientArguments[7];
        longPresentation = clientArguments[8];

        if (clientArguments[8].equals("noLongPresentation")) {
            longPresentation = null;
        }

        user = new User(photo, name, email, password, province, township, shortPresentation, longPresentation); // El Usuario
        String registerResult = sharedObject.getMysqlConnection().registerUser(user);

        if (!registerResult.equals("Ok")) {
            serviceThread.setUser(new User()); // Reset del Usuario
            if (registerResult.equals("Clave")) {
                return "Error: Clave única duplicada";
            } else {
                return "Error: Error de registro";
            }
        }

        File directory = new File("users" + File.separator + clientArguments[3]); // Genera un directorio para el nuevo Usuario
        directory.mkdirs();

        idUser = sharedObject.getMysqlConnection().getUserId(user); // Devolvemos el "id" generado para futuras operaciones
        if (idUser == 0) {
            return "Error: Error al recoger Id";
        }
        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(1) + "#" + idUser); // REGISTRO ACEPTADO (Y "COMPLETADO" [DATOS BASE: Nombre, email, contraseña...])

        // RECIBO DE FOTOGRAFÍA Y LOGIN
        //
        //
        if (clientArguments[1].equals("true")) { // Foto
            serviceThread.receiveArchive("users" + File.separator + clientArguments[3] + File.separator + "photo");
        }

        user.setIdUser(idUser);
        loginUser(user);  // Inicio de Sesión en Lado Servidor

        return ".0"; // FIN DEL REGISTRO Y LOGIN
    }

    // PETICIÓN INICIO DE SESIÓN -> 0.C2, 1.Nombre, 2.Email, 3.Contraseña
    //
    public String confirmUser(String[] clientArguments) {
        name = clientArguments[1];
        email = clientArguments[2];
        password = serviceThread.generateHash(clientArguments[3]);

        if (sharedObject.isClientLoged(email)) {
            return "Error: Usuario ya logeado";
        }

        user = sharedObject.getMysqlConnection().confirmUser(new User(name, email, password));

        if (user.getName().equals("Cliente Anónimo")) {
            return "Error: Usuario no confirmado";
        }

        idUser = user.getIdUser();
        photo = user.hasPhoto();
        province = user.getProvince();
        township = user.getTownship();
        shortPresentation = user.getShortPresentation();
        longPresentation = user.getLongPresentation();

        int notReadMessages = sharedObject.getMysqlConnection().messagesToBeReaden(idUser);

        messageToClient = idUser
                + "#" + photo
                + "#" + name
                + "#" + email
                // + "#" + password  // No devolvemos el password
                + "#" + province
                + "#" + township
                + "#" + shortPresentation
                + "#" + longPresentation
                + "#" + notReadMessages;

        loginUser(user);  // Inicio de Sesión en Lado Servidor

        return "#" + messageToClient;
    }


    // PARA LAS PETICIONES DE REGISTRO Y LOGIN
    //
    public void loginUser(User user) { // Inicio de Sesión en Lado Servidor
        serviceThread.setUser(user);

        sharedObject.putLoggedClient(email, serviceThread.getClient()); // Sesión iniciada: (email/Socket) e Hilo Guardado: (email/HiloTCP)
        sharedObject.putLoggedTCPThread(email, serviceThread);

        NotificationsThread notifications = new NotificationsThread(sharedObject, user.getEmail()); // Iniciamos hilo UDP
        //notifications.start();

        sharedObject.putLoggedUDPThread(user.getEmail(), notifications); // (email/HiloUDP)
    }


    // VER PERFIL USUARIO -> 0.C4, 1.IdUsuario
    //
    public String getUserPerfil(String[] clientArguments) {
        idUser = Integer.parseInt(clientArguments[1]);
        User userPerfil = sharedObject.getMysqlConnection().takeUserPerfil(idUser); // Cogemos TODOS los datos

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(4) + "#" + userPerfil.hasPhoto()); // S4 # true/false (Envío de fotografía)
        if (userPerfil.hasPhoto()) {
            serviceThread.sendPhoto(userPerfil.getEmail());
        }

        if (userPerfil.getIdUser() == -1) {
            return "Error: Perfil borrado";
        } else if (userPerfil.getIdUser() == 0) {
            return "Error: Error de lectura";
        }

        photo = userPerfil.hasPhoto();
        name = userPerfil.getName();
        email = userPerfil.getEmail();
        password = userPerfil.getPassword();
        province = userPerfil.getProvince();
        township = userPerfil.getTownship();
        shortPresentation = userPerfil.getShortPresentation();
        longPresentation = userPerfil.getLongPresentation();

        messageToClient = "#" + idUser
                + "#" + photo
                + "#" + name
                + "#" + email
                + "#" + province
                + "#" + township
                + "#" + shortPresentation
                + "#" + longPresentation;

        return messageToClient;
    }


    // MODIFICAR USUARIO -> 0.C5, 1.IdUser, 2.changePhoto?, 3.Nombre, 4.Email, 5.Contraseña, 6.Provincia, 7.Municipio, 8.Presentación Corta, 9.Presentación Larga
    //
    public String updateUser(String[] clientArguments) {
        boolean auxiliaryHasPhoto = serviceThread.getUser().hasPhoto(); // Puesto que ya se ha iniciado la sesión, su información original está en "serviceThread.getUser()"

        if (clientArguments[2].equals("true")) { // Si ha cambiado la fotografía -> True, y si no mantiene el valor anterior, fuese el que fuese
            auxiliaryHasPhoto = true;
        }

        idUser = Integer.parseInt(clientArguments[1]);
        photo = clientArguments[2].equals("true");
        name = clientArguments[3];
        email = clientArguments[4];
        password = serviceThread.generateHash(clientArguments[5]);
        province = clientArguments[6];
        township = clientArguments[7];
        shortPresentation = clientArguments[8];
        longPresentation = clientArguments[9];

        if (clientArguments[9].equals("noLongPresentation")) { // De no tener Presentación larga, se establece como Null
            longPresentation = null;
        }

        User userToUpdate = new User(idUser, auxiliaryHasPhoto, name, email, password, province, township, shortPresentation, longPresentation); // El Usuario

        String updateResult = sharedObject.getMysqlConnection().updateUser(userToUpdate);

        if (!updateResult.equals("Ok")) {
            if (updateResult.equals("Clave")) { // El Cliente está preparado para que esto no pase NUNCA
                return "Error: Clave única duplicada";
            } else {
                return "Error: Error de actualización";
            }
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(5) + "#" + clientArguments[2]);// S5 # true/false -> Actualización de la fotografía
        if (clientArguments[2].equals("true")) {
            serviceThread.receiveArchive("users" + File.separator + userToUpdate.getEmail() + File.separator + "photo");
        }

        serviceThread.setUser(userToUpdate); // Actualización del Usuario en el lado Servidor

        messageToClient = "#" + userToUpdate.getIdUser()
                + "#" + userToUpdate.hasPhoto()
                + "#" + userToUpdate.getName()
                + "#" + userToUpdate.getEmail()
                + "#" + userToUpdate.getProvince()
                + "#" + userToUpdate.getTownship()
                + "#" + userToUpdate.getShortPresentation()
                + "#" + userToUpdate.getLongPresentation();
        return messageToClient;
    }


    // BORRADO DEL USUARIO
    //
    public String deleteUser() {
        boolean deleteResult = sharedObject.getMysqlConnection().deleteUser(serviceThread.getUser());
        if (!deleteResult) {
            return "Error: Error de borrado";
        }

        File directoryToDelete = new File("users" + File.separator + serviceThread.getUser().getEmail());
        sharedObject.deleteDirectory(directoryToDelete);
        logoutUser(serviceThread.getUser());

        return "Ok";
    }


    // CIERRE DE SESIÓN
    //
    public void logoutUser(User user) {
        sharedObject.removeLoggedClient(user.getEmail()); // Elimina al usuario de la lista de Sesiones Iniciadas
        sharedObject.removeLoggedTCPThread(user.getEmail()); // Se borra a sí mismo de la lista de Threads para evitar ser interrumpido

        sharedObject.getUDPThread(user.getEmail()).interrupt(); // Interrumpe a su propio hilo UDP para enviar una Notificación y cerrarlo
        sharedObject.removeLoggedUDPThread(user.getEmail());

        serviceThread.setUser(new User()); // Resetea al Usuario
    }

    public String searchUsers(String[] clientArguments) {

        LinkedList<String> searchUsersParameters = new LinkedList<>(Arrays.asList(clientArguments).subList(1, clientArguments.length));

        LinkedList<User> searchUsersResult = sharedObject.getMysqlConnection().searchMultipleUsers(searchUsersParameters, serviceThread.getUser().getIdUser());

        if (searchUsersResult == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(21) + "#" + searchUsersResult.size()); // S17#CantidadDeResultadosUsuarios

        for (User u : searchUsersResult) {
            String result = u.getIdUser()
                    + "#" + u.hasPhoto()
                    + "#" + u.getName()
                    + "#" + u.getEmail()
                    + "#" + u.getProvince()
                    + "#" + u.getTownship()
                    + "#" + u.getShortPresentation()
                    + "#" + u.getLongPresentation();
            serviceThread.sendServerResponse(result);
        }
        return "Ok";
    }
}
