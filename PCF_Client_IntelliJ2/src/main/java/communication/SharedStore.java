package communication;


import javafx.scene.image.Image;
import objects.*;
import pcf_client.executables.App;
import tools.*;

import java.util.*;

/**
 * @author william
 */
public class SharedStore {

    private static SharedStore sharedStore; // <--- Patrón Singleton

    private LinkedList<TimerThread> timers;

    private App app; // Para los Controllers

    private ProtocolMessages protocolMessages;
    private String clientMessage; // Mensaje del Cliente al Servidor
    private boolean listening; // Para el Hilo ListenServer (UDP)
    private boolean answered; // Para el Hilo Temporizador
    private ResponseResult responseResult; // Alertas y Notificaciones al Humano en dependencia de la respuesta del Servidor

    private Conversions conversions;
    private String comeback;

    private String[] searchOptions; // Motor de Busqueda Cursos y Usuarios
    private LinkedList<Course> coursesList;
    private LinkedList<User> usersList;

    private User user; // EL Usuario
    private User otherUser; // Para ver perfiles, enviar mensajes, etc.
    private String filePath;
    private Image photo;

    private ReadJsonFromFile PTReadJsonFile; // Json: Provincias y Municipios <--- Registro y Update de Usuario
    private ReadJsonFromUrl PTReadJsonUrl; // Json: Provincias y Municipios <--- Registro y Update de Usuario
    private TreeMap<Province, TreeSet<Township>> provincesTownshipsMap;
    private TreeMap<String, Province> keys;

    private LinkedList<Course> coursesListStudent; // Mis Cursos (Recibiendo e Impartiendo)
    private LinkedList<Course> coursesListTeacher;

    private LinkedList<Message> inboxReceivedList; // Bandeja de Entrada
    private LinkedList<Message> inboxSentList;
    private boolean receivedInbox;
    private Message seeMessage;
    private int noReadMessages;

    // Aula Virtual
    //
    private Course selectedCourse; // Aula Virtual (Curso) y Motor de Busqueda (Perfil)
    private LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap;
    private LinkedList<Resource> activeResources;
    private Unit selectedUnit;
    private Resource selectedResource;

    private LinkedList<ArchiveHomework> filesHomework;
    private LinkedHashMap<User, LinkedList<ArchiveHomework>> studentFilesMap;
    private HashMap<TestQuestion, HashSet<TestAnswer>> proposedTest;
    private HashMap<TestQuestion, TestAnswer> solvedTest;
    private int testsPercentage;
    private int exercisesPercentage;
    private int controlsPercentage;
    private int examsPercentage;

    // CONSTRUCTOR
    private SharedStore() {
        this.timers = new LinkedList<>();
        this.clientMessage = ""; // Comunicación [C <-> S]
        this.protocolMessages = new ProtocolMessages();
        this.answered = true;
        this.listening = true;
        this.responseResult = new ResponseResult();

        this.conversions = new Conversions();
        this.comeback = "";

        this.searchOptions = new String[]{"optionCourses", "", "", "", ""};
        this.coursesList = new LinkedList(); // Motor de Busquedas
        this.usersList = new LinkedList();

        this.user = new User(); // Login y Perfil
        this.otherUser = new User();
        this.filePath = "false";

        this.coursesListStudent = new LinkedList(); // Mis cursos
        this.coursesListTeacher = new LinkedList();

        this.inboxReceivedList = new LinkedList(); // Bandeja de Entrada
        this.inboxSentList = new LinkedList();
        this.receivedInbox = true;

        this.virtualClassMap = new LinkedHashMap();
        this.activeResources = new LinkedList();

        this.studentFilesMap = new LinkedHashMap<>();

        this.testsPercentage = 0;
        this.exercisesPercentage = 0;
        this.controlsPercentage = 0;
        this.examsPercentage = 0;
    }

    public static SharedStore getInstance() { // <--- Para acceder a la Instancia
        if (sharedStore == null) {
            sharedStore = new SharedStore();
        }
        return sharedStore;
    }

    // OPERACIONES HILOS
    //
    public LinkedList<TimerThread> getTimers() {
        return timers;
    }

    public void setTimers(LinkedList<TimerThread> timers) {
        this.timers = timers;
    }

    public synchronized String getClientMessage() throws InterruptedException { // Llamado por el hilo ConnectionToServer <- wait()
        wait();
        return clientMessage;
    }

    public synchronized void setClientMessage(String clientMessage) { // "Activa" a ConnectionToServer <- notify()
        this.clientMessage = clientMessage;
        notify();
    }

    public synchronized void waitUntilResponse(boolean AppThread) { // "Activa" al Hilo Gráfico para que muestre la información actualizada
        try {
            if (AppThread) {
                wait();
            } else {
                notify();
            }
        } catch (InterruptedException iex) {
        }
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public ProtocolMessages getProtocolMessages() { // Mensajes [C <-> S]: C1#C2#C3#C4#C5#... S1#S2#S3#S4#S5#...
        return protocolMessages;
    }

    public void setProtocolMessages(ProtocolMessages protocolMessages) {
        this.protocolMessages = protocolMessages;
    }

    public boolean isAnswered() { // Recibida respuesta del Servidor¿? <- TimerThread
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

    // NAVEGACIÓN (cuando sea necesario)
    //
    public Conversions getConversions() {
        return conversions;
    }

    public void setConversions(Conversions conversions) {
        this.conversions = conversions;
    }

    public String getComeback() {
        return comeback;
    }

    public void setComeback(String comeback) {
        this.comeback = comeback;
    }

    // MOTOR DE BUSQUEDAS
    //
    public String[] getSearchOptions() {
        return searchOptions;
    }

    public void setSearchOptions(String[] searchOptions) {
        this.searchOptions = searchOptions;
    }

    public LinkedList<Course> getCoursesList() {
        return coursesList;
    }

    public void setCoursesList(LinkedList<Course> coursesList) {
        this.coursesList = coursesList;
    }

    public LinkedList<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(LinkedList<User> usersList) {
        this.usersList = usersList;
    }

    public Course getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public Resource getSelectedResource() {
        return selectedResource;
    }

    public void setSelectedResource(Resource selectedResource) {
        this.selectedResource = selectedResource;
    }

    // LOGIN Y PERFIL
    //
    public User getUser() { // EL Usuario
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getOtherUser() { // Perfil
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public Image getPhoto() { // Foto del Perfil
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public String getFilePath() { // Para enviar o no la Fotografía
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    // MIS CURSOS
    //
    public LinkedList<Course> getCoursesListStudent() {
        return coursesListStudent;
    }

    public void setCoursesListStudent(LinkedList<Course> coursesListStudent) {
        this.coursesListStudent = coursesListStudent;
    }

    public LinkedList<Course> getCoursesListTeacher() {
        return coursesListTeacher;
    }

    public void setCoursesListTeacher(LinkedList<Course> coursesListTeacher) {
        this.coursesListTeacher = coursesListTeacher;
    }

    // BANDEJA DE ENTRADA
    //
    public LinkedList<Message> getInboxReceivedList() {
        return inboxReceivedList;
    }

    public void setInboxReceivedList(LinkedList<Message> inboxReceivedList) {
        this.inboxReceivedList = inboxReceivedList;
    }

    public LinkedList<Message> getInboxSentList() {
        return inboxSentList;
    }

    public void setInboxSentList(LinkedList<Message> inboxSentList) {
        this.inboxSentList = inboxSentList;
    }

    public Message getSeeMessage() {
        return seeMessage;
    }

    public void setSeeMessage(Message seeMessage) {
        this.seeMessage = seeMessage;
    }

    public boolean isReceivedInbox() {
        return receivedInbox;
    }

    public void setReceivedInbox(boolean receivedInbox) {
        this.receivedInbox = receivedInbox;
    }

    public int getNoReadMessages() {
        return noReadMessages;
    }

    public void setNoReadMessages(int noReadMessages) {
        this.noReadMessages = noReadMessages;
    }

    public void noReadMessagesPlusOne() {
        this.noReadMessages = noReadMessages + 1;
    }

    public void noReadMessagesMinusOne() {
        this.noReadMessages = noReadMessages - 1;
    }

    // LECTURA JSON <- REGISTRO Y UPDATE DEL USUARIO
    //
    public void readJsonFile() {
        this.PTReadJsonFile = new ReadJsonFromFile();
        this.PTReadJsonFile.read();
        setKeys(PTReadJsonFile.getKeys());
        setProvincesTownships(PTReadJsonFile.getProvincesTownships());
    }

    public void readJsonUrl() {
        this.PTReadJsonUrl = new ReadJsonFromUrl();
        this.PTReadJsonUrl.read();
        setKeys(PTReadJsonUrl.getKeys());
        setProvincesTownships(PTReadJsonUrl.getProvincesTownships());
    }

    public TreeMap<Province, TreeSet<Township>> getProvincesTownships() {
        return provincesTownshipsMap;
    }

    public void setProvincesTownships(TreeMap<Province, TreeSet<Township>> provincesTownshipsMap) {
        this.provincesTownshipsMap = provincesTownshipsMap;
    }

    public TreeMap<String, Province> getKeys() {
        return keys;
    }

    public void setKeys(TreeMap<String, Province> keys) {
        this.keys = keys;
    }

    public TreeSet<Township> getTownships(Province province) {
        return provincesTownshipsMap.get(province);
    }

    // ----- AULA VIRTUAL -----

    public LinkedHashMap<Unit, LinkedList<Resource>> getVirtualClassMap() {
        return virtualClassMap;
    }

    public void setVirtualClassMap(LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap) {
        this.virtualClassMap = virtualClassMap;
    }

    public LinkedList<Resource> getActiveResources() {
        return activeResources;
    }

    public void setActiveResources(LinkedList<Resource> activeResources) {
        this.activeResources = activeResources;
    }

    public LinkedList<ArchiveHomework> getFilesHomework() {
        return filesHomework;
    }

    public void setFilesHomework(LinkedList<ArchiveHomework> filesHomework) {
        this.filesHomework = filesHomework;
    }

    public LinkedHashMap<User, LinkedList<ArchiveHomework>> getStudentFilesMap() {
        return studentFilesMap;
    }

    public void setStudentFilesMap(LinkedHashMap<User, LinkedList<ArchiveHomework>> studentFilesMap) {
        this.studentFilesMap = studentFilesMap;
    }

    public int getTestsPercentage() {
        return testsPercentage;
    }

    public void setTestsPercentage(int testsPercentage) {
        this.testsPercentage = testsPercentage;
    }

    public int getExercisesPercentage() {
        return exercisesPercentage;
    }

    public void setExercisesPercentage(int exercisesPercentage) {
        this.exercisesPercentage = exercisesPercentage;
    }

    public int getControlsPercentage() {
        return controlsPercentage;
    }

    public void setControlsPercentage(int controlsPercentage) {
        this.controlsPercentage = controlsPercentage;
    }

    public int getExamsPercentage() {
        return examsPercentage;
    }

    public void setExamsPercentage(int examsPercentage) {
        this.examsPercentage = examsPercentage;
    }


}
