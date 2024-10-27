/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class SQLMessage {

    /*
    CREATE TABLE pcf_message(
        id_message INT AUTO_INCREMENT PRIMARY KEY,
        subject_message VARCHAR(50) NOT NULL,
        text_message VARCHAR(5000) NOT NULL,
        sent_date_message DATE NOT NULL,
        sent_time_message TIME NOT NULL,
        readed_message BOOLEAN NOT NULL,
        type_message VARCHAR(50) NOT NULL, -- Mensaje / Solicitud / Invitación
        id_sender_user INT NOT NULL,
        id_receiver_user INT NOT NULL,
        deleted_by_sender_message BOOLEAN DEFAULT false, -- Para los borrados
	    deleted_by_receiver_message BOOLEAN DEFAULT false
    );
     */
    private Connection connection;
    private PreparedStatement pstm;
    private String table;
    private String[] columns;
    private String primaryKey;
    private DateFormat dateFormat;
    private DateFormat timeFormat;

    public SQLMessage(Connection connection) {
        this.connection = connection;
        this.table = "pcf_message";
        this.columns = new String[]{"id_message", // 0
                "subject_message", // 1
                "text_message", // 2
                "sent_date_message", // 3
                "sent_time_message", // 4
                "readed_message", // 5
                "type_message", // 6
                "id_teacher_course", // 7
                "id_sender_user", // 8
                "id_receiver_user", // 9
                "deleted_by_sender_message", // 10
                "deleted_by_receiver_message"}; // 11
        this.primaryKey = columns[0];
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        this.timeFormat = new SimpleDateFormat("HH:mm:ss");
    }

    // ENVIAR MENSAJE: HECHO
    public boolean registerMessage(Message message) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", " // <- !5: readed_message DEFAULT false
                    + columns[6] + ", " // <- !7: id_teacher_course puede ser null
                    + columns[8] + ", "
                    + columns[9] + ") VALUES (?, ?, ?, ?, ?, ?, ?);"); // <- !10 && !11: deleted_by_sender_message/deleted_by_receiver_message DEFAULT false

            pstm.setString(1, message.getSubject());
            pstm.setString(2, message.getText());
            pstm.setDate(3, message.getSentDate());
            pstm.setTime(4, message.getSentTime());
            pstm.setString(5, message.getType());
            pstm.setInt(6, message.getIdSender());
            pstm.setInt(7, message.getIdReceiver());

            pstm.executeUpdate();

            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean registerAcceptedSolicitude(Message message) { // Eliminado Automáticamente de la Bandeja de Salida del Profesor (Para no tener 20 copias del mismo mensaje)
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", " // <- !5: readed_message DEFAULT false
                    + columns[6] + ", " // <- !7: id_teacher_course puede ser null
                    + columns[8] + ", "
                    + columns[9] + ", "
                    + columns[10] + ") VALUES (?, ?, ?, ?, ?, ?, ?, true);"); // <- !11: deleted_by_receiver_message DEFAULT false

            pstm.setString(1, message.getSubject());
            pstm.setString(2, message.getText());
            pstm.setDate(3, message.getSentDate());
            pstm.setTime(4, message.getSentTime());
            pstm.setString(5, message.getType());
            pstm.setInt(6, message.getIdSender());
            pstm.setInt(7, message.getIdReceiver());

            pstm.executeUpdate();

            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    public boolean registerMassMessage(Message message) { // Eliminado Automáticamente de la Bandeja de Salida del Profesor (Para no tener 20 copias del mismo mensaje)
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", " // <- !5: readed_message DEFAULT false
                    + columns[6] + ", " // <- !7: id_teacher_course puede ser null
                    + columns[8] + ", "
                    + columns[9] + ", "
                    + columns[10] + ") VALUES (?, ?, ?, ?, ?, ?, ?, true);"); // <- !11: deleted_by_receiver_message DEFAULT false

            pstm.setString(1, message.getSubject());
            pstm.setString(2, message.getText());
            pstm.setDate(3, message.getSentDate());
            pstm.setTime(4, message.getSentTime());
            pstm.setString(5, message.getType());
            pstm.setInt(6, message.getIdSender());
            pstm.setInt(7, message.getIdReceiver());

            pstm.executeUpdate();

            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    public boolean isSolicitudeAlreadyLaunched(Message message) {
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[6] + " = 'request'" // Tipo request
                    + " AND " + columns[7] + " = ?" // id Curso
                    + " AND " + columns[8] + " = ?" // id Solicitante
                    + " AND " + columns[9] + " = ?;"); // id Profesor (Este sobra realmente, pero porsiaca)

            pstm.setInt(1, message.getIdTeachersCourse());
            pstm.setInt(2, message.getIdSender());
            pstm.setInt(3, message.getIdReceiver());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return true; // Sí hay coincidencia, el Curso ya está Solicitado
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isInvitationAlreadyLaunched(int idCourse, int idSender, int idReceiver) {
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[6] + " = 'invitation'" // Tipo Invitación
                    + " AND " + columns[7] + " = ?" // id Curso
                    + " AND " + columns[8] + " = ?" // id Solicitante
                    + " AND " + columns[9] + " = ?;"); // id Profesor (Este sobra realmente, pero porsiaca)

            pstm.setInt(1, idCourse);
            pstm.setInt(2, idReceiver);
            pstm.setInt(3, idSender);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return true; // Sí hay coincidencia, el Curso ya está Solicitado
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // ENVIAR SOLICITUD: Hecho
    public boolean registerSolicitude(Message message) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", " // <- !5: readed_message DEFAULT false
                    + columns[6] + ", "
                    + columns[7] + ", "
                    + columns[8] + ", "
                    + columns[9] + ", "
                    + columns[10] + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, true);"); // <- !11: deleted_by_receiver_message DEFAULT false

            pstm.setString(1, message.getSubject());
            pstm.setString(2, message.getText());
            pstm.setDate(3, message.getSentDate());
            pstm.setTime(4, message.getSentTime());
            pstm.setString(5, message.getType());
            pstm.setInt(6, message.getIdTeachersCourse());
            pstm.setInt(7, message.getIdSender());
            pstm.setInt(8, message.getIdReceiver());

            pstm.executeUpdate();

            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    public boolean registerInvitation(Message message) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", " // <- !5: readed_message DEFAULT false
                    + columns[6] + ", "
                    + columns[7] + ", "
                    + columns[8] + ", "
                    + columns[9] + ", "
                    + columns[10] + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, true);"); // <- !11: deleted_by_receiver_message DEFAULT false

            pstm.setString(1, message.getSubject());
            pstm.setString(2, message.getText());
            pstm.setDate(3, message.getSentDate());
            pstm.setTime(4, message.getSentTime());
            pstm.setString(5, message.getType());
            pstm.setInt(6, message.getIdTeachersCourse());
            pstm.setInt(7, message.getIdSender());
            pstm.setInt(8, message.getIdReceiver());

            pstm.executeUpdate();

            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

        // Borrado parte 1: HECHO
    public boolean isDeletedBySender(int idMessage) {
        try {
            pstm = connection.prepareStatement("SELECT " + columns[10] + " FROM " + table + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idMessage);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getBoolean(columns[10]); // Borrado por Sender = True
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // Borrado Parte 1.2: HECHO
    public boolean isDeletedByReceiver(int idMessage) {
        try {
            pstm = connection.prepareStatement("SELECT " + columns[11] + " FROM " + table + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idMessage);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getBoolean(columns[11]); // Borrado por Receiver = True
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    // "Borrar" Mensaje recibido (eliminar Remitente/Destinatario [Si ambos son eliminados se elimina el mensaje por completo]
    public boolean deleteMessage(int idMessage, boolean isSender) {
        try {
            if (isSender) {
                if (isDeletedByReceiver(idMessage)) {
                    pstm = connection.prepareStatement("Delete FROM " + table + " WHERE " + primaryKey + " = ?;"); // Elimina el mensaje de forma definitiva
                } else {
                    pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[10] + " = true WHERE " + primaryKey + " = ?;"); // Borrado para Sender
                }
            } else {
                if (isDeletedBySender(idMessage)) {
                    pstm = connection.prepareStatement("Delete FROM " + table + " WHERE " + primaryKey + " = ?;"); // Elimina el mensaje de forma definitiva
                } else {
                    pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[11] + " = true WHERE " + primaryKey + " = ?;"); // Borrado para Receiver
                }
            }
            pstm.setInt(1, idMessage);
            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // Notificación en cada pantalla, y por interrupción: HECHO
    public int messagesToBeReaden(int idUser) { // Bandeja de Entrada -> Click para mirar 1 de ellos
        int howMany = 0;

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[9] + " = ? AND " + columns[5] + " = false;");

            pstm.setInt(1, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    howMany++;
                }
                return howMany;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de consulta.");
        }
        return 0;
    }

    // Lectura de mensaje: HECHO
    public boolean readMessage(int idMessage) {
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[5] + " = true WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idMessage);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // Bandeja de Entrada -> Click para mirar 1 de ellos: HECHO
    public LinkedList<Message> searchMultipleMessagesAsReceiver(int idUser) {
        LinkedList<Message> list = new LinkedList();

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + ", pcf_user"
                    + " WHERE pcf_message.id_sender_user = pcf_user.id_user"
                    + " AND " + columns[9] + " = ?"
                    + " AND " + columns[11] + " = false"
                    + " ORDER BY " + columns[3] + ";");

            pstm.setInt(1, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idMessage = rst.getInt(columns[0]);
                    String subject = rst.getString(columns[1]);
                    String text = rst.getString(columns[2]);
                    Date sqlDate = rst.getDate(columns[3]);
                    Time sqlTime = rst.getTime(columns[4]);
                    boolean readed = rst.getBoolean(columns[5]);
                    String type = rst.getString(columns[6]);
                    int course = rst.getInt(columns[7]);
                    int sender = rst.getInt(columns[8]);
                    int receiver = rst.getInt(columns[9]);
                    String senderName = rst.getString("name_user");

                    list.add(new Message(idMessage, subject, text, sqlDate, sqlTime, readed, type, course, sender, receiver, senderName));
                }
                return list;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Bandeja de Entrada -> Click para mirar 1 de ellos: HECHO
    public LinkedList<Message> searchMultipleMessagesAsSender(int idUser) {
        LinkedList<Message> list = new LinkedList();

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + ", pcf_user"
                    + " WHERE pcf_message.id_receiver_user = pcf_user.id_user"
                    + " AND " + columns[8] + " = ?"
                    + " AND " + columns[10] + " = false"
                    + " ORDER BY " + columns[3] + ";");

            pstm.setInt(1, idUser);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idMessage = rst.getInt(columns[0]);
                    String subject = rst.getString(columns[1]);
                    String text = rst.getString(columns[2]);
                    Date sqlDate = rst.getDate(columns[3]);
                    Time sqlTime = rst.getTime(columns[4]);
                    boolean readed = rst.getBoolean(columns[5]);
                    String type = rst.getString(columns[6]);
                    int course = rst.getInt(columns[7]);
                    int sender = rst.getInt(columns[8]);
                    int receiver = rst.getInt(columns[9]);
                    String receiverName = rst.getString("name_user");

                    list.add(new Message(idMessage, subject, text, sqlDate, sqlTime, readed, type, course, sender, receiver, receiverName));
                }
                return list;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
