/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pcf_server;

import objects.*;
import hybrid_encryption.Decryption;
import hybrid_encryption.GeneratePrivateAndPublicKeys;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.security.*;

import hybrid_encryption.Encryption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import operators.*;
import tools.Conversions;

/**
 * @author william
 */
public class ClientServiceThread extends Thread {

    private User user; // Cliente actual
    private Course activeVirtualClass; // Curso de Aula Virtual activo
    private Resource activeResource; // Recurso con el que el usuario esta interactuando (PARA NOTIFICACIONES POR DELETES/HIDES)
    private String messageToClient;
    private String os;

    private SharedObject sharedObject;
    private Socket client;
    private DataInputStream input;
    private DataOutputStream output;

    private Encryption encryption = new Encryption();
    private Decryption decryption = new Decryption();

    private NotificationsThread notifications; // Para Notificaciones

    private ProtocolMessages protocolMessages;
    private Conversions conversions;

    private GeneratePrivateAndPublicKeys privatePublicKeys;
    byte[] privateServerKey;
    byte[] publicServerKey;
    byte[] symmetricKey;

    UserOperator userOperator;
    MessageOperator messageOperator;
    CourseOperator courseOperator;

    UnitOperator unitOperator;

    ResourceOperator resourceOperator;

    ResourceLinkOperator resourceLinkOperator;
    ResourceArchiveOperator resourceArchiveOperator;
    ResourceHomeworkOperator resourceHomeworkOperator;
    ResourceTestOperator resourceTestOperator;


    // CONSTRUCTOR
    public ClientServiceThread(SharedObject sharedObject, Socket client) throws IOException {
        this.user = new User();
        this.activeVirtualClass = new Course();
        this.activeResource = null;

        this.sharedObject = sharedObject;
        this.client = client;
        this.input = new DataInputStream(client.getInputStream());
        this.output = new DataOutputStream(client.getOutputStream());
        this.encryption = new Encryption();
        this.decryption = new Decryption();

        this.protocolMessages = new ProtocolMessages();
        this.conversions = new Conversions(); // Conversiones de Datos
    }

    // GETTERS && SETTERS
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getActiveVirtualClass() {
        return activeVirtualClass;
    }

    public void setActiveVirtualClass(Course activeVirtualClass) {
        this.activeVirtualClass = activeVirtualClass;
    }

    public Resource getActiveResource() {
        return activeResource;
    }

    public void setActiveResource(Resource activeResource) { // TODO: activeResource para los DELETES
        this.activeResource = activeResource;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public NotificationsThread getNotifications() {
        return notifications;
    }

    public void setNotifications(NotificationsThread notifications) {
        this.notifications = notifications;
    }

    public void updateStudentArchive(String teacherEmail, String courseName, String archiveName) throws IOException { // Multiples: en bucle -> FOR
        // Lee archivo
        int bufferLenght = input.readInt();
        byte[] buffer = new byte[bufferLenght];
        input.read(buffer);
        byte[] archive = buffer;

        // Escribe archivo -> Users/Profesor/Curso/Alumno/Tarea
        try (FileOutputStream fos = new FileOutputStream("users" + File.separator + teacherEmail + File.separator + courseName + File.separator + user.getEmail() + File.separator + archiveName)) {
            fos.write(archive);
        }
    }

    public void updateTeacherArchive(String courseName, String archiveName) throws IOException {
        // Lee archivo
        int bufferLenght = input.readInt();
        byte[] buffer = new byte[bufferLenght];
        input.read(buffer);
        byte[] archive = buffer;

        // Escribe archivo -> Users/Profesor/Curso/Archivo
        try (FileOutputStream fos = new FileOutputStream("users" + File.separator + user.getEmail() + File.separator + courseName + File.separator + archiveName)) {
            fos.write(archive);
        }
    }

    /*
        public void updatePhoto(String userEmail) throws IOException {
            byte[] archive = decryptReceivedData(); // Recibo la Fotografía Encriptada

            // byte[] archive = receiveEncryptedData(); // Recibo la Fotografía <- Tambien falla
            String path = "users" + File.separator + userEmail + File.separator + "photo"; // Guarda Foto -> Users/Email/photo

            FileOutputStream fos = new FileOutputStream(new File(path));
            fos.write(archive);
            fos.flush();
            fos.close();
        }
    */

    public void PORFAVOR(String userEmail, byte[] archive) throws FileNotFoundException, IOException {
        String path = "users" + File.separator + userEmail + File.separator + "photo"; // Guarda Foto -> Users/Email/photo

        FileOutputStream fos = new FileOutputStream(new File(path));
        fos.write(archive);
        fos.flush();
        fos.close();
    }

    public void sendPhoto(String email) {
        //Path photoPath = Paths.get("users" + File.separator + email + File.separator + "photo");
        //byte[] photoFile = Files.readAllBytes(photoPath);
        //encryptDataToSend(photoFile);
        sendArchive("users" + File.separator + email + File.separator + "photo");
    }

    // RECIBE 1 MENSAJE (CON O SIN ARGUMENTOS) Y ENVIA 1 RESPUESTA (CON O SIN ARGUMENTOS)
    public void clientService() {
        try {
            String clientMessage;
            String[] clientArguments;
            String response;
            try {
                do {
                    if (!os.equals("android")) {
                        clientMessage = receiveClientMessage();
                    } else {
                        clientMessage = receiveAndroidMessage();
                    }
                    System.out.println("Mensaje recibido del Cliente: " + clientMessage); // C42#naranja#potatoe...
                    clientArguments = clientMessage.split("#");

                    //sleep(7000); // Comprobaciones
                    response = processResponse(clientArguments);

                    System.out.println("Respuesta que se le otorga: " + response); // S23#paco#pepe#pedro...

                    sendServerResponse(response);

                    System.out.println(user.getName() + ": " + sneak(response));

                } while (!response.equals("S0")); // Hasta "Salir"

            } catch (InterruptedException ex) { // Mensaje recibido
                System.out.println("Error inesperado. Este hilo no debería ser interrumpido.");
                clientService(); // Retoma la comunicación
            }
        } catch (IOException ex) {
            System.out.println("Excepción: Error en los flujos de entrada o salida con el Cliente.");
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException ex) {
                System.out.println("Excepción: Error en el cierre de los flujos de entrada y salida con el Cliente.");
                Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    String S;

    // PROCESAMIENTO: (C19) Cliente <-#-> Servidor (S23)
    //
    public String processResponse(String[] clientArguments) throws IOException, Exception {
        S = "S";
        Date date = Date.valueOf(LocalDate.now()); // <- Para registros (A ver si no explota)
        Time time = Time.valueOf(LocalTime.now());

        userOperator = new UserOperator(this, sharedObject);
        messageOperator = new MessageOperator(this, sharedObject, conversions);

        courseOperator = new CourseOperator(this, sharedObject, conversions);
        unitOperator = new UnitOperator(this, sharedObject, conversions);

        resourceOperator = new ResourceOperator(this, sharedObject, conversions);
        resourceLinkOperator = new ResourceLinkOperator(this, sharedObject, conversions);
        resourceArchiveOperator = new ResourceArchiveOperator(this, sharedObject, conversions);
        resourceHomeworkOperator = new ResourceHomeworkOperator(this, sharedObject, conversions);
        resourceTestOperator = new ResourceTestOperator(this, sharedObject, conversions);

        String messageToClient = "";

        char serverInfo = ' ';

        Unit unit;

        switch (clientArguments[0]) {
            case "C0": // SALIR -> 0.C0
                if (sharedObject.isClientLoged(user.getEmail())) { // Elimina al usuario de la lista de Logeados y a si mismo de la lista de Threads
                    userOperator.logoutUser(user);
                }
                return S + "0";

            case "C1": // PETICIÓN DE REGISTRO -> 0.C1, 1.photo?, 2.Nombre, 3.Email, 4.Contraseña, 5.Provincia, 6.Municipio, 7.Presentación Corta, 8.Presentación Larga
                messageToClient = userOperator.registerUser(clientArguments);

                if (messageToClient.equals("Error: Error de registro")) {
                    return S + "1.3";
                } else if (messageToClient.equals("Error: Error al recoger Id")) {
                    return S + "1.2";
                } else if (messageToClient.equals("Error: Clave única duplicada")) {
                    return S + "1.1";
                }
                return S + "1" + messageToClient; // S1.0

            case "C2": // PETICIÓN LOGIN -> 0.C2, 1.Nombre, 2.Email, 3.Contraseña
                messageToClient = userOperator.confirmUser(clientArguments);

                if (messageToClient.equals("Error: Usuario ya logeado")) {
                    return S + "2.2";
                } else if (messageToClient.equals("Error: Usuario no confirmado")) {
                    return S + "2.1";
                }
                return S + "2" + messageToClient;

            case "C3": // LOGOUT -> 0.C3
                userOperator.logoutUser(user);
                return S + "3";

            case "C4": // VER PERFIL USUARIO -> 0.C4, 1.IdUsuario
                messageToClient = userOperator.getUserPerfil(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "4.2";
                } else if (messageToClient.equals("Error: Perfil borrado")) {
                    return S + "4.1";
                }
                return S + "4.0" + messageToClient;

            case "C5": // MODIFICAR USUARIO -> 0.C5, 1.IdUser, 2.changePhoto?, 3.Nombre, 4.Email, 5.Contraseña, 6.Provincia, 7.Municipio, 8.Presentación Corta, 9.Presentación Larga
                messageToClient = userOperator.updateUser(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "5.2";
                } else if (messageToClient.equals("Error: Clave única duplicada")) {
                    return S + "5.1";
                }
                return S + "5.0" + messageToClient;

            case "C6": // DELETE USUARIO
                messageToClient = userOperator.deleteUser();

                if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "6.1";
                }
                return S + "6";

            case "C7": // ENVIAR MENSAJE -> 0.C7, 1.Asunto, 2.Texto, 3.Fecha, 4.Hora, 5.Tipo, 6.Curso, 7.Remitente, 8.Destinatario <--------------------
                messageToClient = messageOperator.sendMessage(clientArguments);

                if (messageToClient.equals("Error: Error al registrar mensaje")) {
                    return S + "7.1";
                }
                return S + "7";

            case "C8": // ENVIAR SOLICITUD -> 0.C7, 1.Asunto, 2.Texto, 3.Fecha, 4.Hora, 5.Tipo, 6.Curso, 7.Remitente, 8.Destinatario <------------------
                messageToClient = messageOperator.sendRequest(clientArguments);

                if (messageToClient.equals("Error: Error al registrar solicitud")) {
                    return S + "8.3";
                } else if (messageToClient.equals("Error: Estudiante ya registrado")) {
                    return S + "8.2";
                } else if (messageToClient.equals("Error: Solicitud ya enviada")) {
                    return S + "8.1";
                }
                return S + "8";

            case "C9": // ENVIAR INVITACIÓN -> 0.C9, 1.idUser, 2.idCurso
                messageToClient = messageOperator.sendInvitation(clientArguments);
                return S + "9" + messageToClient;

            case "C10": // CONSULTAR BANDEJA DE ENTRADA (Recibidos) -> 0.C9, 1.idUser
                messageToClient = messageOperator.inboxReceivedMessages(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "10.1";
                }
                return S + "10.0";

            case "C11": // CONSULTAR BANDEJA DE SALIDA (Enviados) -> 0.C10, 1.idUser
                messageToClient = messageOperator.inboxSendedMessages(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "11.1";
                }
                return S + "11.0";

            case "C12": // LEER MENSAJE -> 0.C10, 1.idMensaje
                messageToClient = messageOperator.readMessage(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "12.1";
                }
                return S + "12";

            case "C13": // ACEPTAR SOLICITUD -> 0.C20, 1.IdReceiver, 2.IdCurso, 3.IdSolicitud, 4.IdSender, 5.Asunto, 6.Mensaje, 7.Fecha, 8.Hora
                messageToClient = messageOperator.acceptSolicitude(clientArguments);

                if (messageToClient.equals("Error: Error de notificación")) {
                    return S + "13.4";
                } else if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "13.3";
                } else if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "13.2";
                } else if (messageToClient.equals("Error: Error de registro")) {
                    return S + "13.1";
                }
                return S + "13";

            case "C14": // ACEPTAR INVITACIÓN -> 0.C14
                messageToClient = messageOperator.acceptInvitation(clientArguments);
                return S + "14" + messageToClient;

            case "C15": // BORRAR MENSAJES (De la Bandeja de Entrada)-> 0.C15, 1.idMensaje, 2.isSender
                messageToClient = messageOperator.deleteMessage(clientArguments);

                if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "15.1";
                }
                return S + "15";

            case "C16": // ALTA CURSO -> 0.C13, 1.NombreCurso, 2.PresentaciónCorta, 3.PresentaciónLarga, 4.FechaInicio, 5.FechaFinal, 6.IdProfesor, ¿7.Tag, ¿8.Tag, ¿9.Tag, ¿etc.
                messageToClient = courseOperator.registerCourse(clientArguments);

                if (messageToClient.equals("Error: Error de registro")) {
                    return S + "16.2";
                } else if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "16.1";
                }
                return S + "16";

            case "C17": // UPDATE DE CURSO -> 0.C13, 1.IdCurso, 2.NombreCurso, 3.PresentaciónCorta, 4.PresentaciónLarga, 5.FechaInicio, 6.FechaFinal, 7.Oculto?, 8.IdProfesor, ¿9.Tag, ¿10.Tag, ¿11.Tag, ¿etc.
                messageToClient = courseOperator.updateCourse(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "17.2";
                } else if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "17.1";
                }
                return S + "17";

            case "C18": // LISTA CURSOS QUE IMPARTO -> 0.C18, 1.userId
                messageToClient = courseOperator.coursesITeach(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "18.1";
                }
                return S + "18.0";

            case "C19": // LISTA CURSOS QUE RECIBO -> 0.C19, 1.userId
                messageToClient = courseOperator.coursesIReceive(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "19.1";
                }
                return S + "19.0";

            case "C20": // DELETE CURSO -> 0.C15, 1.idCurso
                messageToClient = courseOperator.deleteCourse(clientArguments);

                if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "20.1";
                }
                return S + "20";

            case "C21": // MOTOR DE BUSQUEDA: USUARIOS -> 0.C17, 1.NombreUsuario/none, 2.Provincia/none, 3.Municipio/none, 4.Tag/none, 5.Tag?, 6.Tag?, etc.
                messageToClient = userOperator.searchUsers(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "21.1";
                }
                return S + "21.0";

            case "C22": // MOTOR DE BUSQUEDA: CURSOS -> 0.C16, 1.CurrentDate, 2.NombreCurso/none, 3.Provincia/none, 4.Municipio/none, 5.Tag/none, 6.Tag?, 7.Tag?, etc.
                messageToClient = courseOperator.searchCourses(clientArguments);

                if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "22.1";
                }
                return S + "22.0";

            case "C23": // APERTURA DE CURSO AUTOMÁTICA -> 0.C21, 1.IdCurso
                messageToClient = courseOperator.openCourse(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "23.1";
                }
                return S + "23";

            case "C24": // AULA VIRTUAL (guardar en memoria "activeAulaVirtual", listar temas y recursos) -> 0.C27, 1.Id Curso
                messageToClient = courseOperator.virtualClass(clientArguments);
                activeResource = null;

                if (messageToClient.equals("Error: Error de Lectura de Recursos")) {
                    return S + "24.3";
                } else if (messageToClient.equals("Error: Error de Lectura de Temas")) {
                    return S + "24.2";
                } else if (messageToClient.equals("Error: Error de Lectura de Curso")) {
                    return S + "24.1";
                }
                return S + "24" + messageToClient; // S26.0 -> FIN DEL ENVIO

            case "C25": // ALTA TEMA -> 0.C25, 1.titleUnit, 2.hiddenUnit, 3.percentageExercises, 4.percentageControls, 5.percentageExams, 6.percentageTests
                messageToClient = unitOperator.registerUnit(clientArguments);

                if (messageToClient.equals("Error: Error de consulta")) {
                    return S + "25.4";
                } else if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "25.3";
                } else if (messageToClient.equals("Error: Error de registro")) {
                    return S + "25.2";
                } else if (messageToClient.equals("Error: Error de asignación de posición")) {
                    return S + "25.1";
                }
                return S + "25" + messageToClient;

            case "C26": // UPDATE TEMA -> 0.C26, 1.idUnit, 2.titleUnit, 3.hiddenUnit, 4.percentageExercises, 5.percentageControls, 6.percentageExams, 7.percentageTests
                messageToClient = unitOperator.updateUnit(clientArguments);

                if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "26.3";
                } else if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "26.2";
                }
                return S + "26";

            case "C27": // DELETE TEMA -> 0.C27, 1.IdUnit
                messageToClient = unitOperator.deleteUnit(clientArguments);

                if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "27.1";
                }
                return S + "27";

            case "C28": // REORDER TEMA -> 0.C24, 1.Id Unit, 2.Position, 3.Id, 4.Position, 5.Id, 6.Position...
                messageToClient = unitOperator.reorderUnits(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "28.1";
                }
                return S + "28";

            case "C29": // ALTA ENLACES -> 0.C27, 1.Título Recurso, 2.Presentación Recurso,3.,4.,5.,6.,7.,8.,9.
                messageToClient = resourceOperator.registerLink(clientArguments);

                if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "29.4";
                } else if (messageToClient.equals("Error: Error de registro del Recurso")) {
                    return S + "29.3";
                } else if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "29.2";
                } else if (messageToClient.equals("Error: Error de registro del Link")) {
                    return S + "29.1";
                }
                return S + "29";

            case "C30": // ALTA ARCHIVOS -> C28 # tituloRecurso # presentación # tipoRecurso # oculto/no # idUnit # nombreArchivo
                messageToClient = resourceOperator.registerArchive(clientArguments);

                if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "30.4";
                } else if (messageToClient.equals("Error: Error de registro del Recurso")) {
                    return S + "30.3";
                } else if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "30.2";
                } else if (messageToClient.equals("Error: Error de registro del Archivo")) {
                    return S + "30.1";
                }
                return S + "30.0";

            case "C31": // ALTA TAREAS -> 0.C29, 1.,.,.
                messageToClient = resourceHomeworkOperator.registerHomework(clientArguments);

                if (messageToClient.equals("Error: Clave duplicada")) {
                    return S + "31.4";
                } else if (messageToClient.equals("Error: Error de registro del Recurso")) {
                    return S + "31.3";
                } else if (messageToClient.equals("Error: Error de lectura")) {
                    return S + "31.2";
                } else if (messageToClient.equals("Error: Error de registro de la Tarea")) {
                    return S + "31.1";
                }
                return S + "31" /* + messageToClient */;

            case "C32": // ALTA TEST -> 0.C44, 1.tituloRecurso, 2.presentaciónRecurso, 3.tipoRecurso, 4.oculto, 5.idTema,
                // 6.cantidadPreguntas, 7.fechaApertura, 8.horaApertura, 9.fechaCierre, 10.horaCierre, 11.porcentajeNota
                messageToClient = resourceTestOperator.registerTest(clientArguments);
                return S + "32" + messageToClient;

            case "C33": // ALTA PREGUNTA/RESPUESTAS -> 0.C47, 1.idTest, 2.pregunta, 3.respuestaOk, 4.respuestaMal,
                // 5?.respuestaMal, 6?.respuestaMal, 7?.respuestaMal
                messageToClient = resourceTestOperator.registerQuestionAnswers(clientArguments);

                return S + "33" + messageToClient;

            case "C34": // UPDATE ENLACES -> 0.C, 1.
                messageToClient = resourceOperator.updateLink(clientArguments);

                if (messageToClient.equals("Error: Error de actualizacion del recurso")) {
                    return S + "34.2";
                } else if (messageToClient.equals("Error: Error de actualizacion del enlace")) {
                    return S + "34.1";
                }
                return S + "34";

            case "C35": // UPDATE ARCHIVO -> 0.C31, 1.idRecurso, 2.tituloRecurso, 3.presentación, 4.oculto/no, 5.idUnit, 6.nombreArchivo
                messageToClient = resourceOperator.updateArchive(clientArguments);

                if (messageToClient.equals("Error: Error de actualizacion del recurso")) {
                    return S + "35.2";
                } else if (messageToClient.equals("Error: Error de actualizacion del archivo")) {
                    return S + "35.1";
                }
                return S + "35";

            case "C36": // UPDATE TAREA -> 0.C, 1.
                messageToClient = resourceHomeworkOperator.updateHomework(clientArguments);

                if (messageToClient.equals("Error: Error de actualizacion del recurso")) {
                    return S + "36.2";
                } else if (messageToClient.equals("Error: Error de actualizacion de la tarea")) {
                    return S + "36.1";
                }
                return S + "36";

            case "C37": // UPDATE TEST -> 0.C45, 1.idRecurso, 2.tituloRecurso, 3.presentaciónRecurso, 4.oculto,
                // 5.cantidadPreguntas, 6.fechaApertura, 7.horaApertura, 8.fechaCierre, 9.horaCierre, 10.porcentajeNota
                messageToClient = resourceTestOperator.updateResourceTest(clientArguments);

                return S + "37" + messageToClient;

            case "C38": // UPDATE PREGUNTA/RESPUESTAS -> 0.C48, 1.idPregunta, 2.pregunta, 3.idRespuestaOk, 4.respuestaOk, 5.idRespuestaMal, 6.respuestaMal,
                // 7?.idRespuestaMal, 8?.respuestaMal, 9?.idRespuestaMal, 10?.respuestaMal, 11?.idRespuestaMal, 12?.respuestaMal
                messageToClient = resourceTestOperator.updateQuestionAnswers(clientArguments);

                return S + "38" + messageToClient;

            case "C39": // VER ENLACE -> 0.C41, 1.idLink
                messageToClient = resourceLinkOperator.consultLink(clientArguments);

                return S + "39" + messageToClient;

            case "C40": // VER ARCHIVO -> 0.C41, 1.idArchivo
                messageToClient = resourceArchiveOperator.consultArchive(clientArguments);

                return S + "40#" + messageToClient;

            case "C41": // DESCARGAR ARCHIVOs ([Profesor] Tareas y Archivos [Cualquiera]) -> 0.C38, 1.Path, 2.IdRecurso
                messageToClient = resourceOperator.downloadFile(clientArguments);

                if (messageToClient.equals("Error: Error de descarga")) {
                    return S + "41.1";
                }

                return S + "41.0" + messageToClient; // Fin del Envio

            case "C42": // VER TAREA -> 0.C42, 1.idResource
                messageToClient = resourceHomeworkOperator.consultHomework(clientArguments);

                return S + "42" + messageToClient;

            case "C43": // SUBIR ARCHIVO/S (Tareas) -> 0.C43, 1.idUnit, 2.idResource, 3.NombreArchivo, ¿4.NombreArchivo, ¿5.NombreArchivo...
                messageToClient = resourceHomeworkOperator.uploadFiles(clientArguments);

                if (messageToClient.equals("Error: Fecha límite expirada")) {
                    return S + "43.1";
                } else if (messageToClient.equals("Error: Hora límite expirada")) {
                    return S + "43.2";
                }

                return S + "36.0"; // Fin del Envío

            case "C44": // SELECT TEST [Profesor] -> 0.46, 1.idRecurso
                messageToClient = resourceTestOperator.consultCompleteTest(clientArguments);
                return S + "44" + messageToClient;

            case "C45": // SELECT TEST [Alumno] -> 0.46, 1.idRecurso
                messageToClient = resourceTestOperator.consultRandomTest(clientArguments);
                activeResource = new Resource(Integer.parseInt(clientArguments[1]));
                return S + "45" + messageToClient;

            case "C46": // RESOLVER TEST -> C50 # idRecurso # Nota # idRespuesta  # idRespuesta # idRespuesta # idRespuesta...
                messageToClient = resourceTestOperator.solveTest(clientArguments);
                return S + "46" + messageToClient;

            case "C47": // BORRADO RECURSOS -> 0.C35, 1.id Recurso, 2.tipo Recurso
                messageToClient = resourceOperator.deleteResource(clientArguments);

                if (messageToClient.equals("Error: Error de borrado")) {
                    return S + "47.1";
                }
                return S + "47";

            case "C48": // REORDENAR RECURSOS -> 0.C30, 1.Id Recurso, 2.Posición, 3.Id Recurso, 3.Posición....
                messageToClient = resourceOperator.reorderResource(clientArguments);

                if (messageToClient.equals("Error: Error de actualización")) {
                    return S + "48.1";
                }
                return S + "48";

            case "C49": // LISTAR ALUMNOS Y TAREAS SUBIDAS (Asignación de Notas por Tarea: Paso 1)
                messageToClient = resourceHomeworkOperator.consultHomework(clientArguments);

                if (messageToClient.equals("Error: Error inesperado")) {
                    return S + "49.1";
                }

                return S + "49" + messageToClient; // Fin del Envío

            case "C50": // ASIGNAR NOTA -> 0.C50, 1.Score, 2.idStudent, 3.idHomework
                sharedObject.getMysqlConnection().updateScore(Double.parseDouble(clientArguments[1]), Integer.parseInt(clientArguments[2]), Integer.parseInt(clientArguments[3]));
                return S + "50";

            case "C51": // ENVIO DE CORREOS EN MASA -> 0.C41, 1.Asunto, 2.Texto, 3.Fecha, 4.Hora, 5.Tipo, 6.Curso, 7.Remitente, 8.Destinatario
                messageToClient = messageOperator.sendMultipleMessages(clientArguments);

                if (messageToClient.equals("Error: Error de envío")) {
                    return S + "51.1";
                }

                return S + "51"; // S40 -> Fin del Envío

            case "C52": // LISTAR REGISTROS (MAPA)
                RecordOperator recordOperator = new RecordOperator(this, sharedObject, conversions);
                return S + "52";

            case "C53": // FINALIZAR CURSO
                return S + "53";


            case "C54": // UPDATE HOMEWORK PORCENTAJE -> 0.C, 1.Titulo Tema, 2.IdTarea, 3.Porcentaje <------------------------------------------------------------------
                messageToClient = resourceTestOperator.registerSolvedTest(clientArguments);
                return S + "54" + messageToClient;


            default:
                System.out.println("Esto nunca debería pasar realmente.");
                if (sharedObject.isClientLoged(user.getEmail())) {
                    sharedObject.removeLoggedClient(user.getEmail()); // Elimina al usuario de la lista de Logeados
                    sharedObject.removeLoggedTCPThread(user.getEmail()); // Se borra a si mismo de la lista de Threads para evitar ser interrumpido
                }
                user = new User(); // Resetea al Usuario (Para nada, porque se va a cerrar)
                //return protocolMessages.getServerMessage();
                return S + "";
        }
    }

    public String sneak(String serverResponse) {

        String[] serverNotification = serverResponse.split("#");

        switch (serverNotification[0]) {
            case "S0":
                return "Desconectado.";
            case "S1":
                return "Registrado correctamente";
            case "S1.1":
                return "ERROR: Error inesperado durante la recogida de datos.";
            case "S1.2":
                return "ERROR: Error inesperado durante el registro.";
            case "S1.3":
                return "ERROR: Email ya registrado.";
            case "S2":
                return "Inicio de Sesión realizado con éxito.";
            case "S2.1":
                return "ERROR: Error inesperado durante el inicio de sesión.";
            case "S2.2":
                return "ERROR: El usuario ya se encuentra logeado.";
            case "S3":
                return "";
            case "S4.0":
                return "";
            case "S4.1":
                return "";
            case "S4.2":
                return "";
            case "S5.0":
                return "";
            case "S5.1":
                return "";
            case "S5.2":
                return "";
            case "S6":
                return "";
            case "S6.1":
                return "";

            default:
                return "";
        }
    }

    // RECIBE MENSAJES Y ARCHIVOS
    //
/*
    public String receiveClientMessage() { // <- PARA LA VERSIÓN ANDROID
        String clientMessage = null;
        try {
            clientMessage = input.readUTF();
        } catch (IOException e) {
            // Si la comunicación cae abruptamente...
            sharedObject.removeLoggedClient(user.getEmail()); // Elimina al usuario de la lista de Logeados
            sharedObject.removeLoggedTCPThread(user.getEmail()); // Se borra a si mismo de la lista de Threads para evitar ser interrumpido

            sharedObject.getUDPThread(user.getEmail()).interrupt(); // Interrumpe a su propio hilo UDP para enviar una Notificación y cerrarlo
            sharedObject.removeLoggedUDPThread(user.getEmail());

            user = new User(); // Resetea al Usuario (Por si acaso)

            throw new RuntimeException(e);
        }
        return clientMessage;
    }
//  */


    // En Android la encriptación no funciona, desconozco el porque
    //
    public String receiveAndroidMessage() {

        String androidMessage = null;
        try {
            androidMessage = input.readUTF();
        } catch (IOException e) {
            if (sharedObject.isClientLoged(user.getEmail())) { // Cierre de comunicación
                androidMessage = "C0";
            }
        }
        return androidMessage;
    }

    public void sendResponseForAndroid(String messageToClient) {
        try {
            output.writeUTF(messageToClient);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // <- PARA LA VERSIÓN DE ESCRITORIO
    //
    public String receiveClientMessage() { // TRADUCE (byte[] a String) <- DESENCRIPTA (decryptReceivedData) <- LEE (receiveEncryptData)
        byte[] decryptedData = decryptReceivedData();
        String decryptedMessage = new String(decryptedData);
        return decryptedMessage;
    }

    public byte[] decryptReceivedData() {
        try {
            byte[] encryptedData = receiveEncryptedData();
            return decryption.decryptData(symmetricKey, encryptedData);
        } catch (Exception ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] receiveEncryptedData() {
        try {
            byte[] buffer = new byte[4];
            input.read(buffer);
            int serverMessageLength = conversions.bytesToInt(buffer);

            byte[] buffer2 = new byte[serverMessageLength];
            input.read(buffer2);

            return buffer2;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void receiveArchive(String fileName) {
        int bytes = 0;
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            long size = input.readLong(); // Lee tamaño del Archivo

            byte[] encryptedBuffer = new byte[4 * 1024];
            while (size > 0 && (bytes = input.read(encryptedBuffer, 0, (int) Math.min(encryptedBuffer.length, size))) != -1) { // Lee por Trozos
                fileOutputStream.write(encryptedBuffer, 0, bytes);
                size -= bytes;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Archivo recibido correctamente.");
    }

    // ENVIA MENSAJES Y ARCHIVOS
    /*
    public void sendServerResponse(String response){ // <- VERSION ANDROID
        try {
            output.writeUTF(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//  */


    public void sendServerResponse(String response) { // TRADUCE (String a byte[]) -> ENCRIPTA (encryptDataToSend) -> ENVIA (sendEncryptedData)
        if (os.equals("android")) {
            sendResponseForAndroid(response);
        } else {
            byte[] responseInBytes = response.getBytes();
            encryptDataToSend(responseInBytes);
        }
    }

    //  */
    public void encryptDataToSend(byte[] response) {
        try {
            byte[] encryptedResponse = encryption.encryptData(response, symmetricKey); // Encripto la respuesta
            sendEncryptedData(encryptedResponse.length, encryptedResponse);
        } catch (Exception ex) {

        }
    }

    public void sendEncryptedData(int messageLength, byte[] encryptedData) throws IOException {
        byte[] messageLengthInBytes = conversions.intToBytes(messageLength);
        byte[] message = conversions.concatByteArrays(messageLengthInBytes, encryptedData);

        output.write(message); // Envia al Cliente la respuesta Encriptada
    }

    public void sendArchive(String path) {
        try {
            System.out.println("Ruta: " + path); // TODO CHIVATO

            int bytes = 0;
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            output.writeLong(file.length()); // Manda el tamaño del archivo

            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) { // Lee y envía por trozos
                output.write(buffer, 0, bytes);
                output.flush();
            }
            fileInputStream.close();


        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);

        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }

    //
    //
    public boolean confirmPassword(String clientPass, String BBDDpass) { // Hash -> Para el Login
        return generateHash(clientPass).equals(BBDDpass);
    }

    public String generateHash(String password) { // Hash -> para el Registro/Login
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256"); // Genero el Hash (bytes[]) a partir de la contraseña
            final byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexadecimalString = new StringBuilder(2 * hash.length); // Construyo el String hexadecimal resultante
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexadecimalString.append('0');
                }
                hexadecimalString.append(hex);
            }
            return hexadecimalString.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void hybridEncryptionPreparation_Part1() {
        try {
            // 1º.- Generamos las Claves Pública y Privada:
            GeneratePrivateAndPublicKeys gkServer = new GeneratePrivateAndPublicKeys(1024);
            gkServer.createKeys();

            privateServerKey = gkServer.getPrivateKey().getEncoded(); // Las mantenemos en memoria (por si acaso)
            publicServerKey = gkServer.getPublicKey().getEncoded();

            // 2º.- Pasamos la Clave Pública al Cliente
            sendEncryptedData(publicServerKey.length, publicServerKey);
        } catch (Exception ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void hybridEncryptionPreparation_Part2() {
        try {
            // 3º.- Leemos la Clave de Sesión encriptada
            byte[] symmetricalEncryptedKey = receiveEncryptedData();

            // 4º.- Desencriptación de la Clave Simétrica del Cliente (Clave de Sesión)
            Decryption d = new Decryption();
            symmetricKey = d.decryptSymmetricKey(privateServerKey, symmetricalEncryptedKey);

            System.out.println("Encriptación Hibrida Preparada");
        } catch (Exception ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    public void prepareNotifications() {
        try {
            DatagramSocket provisionalSocketUPD = new DatagramSocket(4556); // "Socket" del Servidor
            byte[] buffer = new byte[1024];

            DatagramPacket udpClientMessage = new DatagramPacket(buffer, buffer.length);
            System.out.println("Esperando recibir datagrampacket del Cliente");
            provisionalSocketUPD.receive(udpClientMessage);
            System.out.println("Datagrampacket recibido");

            this.udpClientPort = udpClientMessage.getPort();
            this.udpClientAddress = udpClientMessage.getAddress();
            System.out.println("Puerto del Cliente: " + udpClientPort);
            System.out.println("Dirección del Cliente: " + udpClientAddress);

            provisionalSocketUPD.close();
        } catch (SocketException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     */
    public void prepareCommunication() { // ANDROID EXPLOTA CON LA ENCRIPTACIÓN
        hybridEncryptionPreparation_Part1();
        hybridEncryptionPreparation_Part2();
    }

    @Override
    public void run() {
        try {

            os = input.readUTF();
            output.writeUTF("Ok");
            System.out.println(os);

            if (!os.equals("android")) {
                prepareCommunication();
            }

            clientService();

            client.close();
            System.out.println("Cliente desconectado.");
        } catch (IOException ex) {
            System.out.println("Excepción: Error en la desconexión del Cliente.");
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
