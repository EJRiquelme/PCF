package operators;

import objects.Course;
import objects.Message;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.LinkedList;

public class MessageOperator {
    private ClientServiceThread serviceThread;
    private SharedObject sharedObject;
    private Conversions conversions;

    private int idMessage;
    private String subject;
    private String text;
    private Date sentDate; // Se pueden introducir en la BBDD como String
    private Time sentTime; // Y los recibo como String desde el Cliente

    private boolean readed;
    private String type;
    private int idTeachersCourse; // Para las solicitudes (Hay que cambiar la BBDD)
    private int idSender;
    private int idReceiver;

    public MessageOperator(ClientServiceThread serviceThread, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = serviceThread;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String sendMessage(String[] clientArguments) {
        subject = clientArguments[1];
        text = clientArguments[2];
        sentDate = conversions.convertStringToDate(clientArguments[3]); // <-------------------------------------------- Date currentDate = Date.valueOf(LocalDate.now());
        sentTime = conversions.convertStringToTime(clientArguments[4]); // <-------------------------------------------- Time currentTime = Time.valueOf(LocalTime.now());

        type = clientArguments[5];
        idTeachersCourse = Integer.parseInt(clientArguments[6]);
        idSender = Integer.parseInt(clientArguments[7]);
        idReceiver = Integer.parseInt(clientArguments[8]);

        Message message = new Message(subject, text, sentDate, sentTime, type, idTeachersCourse, idSender, idReceiver);

        if (!sharedObject.getMysqlConnection().registerMessage(message)) {
            return "Error: Error al registrar mensaje"; // S7.1 -> ERROR AL ENVIAR EL MENSAJE
        }

        String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(Integer.parseInt(clientArguments[8])); // Si el destinatario esta logeado se le notifica
        notifyReceiver(emailToInterrupt);
        return "Ok";
    }

    public String sendRequest(String[] clientArguments) {
        subject = clientArguments[1];
        text = clientArguments[2];
        sentDate = conversions.convertStringToDate(clientArguments[3]);
        sentTime = conversions.convertStringToTime(clientArguments[4]);

        type = clientArguments[5];
        idTeachersCourse = Integer.parseInt(clientArguments[6]);
        idSender = Integer.parseInt(clientArguments[7]);
        idReceiver = Integer.parseInt(clientArguments[8]);

        Message message = new Message(subject, text, sentDate, sentTime, type, idTeachersCourse, idSender, idReceiver);

        if (sharedObject.getMysqlConnection().isStudentRegistered(message.getIdSender(), message.getIdTeachersCourse())) { // Si está registrado ya en el Curso
            return "Error: Estudiante ya registrado";
        }
        if (sharedObject.getMysqlConnection().isSolicitudeAlreadyLaunched(message)) { // Si ya hay otra Solicitud al mismo Curso por parte del mismo Solicitante
            return "Error: Solicitud ya enviada";
        }
        if (!sharedObject.getMysqlConnection().registerSolicitude(message)) {
            return "Error: Error al registrar solicitud";
        }

        String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(Integer.parseInt(clientArguments[8])); // Si el destinatario esta logeado se le notifica
        notifyReceiver(emailToInterrupt);
        return "Ok";
    }

    // nombreCurso, idProfesor, idAlumnoPotencial
    //
    public String sendInvitation(String[] clientArguments) {
        String courseName = clientArguments[1];
        subject = clientArguments[2];
        text = clientArguments[3];
        sentDate = conversions.convertStringToDate(clientArguments[4]);
        sentTime = conversions.convertStringToTime(clientArguments[5]);
        type = clientArguments[6];
        idSender = Integer.parseInt(clientArguments[7]);
        idReceiver = Integer.parseInt(clientArguments[8]);

        Course course = new Course(courseName, idSender);
        idTeachersCourse = sharedObject.getMysqlConnection().getIdCourse(course);

        if (idTeachersCourse == -1) {
            return ".1" + "#" + "Error: Curso inexistente";
        }

        Message message = new Message(subject, text, sentDate, sentTime, type, idTeachersCourse, idSender, idReceiver);

        if (sharedObject.getMysqlConnection().isStudentRegistered(idReceiver, idTeachersCourse)) { // Si está registrado ya en el Curso
            return ".2" + "#" + "Error: Estudiante ya registrado";
        }
        if (sharedObject.getMysqlConnection().isInvitationAlreadyLaunched(idTeachersCourse, idSender, idReceiver)) { // Si ya hay otra Invitación al mismo Curso por parte del mismo Solicitante
            return ".3" + "#" + "Error: Invitación ya enviada";
        }
        if (!sharedObject.getMysqlConnection().registerInvitation(message)) {
            return ".4" + "#" + "Error: Error al enviar la invitación";
        }
        String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(idReceiver); // Si el destinatario esta logeado se le notifica
        notifyReceiver(emailToInterrupt);
        return "#" + "Ok";
    }


    public void notifyReceiver(String emailToInterrupt) {
        if (sharedObject.isClientLoged(emailToInterrupt)) {
            sharedObject.getServerUDPThreads().get(emailToInterrupt).interrupt();
        }
    }

    public String inboxReceivedMessages(String[] clientArguments) {
        idReceiver = Integer.parseInt(clientArguments[1]);
        LinkedList<Message> inboxMessagesAsReceiver = sharedObject.getMysqlConnection().searchMultipleMessagesAsReceiver(idReceiver);

        if (inboxMessagesAsReceiver == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(10) + "#" + inboxMessagesAsReceiver.size()); // S8#Cantidad -> Mensajes

        for (Message m : inboxMessagesAsReceiver) {
            String strDate = conversions.convertDateToString(m.getSentDate());
            String strTime = conversions.convertTimeToString(m.getSentTime());

            String result = m.getIdMessage()
                    + "#" + m.getSubject()
                    + "#" + m.getText()
                    + "#" + strDate
                    + "#" + strTime
                    + "#" + m.isReaded()
                    + "#" + m.getType()
                    + "#" + m.getIdTeachersCourse()
                    + "#" + m.getSenderReceiverName()
                    + "#" + m.getIdSender()
                    + "#" + m.getIdReceiver();
            System.out.println(result); // TODO
            serviceThread.sendServerResponse(result); // MensajeConInfo -> Mensaje enviado
        }
        return "Ok";
    }

    public String inboxSendedMessages(String[] clientArguments) {
        idSender = Integer.parseInt(clientArguments[1]);
        LinkedList<Message> inboxMessagesAsSender = sharedObject.getMysqlConnection().searchMultipleMessagesAsSender(idSender);

        if (inboxMessagesAsSender == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(11) + "#" + inboxMessagesAsSender.size()); // S8#Cantidad -> Mensajes

        for (Message m : inboxMessagesAsSender) {
            String strDate = conversions.convertDateToString(m.getSentDate());
            String strTime = conversions.convertTimeToString(m.getSentTime());

            String result = m.getIdMessage()
                    + "#" + m.getSubject()
                    + "#" + m.getText()
                    + "#" + strDate
                    + "#" + strTime
                    + "#" + m.isReaded()
                    + "#" + m.getType()
                    + "#" + m.getIdTeachersCourse()
                    + "#" + m.getSenderReceiverName()
                    + "#" + m.getIdSender()
                    + "#" + m.getIdReceiver();
            serviceThread.sendServerResponse(result); // MensajeConInfo -> Mensaje enviado
        }
        return "Ok";
    }

    public String readMessage(String[] clientArguments) {
        idMessage = Integer.parseInt(clientArguments[1]);

        if (!sharedObject.getMysqlConnection().readMessage(idMessage)) {
            return "Error: Error de actualización";
        }
        return "Ok";
    }

    public String deleteMessage(String[] clientArguments) {
        idMessage = Integer.parseInt(clientArguments[1]);
        boolean isSender = Boolean.parseBoolean(clientArguments[2]);

        boolean deleteResult = sharedObject.getMysqlConnection().deleteMessage(idMessage, isSender);

        if (!deleteResult) {
            return "Error: Error de borrado";
        }
        return "Ok";
    }

    public String acceptSolicitude(String[] clientArguments) {
        subject = clientArguments[5];
        text = clientArguments[6];
        sentDate = conversions.convertStringToDate(clientArguments[7]);
        sentTime = conversions.convertStringToTime(clientArguments[8]);
        int idSolicitudeSender = Integer.parseInt(clientArguments[4]);
        int idSolicitudeReceiver = Integer.parseInt(clientArguments[1]);

        String registeringResult = sharedObject.getMysqlConnection().registerStudent(Integer.parseInt(clientArguments[1]), Integer.parseInt(clientArguments[2])); // Si se acepta

        if (registeringResult.equals("Error")) {
            return "Error: Error de registro";
        } else if (registeringResult.equals("Clave")) {
            return "Error: Clave duplicada";
        }

        boolean deleteResult = sharedObject.getMysqlConnection().deleteMessage(Integer.parseInt(clientArguments[3]), false); // Se borra la Solicitud

        if (!deleteResult) {
            return "Error: Error de borrado";
        }

        Message message = new Message(subject, text, sentDate, sentTime, "message", idSolicitudeSender, idSolicitudeReceiver);

        boolean messageResult = sharedObject.getMysqlConnection().registerAcceptedSolicitude(message); // Y se envia un mensaje al Solicitante

        if (!messageResult) {
            return "Error: Error de notificación";
        }

        String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(Integer.parseInt(clientArguments[1])); // Si el destinatario esta logeado se le notifica
        notifyReceiver(emailToInterrupt);

        return "Ok";
    }

    public String acceptInvitation(String[] clientArguments) {
        idReceiver = Integer.parseInt(clientArguments[1]);
        idTeachersCourse = Integer.parseInt(clientArguments[2]);
        idMessage = Integer.parseInt(clientArguments[3]);
        idSender = Integer.parseInt(clientArguments[4]);
        subject = clientArguments[5];
        text = clientArguments[6];
        sentDate = conversions.convertStringToDate(clientArguments[7]);
        sentTime = conversions.convertStringToTime(clientArguments[8]);

        String registeringResult = sharedObject.getMysqlConnection().registerStudent(idReceiver, idTeachersCourse); // Si se acepta

        if (registeringResult.equals("Error")) {
            return ".1" + "#" + "Error: Error de registro.";
        } else if (registeringResult.equals("Clave")) {
            return ".2" + "#" + "Error: Estudiante ya registrado.";
        }

        boolean deleteResult = sharedObject.getMysqlConnection().deleteMessage(idMessage, false); // Se borra la Invitación

        if (!deleteResult) {
            return ".3" + "#" + "Error: Error de borrado.";
        }

        Message message = new Message(subject, text, sentDate, sentTime, "message", idReceiver, idSender);

        boolean messageResult = sharedObject.getMysqlConnection().registerAcceptedSolicitude(message); // Y se envia un mensaje al que envió la Invitación

        if (!messageResult) {
            return ".4" + "#" + "Error: Error de notificación.";
        }

        String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(idReceiver); // Si el destinatario esta logeado se le notifica
        notifyReceiver(emailToInterrupt);

        return "#" + "Ok";
    }

    public String sendMultipleMessages(String[] clientArguments) {
        java.sql.Date sqlDateMassSend = conversions.convertStringToDate(clientArguments[3]); // Date currentDate = date.valueOf(LocalDate.now());
        java.sql.Time sqlTimeMassSend = conversions.convertStringToTime(clientArguments[4]); // Time currentTime = Time.valueOf(LocalTime.now());

        LinkedList<Integer> studentsIds = sharedObject.getMysqlConnection().getCourseStudents(Integer.parseInt(clientArguments[6]));

        for (Integer i : studentsIds) {
            Message messageMassSend = new Message(clientArguments[1],
                    clientArguments[2],
                    sqlDateMassSend,
                    sqlTimeMassSend,
                    "message",
                    Integer.parseInt(clientArguments[6]),
                    Integer.parseInt(clientArguments[7]),
                    i);

            if (sharedObject.getMysqlConnection().registerMassMessage(messageMassSend)) {
                String emailToInterrupt = sharedObject.getMysqlConnection().getUserEmail(Integer.parseInt(clientArguments[8])); // Si el destinatario esta logeado se le notifica
                if (sharedObject.isClientLoged(emailToInterrupt)) {
                    sharedObject.getServerUDPThreads().get(emailToInterrupt).interrupt();
                }
            }
        }

        return "Ok";
    }
}
