/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import pcf_server.ClientServiceThread;

/**
 *
 * @author william
 */
public class Conversions {

    public java.sql.Time convertStringToTime(String str) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            Time parsedTime = new Time(timeFormat.parse(str).getTime());
            java.sql.Time sqlTime = new java.sql.Time(parsedTime.getTime());
            return sqlTime;
        } catch (ParseException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String convertTimeToString(java.sql.Time time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String strDate = timeFormat.format(time);
        return strDate;
    }

    public java.sql.Date convertStringToDate(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = dateFormat.parse(str);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return sqlDate;
        } catch (ParseException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String convertDateToString(java.sql.Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = dateFormat.format(date);
        return strDate;
    }

    public LocalDate convertStringToLocalDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(str, formatter);
        return localDate;
    }

    public String convertLocalDateToString(LocalDate localDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = localDate.format(dateFormatter);
        return date;
    }

    public LocalTime convertStringToLocalTime(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.parse(str, formatter);
        return localTime;
    }

    public String convertLocalTimeToString(LocalTime localTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = localTime.format(timeFormatter);
        return time;
    }

    /*
    public Date convertStringToDate(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = dateFormat.parse(str);

            return date;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }
     */
    public byte[] concatByteArrays(byte[] a, byte[] b) throws IOException { // Para los mensajes que se envían
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);

        byte c[] = outputStream.toByteArray();
        return c;
    }

    public int bytesToInt(byte[] bytes) { // Para los mensajes recibidos
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int test = buffer.getInt();
        return test;
    }

    public byte[] intToBytes(int num) { // Para los mensajes que se envían
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(num);
        byte[] test = buffer.array();
        return test;
    }
}
