package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.Message;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class MessagesOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    private Message message;

    private int idMessage;
    private String subject;
    private String text;
    private LocalDate sentDate;
    private LocalTime sentTime;
    private boolean readed;
    private String type;
    private int idTeachersCourse; // Para las solicitudes
    private String senderReceiverName;
    private int idSender;
    private int idReceiver;

    public MessagesOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    // MENSAJES RECIBIDOS -> 0.S9, 1.CantidadMensajesRecibidos
    //
    //
    public void viewInbox(String[] serverArguments){
        int mesagesReceivedQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setInboxReceivedList(new LinkedList<>()); // Reinicio la lista

        for (int i = 0; i < mesagesReceivedQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idMessage = Integer.parseInt(finalArguments[0]);
            subject = finalArguments[1];
            text = finalArguments[2];
            sentDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[3]);
            sentTime = sharedStore.getConversions().convertStringToLocalTime(finalArguments[4]);
            readed = Boolean.parseBoolean(finalArguments[5]);
            type = finalArguments[6];
            idTeachersCourse = Integer.parseInt(finalArguments[7]);
            senderReceiverName = finalArguments[8];
            idSender = Integer.parseInt(finalArguments[9]);
            idReceiver = Integer.parseInt(finalArguments[10]);

            message = new Message(idMessage, subject, text, sentDate, sentTime, readed, type, idTeachersCourse, senderReceiverName, idSender, idReceiver);
            sharedStore.getInboxReceivedList().add(message); // Almacenamos los Mensajes Recibidos
        }
        connectionToServer.receiveServerResponse(); // Fin del envio -> S9.0
    }

    // MENSAJES ENVIADOS -> 0.S10, 1.CantidadMensajesEnviados
    //
    //
    public void viewOutbox(String[] serverArguments){
        int mesagesSendedQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setInboxSentList(new LinkedList<>()); // Reinicio la lista

        for (int i = 0; i < mesagesSendedQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idMessage = Integer.parseInt(finalArguments[0]);
            subject = finalArguments[1];
            text = finalArguments[2];
            sentDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[3]);
            sentTime = sharedStore.getConversions().convertStringToLocalTime(finalArguments[4]);
            readed = Boolean.parseBoolean(finalArguments[5]);
            type = finalArguments[6];
            idTeachersCourse = Integer.parseInt(finalArguments[7]);
            senderReceiverName = finalArguments[8];
            idSender = Integer.parseInt(finalArguments[9]);
            idReceiver = Integer.parseInt(finalArguments[10]);

            message = new Message(idMessage, subject, text, sentDate, sentTime, readed, type, idTeachersCourse, senderReceiverName, idSender, idReceiver);
            sharedStore.getInboxSentList().add(message); // Almacenamos los Mensajes Recibidos
        }
        connectionToServer.receiveServerResponse(); // Fin del envio -> S10.0
    }
}
