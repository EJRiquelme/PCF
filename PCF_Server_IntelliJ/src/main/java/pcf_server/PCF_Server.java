/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pcf_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author william
 */
public class PCF_Server {

    private int port;
    private ServerSocket server;
    private boolean active;
    private SharedObject boltSharedObject; // Clase Compartida por los Hilos "ClientServiceThread", incluye la conexión a la BBDD

    // CONSTRUCTOR
    public PCF_Server() throws IOException {
        this.port = 4445;
        this.server = new ServerSocket(port);
        this.active = true;
        this.boltSharedObject = new SharedObject();
    }

    // GETTERS Y SETTERS
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // OPERACIONES
    public void attendClient() {
        try {
            while (active) {
                System.out.println("Esperando a Cliente...");
                Socket client = server.accept(); // A la espera para crear el Socket Cliente
                System.out.println("Cliente Conectado.");
                launchService(client);
            }
        } catch (IOException ex) {
            System.out.println("Excepción: Error en la conexión del Cliente.");
            Logger.getLogger(PCF_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void launchService(Socket client) {
        try {
            ClientServiceThread serviceTCP = new ClientServiceThread(boltSharedObject, client); // Aquí manejo las excepciones de crear el DataInput y DataOutput de ClientServiceThread
            serviceTCP.start();
        } catch (IOException ex) {
            System.out.println("Excepción: Error en la apertura de los flujos de entrada y salida del Cliente.");
            Logger.getLogger(PCF_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        try {
            PCF_Server pcfServer = new PCF_Server();
            String os = System.getProperty("os.name");
            System.out.println(os);
            System.out.println("Servidor activo.");
            
            pcfServer.boltSharedObject.connect();
            pcfServer.attendClient();
            pcfServer.boltSharedObject.disconnect();
        } catch (IOException ex) {
            System.out.println("Excepción: Error al iniciar el Servidor.");
            Logger.getLogger(PCF_Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
