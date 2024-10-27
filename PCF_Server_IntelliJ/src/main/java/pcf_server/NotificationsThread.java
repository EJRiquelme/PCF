/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pcf_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author william
 */
public class NotificationsThread extends Thread {

    private SharedObject sharedObject;

    private int udpClientPort; // Para Notificaciones
    private InetAddress udpClientAddress;
    private String mail;
    private String udpMessage;

    public NotificationsThread(SharedObject sharedObject, String mail) {
        this.sharedObject = sharedObject;
        this.mail = mail;
    }

    public void waitUntilMessage() {
        try {
            synchronized (this) { // wait hasta recibir un Mensaje de Otro Cliente (Ser interrumpido)
                wait();
            }
        } catch (InterruptedException ex) {
            if (sharedObject.isClientLoged(mail)) { // Si esta logeado Recursión, y si no, No (Para cerrar la comunicación)
                udpMessage = "A";
                notifyClient();
                waitUntilMessage();
            } else {
                udpMessage = "";
                notifyClient();
            }
        }
    }

    public void notifyClient() { // UDP
        try {
            System.out.println("Notificación en curso.");
            
            byte[] buffer = new byte[1024];
            
            System.out.println(udpMessage);
            buffer = udpMessage.getBytes();

            DatagramSocket serverUdpSocket = new DatagramSocket();
            DatagramPacket notification = new DatagramPacket(buffer, buffer.length, udpClientAddress, udpClientPort);
            serverUdpSocket.send(notification);
            System.out.println("Notificación UDP enviada.");

            serverUdpSocket.close();
        } catch (SocketException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientServiceThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    @Override
    public void run() {
        System.out.println("Hilo NotificationsThread Iniciado.");
        prepareNotifications();
        waitUntilMessage();
        System.out.println("Hilo NotificationsThread Finalizado.");
    }
}
