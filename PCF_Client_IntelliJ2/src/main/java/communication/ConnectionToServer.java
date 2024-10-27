package communication;


import hybrid_encrypt.Decryption;
import hybrid_encrypt.Encryption;
import objects.*;
import operators.*;
import tools.Conversions;
import tools.ResponseResult;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author william
 */
public class ConnectionToServer extends Thread {

    private SharedStore sharedStore;

    private String serverHost;
    private int serverPort;
    private Socket conexion;
    private DataInputStream input;
    private DataOutputStream output;

    private Encryption encryption;
    private Decryption decryption;
    private ProtocolMessages messages;
    private String account;
    private String passwordToEncrypt;

    private ListenServer listener;

    private Conversions conversions;

    // CONSTRUCTOR
    public ConnectionToServer(String serverHost, int serverPort) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.conexion = new Socket(serverHost, serverPort);
        this.input = new DataInputStream(conexion.getInputStream());
        this.output = new DataOutputStream(conexion.getOutputStream());

        this.encryption = new Encryption(); // Encriptación
        this.decryption = new Decryption();
        this.conversions = new Conversions(); // Conversiones de Datos

        this.sharedStore = SharedStore.getInstance();
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void clientMessage() { // COMUNICACIÓN: (C24) Cliente <-#-> Servidor (S73)
        try {
            String message = "";
            do {
                message = sharedStore.getClientMessage(); // <- wait();
                // System.out.println("Lo que le digo: " + message); // TODO. Chivato

                if (message.equals("C0") || message.equals("C3")) { // Si es Cierre de programa o Logout
                    sharedStore.setListening(false); // Preparamos el fin del hilo ListenServer
                }

                sendClientMessage(message); // Envia mensaje encriptado al Servidor

                if (message.equals("C0")) {
                    sharedStore.waitUntilResponse(false); // <- Despierta a App para cerrar el programa
                    disconnect(); // <- No preciso de recibir respuesta del Servidor
                    System.exit(0);
                } else {
                    sharedStore.setAnswered(false);
                    TimerThread timer = new TimerThread(this); // Inicia el hilo Temporizador
                    timer.start();
                    sharedStore.getTimers().add(timer);
                }

                String serverResponse = receiveServerResponse(); // Lee la respuesta del Servidor
                // System.out.println("Lo que me dice: " + serverResponse); // TODO. Chivato

                sharedStore.setAnswered(true); // Para evitar que el hilo Temporizador avise de Errores no ocurridos
                processServerResponse(serverResponse); // Procesa la respuesta del Servidor

                sharedStore.waitUntilResponse(false); // Despierta al Hilo Gráfico una vez la información se ha actualizado

            } while (!message.equals("C0"));

        } catch (InterruptedException ex) {
            // Esta Interrupción no se producirá nunca
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void processServerResponse(String response) { // Respuesta del Servidor
        UsersOperator usersOperator = new UsersOperator(this, listener);
        MessagesOperator messagesOperator = new MessagesOperator(this, listener);
        CoursesOperator coursesOperator = new CoursesOperator(this, listener);
        UnitsResourcesOperator unitsResourcesOperator = new UnitsResourcesOperator(this, listener);

        ResourceFileOperator resourceFileOperator = new ResourceFileOperator(this, listener);
        ResourceLinkOperator linksOperator = new ResourceLinkOperator(this, listener);
        ResourceHomeworkOperator resourceHomeworkOperator = new ResourceHomeworkOperator(this, listener);
        ResourceTestOperator resourceTestOperator = new ResourceTestOperator(this, listener);

        String[] serverArguments = response.split("#"); // S2#Pepito Perez#Almadena...


        switch (serverArguments[0]) {

            case "S0":
                disconnect();
                System.exit(0);
                break;
            case "S1": // REGISTRO ACEPTADO -> S1 # idUser
                usersOperator.registrationAccepted(serverArguments);
                break;

            case "S1.2": // Error: Usuario existente
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Datos incorrectos.",
                        "El usuario con cuyos datos intenta registrarse ya esta registrado."));
                break;

            case "S2": // LOGIN ACEPTADO -> 0.S2, 1.IdUser, 2.hasPhoto?, 3.Nombre, 4.Email, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga, 9.MensajesNoLeídos
                usersOperator.loginAccepted(serverArguments);
                break;

            case "S2.1": // Error: Login incorrecto
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Datos incorrectos.",
                        "Los datos que nos ha proporcionado no casan en nuestra Base de Datos."));
                break;

            case "S2.2": // Error: Login... "ocupado"?¿?
                sharedStore.setUser(new User()); // Resetea al usuario del lado Cliente
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Datos incorrectos.",
                        "El usuario con cuyos datos intenta logearse ya esta logeado."));
                break;

            case "S3": // LOGOUT
                usersOperator.logout(serverArguments);
                break;

            case "S4": // PERFIL -> 0.S4, 1.HasPhoto?
                usersOperator.viewProfile(serverArguments);
                break;

            case "S5": // UPDATE USER -> 0.S5, 1.sendPhoto?
                usersOperator.updateProfile(serverArguments);
                break;

            case "S6": // DELETE USER
                //
                //----------------------------------------------------------------------------------------------------------------------------------------------------------------
                //
                // Complejo debido al borrado en cadena (Los mensajes enviados, los cursos creados, las inscripciones a dichos cursos, el material subido, etc.) <- HACER AL FINAL
                //
                //----------------------------------------------------------------------------------------------------------------------------------------------------------------
                //

            case "S7": // Enviar Mensaje
                sharedStore.setResponseResult(new ResponseResult("notification", "Mensaje enviado correctamente."));
                break;

            case "S7.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error al enviar el mensaje.",
                        "Se ha sufrido un error inesperado durante el envio del Mensaje."));
                break;

            case "S8": // Enviar Solicitud
                sharedStore.setResponseResult(new ResponseResult("notification", "Solicitud enviada correctamente."));
                break;

            case "S8.1": // Error al enviar la Solicitud
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Solicitud en curso.",
                        "Ya has enviado una Solicitud para este curso,\nse te notificará por mensajería cuando sea aceptada."));
                break;

            case "S8.2": // Error al enviar la Solicitud
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Curso ya aplicado.",
                        "Ya estas apuntado al Curso que intentas solicitar,\npuedes encontrarlo en el listado \"Cursando\" en el apartado \"Mis Cursos\"."));
                break;

            case "S8.3": // Error al enviar la Solicitud
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error al enviar la Solicitud.",
                        "Se ha sufrido un error inesperado durante el envio de la Solicitud."));
                break;

            case "S9": // ENVIAR INVITACIÓN
                // PERFECTO
                break;
            case "S9.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "El nombre del curso no coincide, debe proporcionar\nel nombre exacto de un curso que este impartiendo."));
                break;
            case "S9.2":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "El usuario ya esta apuntado al Curso al que le estas\n" +
                                "intentando invitar."));
                break;
            case "S9.3":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "Ya has enviado una invitación con anterioridad del mismo curso al mismo usuario."));
                break;
            case "S9.4":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "Se ha sufrido un error inesperado durante el envío."));
                break;

            case "S10": // MENSAJES RECIBIDOS -> 0.S9, 1.CantidadMensajesRecibidos
                messagesOperator.viewInbox(serverArguments);
                break;

            case "S11": // MENSAJES ENVIADOS -> 0.S10, 1.CantidadMensajesEnviados
                messagesOperator.viewOutbox(serverArguments);
                break;

            case "S12": // Leer Mensajes
                // Los mensajes no leidos aparecen en negrita y los leidos no.
                break;

                    /*
                case "S11": // Notificación de Mensajes no Leido. Se muestra al cambiar de pantalla de forma automática
                    noReadedMessages = Integer.parseInt(serverArguments[1]);
                    sharedStore.setNoReadMessages(noReadedMessages);
                    break;
                    */
            case "S13": // ACEPTAR SOLICITUD
                break;

            case "S13.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error al aceptar Solicitud.",
                        "Se ha producido un error al aceptar la Solicitud."));
                break;

            case "S14": // ACEPTAR INVITACIÓN
                break;

            case "S14.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "Se ha producido un error al aceptar la Invitación."));
                break;
            case "S14.2":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "Ya estas registrado en el curso."));
                break;
            case "S14.3":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "No se ha podido borrar la Invitación automáticamente."));
                break;
            case "S14.4":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "No se ha podido notificar al Profesor del curso de tu decisión."));
                break;

            case "S15": // "Borrado" de Mensajes
                // Desaparecen de la Bandeja de Entrada, por lo que no considero necesario notificar al Humano
                break;

            case "S16": // Alta de Cursos
                // Al acabar el alta te devuelve a la seccion de "Mis Cursos"
                break;

            case "S16.1": // Alta de Cursos: ERROR
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error durante el registro.",
                        "Se ha sufrido un error inesperado durante el registro."));
                break;

            case "S16.2": // Alta de Cursos: ERROR
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Nombre Duplicado.",
                        "No puedes crear dos cursos con el mismo nombre."));
                break;

            case "S17": // Update Curso
                // Ocurre lo mismo que con las altas. No hay notificación directa, ya que se puede ver al acabar.
                break;


            case "S18": // Lista de Cursos que imparto
                coursesOperator.coursesITeach(serverArguments);
                break;

            case "S19": // Lista de Cursos que recibo
                coursesOperator.coursesIReceive(serverArguments);

            case "S20": // Delete Curso
                // Desaparece de la lista de "Cursos que Imparto" en Mis Cursos, ergo no hace falta notificación.
                break;

            case "S21": // Motor de Busqueda -> Usuarios
                usersOperator.searchUsers(serverArguments);
                break;

            case "S22": // Motor de busqueda -> Cursos
                coursesOperator.searchCourse(serverArguments);
                break;

            case "S23": // Apertura de Curso OK
                // Función Automática, no se notifica
                break;

            case "S23.1": // Apertura de Curso MAL
                if (sharedStore.getUser().getIdUser() == sharedStore.getSelectedCourse().getIdTeacher()) { // Si es el Profesor
                    sharedStore.setResponseResult(new ResponseResult("alert",
                            "error",
                            "ERROR",
                            "Error: Error inesperado.",
                            "Se ha producido un error inesperado durante la apertura del Curso."));
                }
                // Función Automática, no se notifica. Si Ocurre habra un Checkbox seleccionado en Configurar Curso.
                break;

            case "S24": // AULA VIRTUAL -> S24 # Tema? # Recuso? # Tema? # Recurso?...
                coursesOperator.chargeVirtualClass(serverArguments);
                break;

            case "S24.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error inesperado.",
                        "Se ha producido un error inesperado durante la aplicación del Curso,"
                                + " se recomienda reiniciar el Aula Virtual y si el problema persiste"
                                + " se recomienda no realizar ningún traspaso de archivos hasta que se solucione en el futuro."));
                break;

            case "S25": // Alta Tema
                // Aparecerá en el Aula Virtual Correspondiente
                break;

            case "S25.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error inesperado.",
                        "Se ha producido un error inesperado durante el registro."));
                break;

            case "S25.2":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Nombre Duplicado.",
                        "No puedes tener dos temas con el mismo nombre dentro del mismo curso."));
                break;

            case "S26": // UPDATE TEMA
                // Aparecerá en el Aula Virtual Correspondiente
                break;

            case "S26.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error inesperado.",
                        "Se ha producido un error inesperado durante la actualización."));
                break;

            case "S26.2":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Nombre Duplicado.",
                        "No puedes tener dos temas con el mismo nombre dentro del mismo curso."));
                break;

            case "S27": // Delete Tema
                // Los cambios aparecerán en el Aula Virtual Correspondiente
                break;

            case "S28": // Reordenar Temas
                // Los cambios aparecerán en el Aula Virtual Correspondiente
                break;

            case "S28.1": // Reordenar Temas -> Error
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Error al Reordenar.",
                        "Se ha producido un error al actualizar el orden de los Temas."));
                break;

            case "S29": // ALTA ENLACE
                break;

            case "S30": // ALTA ARCHIVO
                resourceFileOperator.uploadFile();
                break;

            case "S31": // ALTA TAREA
                break;

            case "S32": // ALTA TEST
                break;

            case "S33": // Insert pregunta Test -> S47 # idPregunta # idRespuestaTrue # idRespuestaFalse # idRespuesta2False? # idRespuesta3False? # idRespuesta4False?
                resourceTestOperator.insertIds(serverArguments);
                break;

            case "S34": // UPDATE ENLACE
                break;

            case "S35": // UPDATE ARCHIVO
                resourceFileOperator.uploadFile();
                break;

            case "S36": // UPDATE TAREA
                break;

            case "S37": // UPDATE TEST -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                break;

            case "S38": // Update pregunta Test
                resourceTestOperator.insertLastIds(serverArguments);
                break;

            case "S39": // VER ENLACE -> S41 # url
                linksOperator.saveLinkData(serverArguments);
                break;

            case "S40": // VER ARCHIVO -> S42 # nombreArchivo
                resourceFileOperator.saveFileData(serverArguments);
                break;

            case "S41": // DESCARGAR ARCHIVO (Material o Tarea) -> S38 # nombreArchivo
                resourceFileOperator.downloadFile(serverArguments);
                break;

            case "S41.1": // DESCARGAR ARCHIVO (Material o Tarea) -> S38 # nombreArchivo
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        serverArguments[1],
                        "No se ha encontrado el archivo en la base de datos."));
                break;

            case "S42": // Consultar Tarea -> S42 # idHomework #  #  #
                resourceHomeworkOperator.saveHomeworkData(serverArguments);
                break;

            case "S43":
                resourceFileOperator.uploadHomeworkFiles();
                break;

            case "S44": // Consultar Test [Profesor] -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                break;

            case "S45": //  Consultar Test [Alumno] -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                resourceTestOperator.saveTestData(serverArguments);
                break;

            case "S45.1": //  Consultar Test [Alumno] -> S45/S46 # titulo # presentation # isHidden #questionsQuantity #openDate #openTime #closeDate #closeTime #percentage # testCompleto
                break;

            case "S46": // Resolver Test -> S49 # Ok
                break;
            case "S46.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Resolución duplicada.",
                        "Ya has resuelto este test con anterioridad."));
                break;

            case "S47": // BORRADO RECURSO
                break;

            case "S48": // REORDENAR RECURSOS
                break;

            case "S49": // LISTAR ALUMNOS Y TAREAS SUBIDAS
                resourceHomeworkOperator.saveHomeworkData(serverArguments);
                break;

            case "S50": // ASIGNAR NOTA
                break;

            case "S51": // ENVIO DE CORREOS EN MASA
                break;

            case "S52": // LISTAR REGISTROS (MAPA)
                break;

            case "S53": // FINALIZAR CURSO
                break;

            case "S54":
                break;
            case "S54.1":
                sharedStore.setResponseResult(new ResponseResult("alert",
                        "error",
                        "ERROR",
                        "Error: Resolución duplicada",
                        "Ya has resuelto este test con anterioridad."));
                break;

            default: sharedStore.setResponseResult(new ResponseResult("alert",
                    "error",
                    "ERROR",
                    "Error: Error inesperadp.",
                    "Este error nunca debería pasar."));
                break;
        }
    }

    // ENVIA MENSAJES Y ARCHIVOS
    //
    public void sendClientMessage(String message) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception { // TRADUCE (String a byte[]) -> ENCRIPTA (encryptDataToSend) -> ENVIA (sendEncryptedData)
        byte[] messageInBytes = message.getBytes();
        encryptDataToSend(messageInBytes);
    }

    public void encryptDataToSend(byte[] clientData) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, Exception {
        byte[] encryptedData = encryption.encryptData(clientData);
        sendEncryptedData(encryptedData.length, encryptedData);
    }

    public void sendEncryptedData(int messageLength, byte[] encryptedData) throws IOException {
        byte[] messageLengthInBytes = conversions.intToBytes(messageLength);
        byte[] message = conversions.concatByteArrays(messageLengthInBytes, encryptedData);

        output.write(message); // Envia al Servidor el Mensaje Encriptado
    }

    public void sendArchive(String path) throws IOException {
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        output.writeLong(file.length()); // Envia el tamaño del archivo

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInputStream.read(buffer)) != -1) { // Divide el archivo en trozos
            output.write(buffer, 0, bytes);
            output.flush();
        }
        fileInputStream.close();

        sharedStore.setFilePath("false"); // Para modificaciones: Si no se actualizan los archivos a enviar filePath será "false"
    }

    // RECIBE MENSAJES Y ARCHIVOS
    //
    public String receiveServerResponse() { // TRADUCE (byte[] a String) <- DESENCRIPTA (decryptReceivedData) <- LEE (receiveEncryptData)
        try {
            byte[] decryptedData = decryptReceivedData();
            String decryptedMessage = new String(decryptedData);
            return decryptedMessage;
        } catch (IOException ex) {
            sharedStore.setResponseResult(new ResponseResult("alert",
                    "error",
                    "ERROR",
                    "Error: Error en la comunicación.",
                    "Se ha producido un error inesperado durante el traspaso de información con el servidor."));
            return "";
        }
    }

    public byte[] decryptReceivedData() throws IOException {
        byte[] encryptedData = receiveEncryptedData(); // Recibe el Mensaje del Servidor Encriptado
        decryption.decryptData(encryption.getSymmetricKey(), encryptedData);
        return decryption.getDecryptData();
    }

    public byte[] receiveEncryptedData() throws IOException {
        byte[] buffer = new byte[4];
        input.read(buffer);
        int serverMessageLength = conversions.bytesToInt(buffer);

        byte[] buffer2 = new byte[serverMessageLength];
        input.read(buffer2);

        return buffer2;
    }

    public void receiveArchive(String pathFile) {
        int bytes = 0;
        try (FileOutputStream fileOutputStream = new FileOutputStream(pathFile)) {
            long size = input.readLong(); // Lee tamaño del Archivo

            byte[] encryptedBuffer = new byte[4 * 1024];
            while (size > 0 && (bytes = input.read(encryptedBuffer, 0, (int) Math.min(encryptedBuffer.length, size))) != -1) { // Lee por Trozos
                fileOutputStream.write(encryptedBuffer, 0, bytes);
                size -= bytes;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public byte[] receiveArchivePreview() { // Fotografía del Perfil
        int bytes = 0;
        long size = 0; // Lee tamaño del Archivo
        byte[] preview = new byte[0];

        try {
            size = input.readLong();

            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = input.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) { // Lee por Trozos
                preview = conversions.concatByteArrays(preview, buffer);
                size -= bytes;
            }
        } catch (IOException e) {
            sharedStore.setResponseResult(new ResponseResult("alert",
                    "error",
                    "ERROR",
                    "Error: Error en la comunicación.",
                    "No se ha podido mostrar la fotografía de este perfil."));
        }
        return preview;
    }

    // PREPARACIÓN ENCRIPTADO
    //
    public void hybridEncryptionPreparation() {
        try {
            // 1º.- Recibe la Clave Pública del Servidor (Bytes[])
            byte[] publicServerKey = receiveEncryptedData();

            //2º.- Encripta la Clave Simétrica y la envia
            byte[] symmetricalEncryptedKey = encryption.encryptSymmetricKey(publicServerKey);
            sendEncryptedData(symmetricalEncryptedKey.length, symmetricalEncryptedKey);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // DESCONEXIÓN
    //
    public void disconnect() {
        try {
            input.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            output.writeUTF("noAndroid");
            input.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hybridEncryptionPreparation(); // Comparte la Clave de Sesión con el Servidor
        clientMessage(); // Inicia el proceso de Comunicación
    }
}
