/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pcf_server;

import java.io.File;
import java.net.Socket;
import java.util.TreeMap;

import jdbc_mysql.PreparedConnection;

/**
 * @author william
 */
public class SharedObject {

    /*
    private TreeMap<String, Socket> loggedClients;
    private TreeMap<String, String[]> message;
     */
    private TreeMap<String, Socket> loggedClients; // (Email, Cliente) -> Clientes Logeados
    private TreeMap<String, Thread> serverTCPThreads; // (Email, Hilos ClientServiceThread) -> Por si acaso
    private TreeMap<String, NotificationsThread> serverUDPThreads; // (Email, Hilos ClientServiceThread) -> Para interrupciones
    private PreparedConnection mysqlConnection;
    private ProtocolMessages protocolMessages;

    // CONSTRUCTOR
    public SharedObject() {
        this.loggedClients = new TreeMap<>();
        this.serverTCPThreads = new TreeMap<>();
        this.serverUDPThreads = new TreeMap<>();
        this.mysqlConnection = new PreparedConnection();
        this.protocolMessages = new ProtocolMessages();
    }

    // GETTERS Y SETTERS
    public synchronized TreeMap<String, Socket> getLoggedClients() {
        return loggedClients;
    }

    public synchronized void setLoggedClients(TreeMap<String, Socket> loggedClients) {
        this.loggedClients = loggedClients;
    }

    public synchronized TreeMap<String, Thread> getServerTCPThreads() {
        return serverTCPThreads;
    }

    public synchronized void setServerTCPThreads(TreeMap<String, Thread> serverTCPThreads) {
        this.serverTCPThreads = serverTCPThreads;
    }

    public synchronized TreeMap<String, NotificationsThread> getServerUDPThreads() { // Synchronized: No puede haber dos notificaciones simultaneas
        return serverUDPThreads;
    }

    public synchronized void setServerUDPThreads(TreeMap<String, NotificationsThread> serverUDPThreads) {
        this.serverUDPThreads = serverUDPThreads;
    }

    public synchronized PreparedConnection getMysqlConnection() { // Para TODAS las operaciones con la BBDD
        return mysqlConnection;
    }

    public synchronized void setMysqlConnection(PreparedConnection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
    }

    public ProtocolMessages getProtocolMessages() {
        return protocolMessages;
    }

    public void setProtocolMessages(ProtocolMessages protocolMessages) {
        this.protocolMessages = protocolMessages;
    }

    // OPERACIONES SOCKETS
    public synchronized boolean isClientLoged(String email) { // ¿Cliente Logeado?
        return loggedClients.containsKey(email);
    }

    public synchronized void putLoggedClient(String mail, Socket socket) { // Logear Cliente
        this.loggedClients.put(mail, socket);
    }

    public synchronized void removeLoggedClient(String mail) { // Logout Cliente
        this.loggedClients.remove(mail);
    }

    // OPERACIONES THREADS
    public synchronized boolean searchTCPThread(String mail) { // Busca Hilo TCP
        return serverTCPThreads.containsKey(mail);
    }

    public synchronized void putLoggedTCPThread(String mail, ClientServiceThread threadTCP) { // "Login" del Hilo
        this.serverTCPThreads.put(mail, threadTCP);
    }

    public synchronized void removeLoggedTCPThread(String mail) { // "Logout" del Hilo
        this.serverTCPThreads.remove(mail);
    }

    public synchronized NotificationsThread getUDPThread(String mail) {
        return serverUDPThreads.get(mail);
    }

    public synchronized boolean searchUDPThread(String mail) { // Busca Hilo UDP -> Para interrupciones
        return serverUDPThreads.containsKey(mail);
    }

    public synchronized void putLoggedUDPThread(String mail, NotificationsThread threadUDP) { // "Login" del Hilo -> Para interrupciones
        this.serverUDPThreads.put(mail, threadUDP);
    }

    public synchronized void removeLoggedUDPThread(String mail) { // "Logout" del Hilo -> Para evitar interrupciones que no van a ninguna parte
        this.serverUDPThreads.remove(mail);
    }

    // OPERACIONES BBDD -> CONEXIÓN
    public synchronized void connect() {
        mysqlConnection.connect();
    }

    public synchronized void disconnect() {
        mysqlConnection.disconnect();
    }

    // OPERACIONES ARCHIVOS
    public synchronized boolean deleteDirectory(File directoryToBeDeleted) { // Borrado de Directorios
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public synchronized void deleteFile(File directory) { // Borrado de Archivos (Actualizaciones de Archivos[Material] y Tareas[Alumno])
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
        }
        directory.delete();
    }
}
