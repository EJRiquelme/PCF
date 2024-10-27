package objects;

import java.time.LocalDate;
import java.time.LocalTime;

public class Message {

    /*
    CREATE TABLE pcf_message(
        id_menssage INT AUTO_INCREMENT PRIMARY KEY,
        subject_message VARCHAR(50) NOT NULL,
        text_message VARCHAR(5000) NOT NULL,
        sent_date_message DATE NOT NULL,
        sent_time_message TIME NOT NULL,
        readed_message BOOLEAN NOT NULL,
        type_message VARCHAR(50) NOT NULL, -- Mensaje / Solicitud / Invitación
        id_sender_user INT NOT NULL,
        id_receiver_user INT NOT NULL
    );
     */
    private int idMessage;
    private String subject;
    private String text;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private boolean readed;
    private String type;
    private int idTeachersCourse; // Para las solicitudes
    private int idSender;
    private int idReceiver;
    private String senderReceiverName;

    // CONSTRUCTOR
    public Message() {
    }

    // Mensajes Corrientes
    public Message(String subject, String text, LocalDate sentDate, LocalTime sentTime, String type, int idSender, int idReceiver) {
        this.subject = subject;
        this.text = text;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.type = type;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
    }

    // Solicitudes
    public Message(String subject, String text, LocalDate sentDate, LocalTime sentTime, String type, int idTeachersCourse, int idSender, int idReceiver) {
        this.subject = subject;
        this.text = text;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.type = type;
        this.idTeachersCourse = idTeachersCourse;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
    }

    public Message(int idMessage, String subject, String text, LocalDate sentDate, LocalTime sentTime, boolean readed, String type, int idTeachersCourse, int idSender, int idReceiver) {
        this.idMessage = idMessage;
        this.subject = subject;
        this.text = text;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.readed = readed;
        this.type = type;
        this.idTeachersCourse = idTeachersCourse;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
    }

    // Bandeja de entrada
    public Message(int idMessage, String subject, String text, LocalDate sentDate, LocalTime sentTime, boolean readed, String type, int idTeachersCourse, String senderReceiverName, int idSender, int idReceiver) {
        this.idMessage = idMessage;
        this.subject = subject;
        this.text = text;
        this.sentDate = sentDate;
        this.sentTime = sentTime;
        this.readed = readed;
        this.type = type;
        this.idTeachersCourse = idTeachersCourse;
        this.senderReceiverName = senderReceiverName;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
    }

    // GETTERS & SETTERS
    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }

    public LocalTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalTime sentTime) {
        this.sentTime = sentTime;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIdTeachersCourse() {
        return idTeachersCourse;
    }

    public void setIdTeachersCourse(int idTeachersCourse) {
        this.idTeachersCourse = idTeachersCourse;
    }

    public String getSenderReceiverName() {
        return senderReceiverName;
    }

    public void setSenderReceiverName(String senderReceiverName) {
        this.senderReceiverName = senderReceiverName;
    }

    public int getIdSender() {
        return idSender;
    }

    public void setIdSender(int idSender) {
        this.idSender = idSender;
    }

    public int getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(int idReceiver) {
        this.idReceiver = idReceiver;
    }

}
