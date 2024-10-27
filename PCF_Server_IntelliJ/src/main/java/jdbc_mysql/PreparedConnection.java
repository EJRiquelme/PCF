/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class PreparedConnection {

    private final String BBDD = "PCF";
    private final String USER = "william";
    private final String PASS = "y0-50y-54r3k";

    private Connection connection;
    private PreparedStatement pstm;

    private SQLUser sqlUser;
    private SQLMessage sqlMessage;
    private SQLCourse sqlCourse;
    private SQLUnit sqlUnit;

    private SQLResource sqlResource;
    private SQLResourceFile sqlResourceFile;
    private SQLResourceTest sqlResourceTest;
    private SQLResourceHomework sqlResourceHomework;

    public void operators() {
        this.sqlUser = new SQLUser(connection);
        this.sqlMessage = new SQLMessage(connection);
        this.sqlCourse = new SQLCourse(connection);
        this.sqlUnit = new SQLUnit(connection);
        this.sqlResource = new SQLResource(connection);
        this.sqlResourceFile = new SQLResourceFile(connection);
        this.sqlResourceTest = new SQLResourceTest(connection);
        this.sqlResourceHomework = new SQLResourceHomework(connection);
    }

    public Connection getConnection() {
        return connection;
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + BBDD, USER, PASS);

            operators(); // Inicializo las "ramas" de consultas
            System.out.println("Conexión a la BBDD establecida");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Driver no encontrado.");
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de conexión.");
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de desconexión.");
        }
    }

    // OPERACIONES BBDD -> USUARIOS
    public int getUserId(User user) { // Para operaciones posteriores al Login
        return sqlUser.getUserId(user);
    }

    public String getUserName(int userId) {
        return sqlUser.getUserName(userId);
    }

    public String getUserEmail(int userId) { // Para operaciones posteriores al Login
        return sqlUser.getUserEmail(userId);
    }

    public String registerUser(User user) { // Registro de Usuario
        return sqlUser.registerUser(user);
    }

    public boolean deleteUser(User user) { // Baja de Usuario
        return sqlUser.deleteUser(user);
    }

    public String updateUser(User user) { // Modificación de Usuario
        return sqlUser.updateUser(user);
    }

    public User confirmUser(User user) { // Login part 1
        return sqlUser.confirmUser(user);
    }

    public User takeUserPerfil(int userId) { // Login part 2
        return sqlUser.takeUserPerfil(userId);
    }

    public LinkedList<User> searchMultipleUsers(LinkedList<String> searchParameters, int idUser) { // Motor de Busqueda -> Usuarios
        return sqlUser.searchMultipleUsers(searchParameters, idUser);
    }

    public LinkedList<Integer> getCourseStudents(int courseId) {
        return sqlUser.getCourseStudents(courseId);
    }

    public LinkedList<User> listStudents(int idCourse) {
        return sqlUser.listStudents(idCourse);
    }

    // OPERACIONES BBDD -> MENSAJES (Bandeja de Entrada)
    //
    public boolean registerMessage(Message message) { // Enviar Mensaje
        return sqlMessage.registerMessage(message);
    }

    public boolean registerMassMessage(Message message) { // Enviar Mensaje
        return sqlMessage.registerMassMessage(message);
    }

    public boolean isSolicitudeAlreadyLaunched(Message message) {
        return sqlMessage.isSolicitudeAlreadyLaunched(message);
    }

    public boolean isInvitationAlreadyLaunched(int idCourse, int idSender, int idReceiver) {
        return sqlMessage.isInvitationAlreadyLaunched(idCourse, idSender, idReceiver);
    }

    public boolean registerSolicitude(Message message) { // Enviar Solicitud
        return sqlMessage.registerSolicitude(message);
    }

    public boolean registerInvitation(Message message) { // Enviar Invitación
        return sqlMessage.registerInvitation(message);
    }

    public boolean registerAcceptedSolicitude(Message message) {
        return sqlMessage.registerAcceptedSolicitude(message);
    }

    public boolean deleteMessage(int idMessage, boolean isSender) { // "Borrar" Mensaje recibido (eliminar Remitente/Destinatario [Si ambos son eliminados se eliminaría el mensaje por completo])
        return sqlMessage.deleteMessage(idMessage, isSender);
    }

    public int messagesToBeReaden(int idUser) {
        return sqlMessage.messagesToBeReaden(idUser); // Cantidad de Mensajes no leidos
    }

    public boolean readMessage(int idMessage) {
        return sqlMessage.readMessage(idMessage);
    }

    public LinkedList<Message> searchMultipleMessagesAsReceiver(int idUser) { // Bandeja de Entrada -> Mensajes recibidos
        return sqlMessage.searchMultipleMessagesAsReceiver(idUser);
    }

    public LinkedList<Message> searchMultipleMessagesAsSender(int idUser) { // Bandeja de Entrada -> Mensajes enviados
        return sqlMessage.searchMultipleMessagesAsSender(idUser);
    }

    // OPERACIONES BBDD -> CURSOS
    public int getIdCourse(Course course) {
        return sqlCourse.getIdCourse(course);
    }

    public String registerCourse(Course course, LinkedList<String> tags) { // Alta Curso, Alta Tags y Alta relación Curso/Tags
        return sqlCourse.registerCourse(course, tags);
    }

    public String registerCourseNewTags(LinkedList<String> tags) {
        return sqlCourse.registerCourseNewTags(tags);
    }

    public ArrayList<Integer>  getTagsIds(LinkedList<String> courseTagsUpdate) {
        return sqlCourse.getTagsIds(courseTagsUpdate);
    }

    public void registerCourseTagsRelation(int idCourse, ArrayList<Integer> idsTags) {
        sqlCourse.registerCourseTagsRelation(idCourse, idsTags);
    }

    public boolean isStudentRegistered(int idUser, int idCourse) {
        return sqlCourse.isStudentRegistered(idUser, idCourse);
    }

    public String registerStudent(int idUser, int idCourse) {
        return sqlCourse.registerStudent(idUser, idCourse);
    }

    public boolean deleteCourse(int courseId) { // Baja Curso
        return sqlCourse.deleteCourse(courseId);
    }

    public String updateCourse(Course course, LinkedList<String> tags) { // Modificación Curso
        return sqlCourse.updateCourse(course, tags);
    }

    public boolean openCourse(int idCourse) { // Abrir y Cerrar Curso
        return sqlCourse.openCourse(idCourse);
    }

    public boolean closeCourse(int idCourse) { //<====================================================================== TODO
        return sqlCourse.closeCourse(idCourse);
    }

    public boolean deleteStudents(int idCourse) {
        return sqlCourse.deleteStudents(idCourse);
    }

    public Course searchCourse(int idCourse) { // Busqueda Curso
        return sqlCourse.searchCourse(idCourse);
    }

    public LinkedList<Course> searchCoursesITeach(int idTeacher) {
        return sqlCourse.searchCoursesITeach(idTeacher);
    }

    public LinkedList<Course> searchCoursesIReceive(int studentId) {
        return sqlCourse.searchCoursesIReceive(studentId);
    }

    public LinkedList<Course> searchMultipleCourses(LinkedList<String> parameters) { // Motor de Busqueda -> Cursos
        return sqlCourse.searchMultipleCourses(parameters);
    }

    // OPERACIONES BBDD -> UNIDADES / TEMAS
    public int getIdUnit(Unit unit) {
        return sqlUnit.getIdUnit(unit);
    }

    public int getNewPositionUnit(int courseId) { // 1, 2, 3...
        return sqlUnit.getNewPositionUnit(courseId);
    }

    public String registerUnit(Unit unit) { // Crear Unidad
        return sqlUnit.registerUnit(unit);
    }

    public boolean reorderUnit(int id, int pos) {
        return sqlUnit.reOrderUnit(id, pos);
    }

    public boolean deleteUnit(int idUnit) { // Eliminar Unidad
        return sqlUnit.deleteUnit(idUnit);
    }

    public String updateUnit(Unit unit) { // Aula Virtual -> Consultar Curso
        return sqlUnit.updateUnit(unit);
    }


    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassTeacher(int idCourseVirtualClass) {
        return sqlCourse.consultVirtualClassTeacher(idCourseVirtualClass);
    }

    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassStudent(int idCourseVirtualClass) {
        return sqlCourse.consultVirtualClassStudent(idCourseVirtualClass);
    }


    public LinkedList<Unit> listUnitsTeacher(int idCourse) {
        return sqlUnit.listUnitsTeacher(idCourse);
    }

    public LinkedList<Unit> listUnitsStudent(int idCourse) {
        return sqlUnit.listUnitsStudent(idCourse);
    }

    // OPERACIONES BBDD -> RECURSOS
    public int getNewPositionResource(int idUnit) {
        return this.sqlResource.getNewPositionResource(idUnit);
    }

    public ResourceLink selectLink(int idLink) {
        return this.sqlResource.selectLink(idLink);
    }

    public ResourceArchive selectArchive(int idArchive) {
        return this.sqlResource.selectArchive(idArchive);
    }

    public ResourceHomework selectHomework(int idHomework) {
        return this.sqlResourceHomework.selectHomework(idHomework);
    }

    public LinkedList<Resource> listResourcesTeacher(int idUnit) { // Subir Archivo
        return sqlResource.listResourcesTeacher(idUnit);
    }

    public LinkedList<Resource> listResourcesStudent(int idUnit) { // Subir Archivo
        return sqlResource.listResourcesStudent(idUnit);
    }

    public String registerResource(Resource resource) {
        return sqlResource.registerResource(resource);
    }

    public int getIdNewResource(Resource resource) {
        return sqlResource.getIdNewResource(resource);
    }

    public String registerLink(ResourceLink link) {
        return sqlResource.registerLink(link);
    }

    public String registerArchive(ResourceArchive archive) {
        return sqlResource.registerArchive(archive);
    }

    public String registerHomework(ResourceHomework homework) {
        return sqlResourceHomework.registerHomework(homework);
    }

    public boolean updateResource(Resource resource) {
        return sqlResource.updateResource(resource);
    }

    public boolean updateLink(ResourceLink link) {
        return sqlResource.updateLink(link);
    }

    public boolean updateArchive(ResourceArchive archive) {
        return sqlResource.updateArchive(archive);
    }

    public boolean updateHomework(ResourceHomework homework) {
        return sqlResourceHomework.updateHomework(homework);
    }

    public boolean updateHomeworkPercentage(int idHomework, int percentage) {
        return sqlResource.updateHomeworkPercentage(idHomework, percentage);
    }

    public boolean reOrderResources(int idUnit, int position) {
        return sqlResource.reOrderResources(idUnit, position);
    }

    public boolean deleteResource(int idResource) {
        return sqlResource.deleteResource(idResource);
    }

    public char registerHomeworkArchivePart1(String path) {
        return sqlResourceHomework.registerHomeworkArchivePart1(path);
    }

    public char registerHomeworkArchivePart2(int idUser, String archivePath, int idHomework) {
        return sqlResourceHomework.registerHomeworkArchivePart2(idUser, archivePath, idHomework);
    }

    public boolean deleteFileHomework(int idFile) {
        return sqlResourceHomework.deleteFileHomework(idFile);
    }

    public String getPathArchiveHomework(int idArchive) {
        return sqlResourceHomework.getPathArchiveHomework(idArchive);
    }

    public LinkedList<HomeworkArchive> selectHomeworkFiles(int idUser, int idHomework) {
        return sqlResourceHomework.selectHomeworkFiles(idUser, idHomework);
    }

    public LinkedHashMap<User, LinkedList<HomeworkArchive>> selectHomeworkStudentsFiles(int idHomework) {
        return sqlResourceHomework.selectHomeworkStudentsFiles(idHomework);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public LinkedList<User> listHomeworkArchivesStudentsForTeacher1(int idHomeworkResource) {
        return sqlResource.listHomeworkArchivesStudentsForTeacher1(idHomeworkResource);
    }

    public LinkedList<HomeworkArchive> listHomeworkArchivesStudentsForTeacher2(int idStudent) {
        return sqlResource.listHomeworkArchivesStudentsForTeacher2(idStudent);
    }

    public boolean updateScore(double score, int idStudent, int idHomework) {
        return sqlResourceHomework.updateScore(score, idStudent, idHomework);
    }

    public void registerRecord(Record record) {
        sqlResource.registerRecord(record);
    }

    public LinkedList<Record> listStudentRecord(int idStudent) {
        return sqlResource.listStudentRecord(idStudent);
    }

    public String registerTest(ResourceTest resourceTest) {
        return sqlResourceTest.registerTest(resourceTest);
    }

    public String updateTest(ResourceTest resourceTest) {
        return sqlResourceTest.updateTest(resourceTest);
    }

    public ResourceTest selectTest(int idTest) {
        return sqlResourceTest.selectTest(idTest);
    }

    public String registerTestQuestion(TestQuestion testQuestion) {
        return sqlResourceTest.registerTestQuestion(testQuestion);
    }

    public String updateTestQuestion(TestQuestion testQuestion) {
        return sqlResourceTest.updateTestQuestion(testQuestion);
    }

    public String deleteQuestion(int idQuestion) {
        return sqlResourceTest.deleteQuestion(idQuestion);
    }

    public String registerTestAnswers(LinkedHashSet<TestAnswer> testAnswers) {
        return sqlResourceTest.registerTestAnswers(testAnswers);
    }

    public String updateTestAnswers(LinkedHashSet<TestAnswer> testAnswers) {
        return sqlResourceTest.updateTestAnswers(testAnswers);
    }

    public String deleteTestAnswers(LinkedHashSet<Integer> testAnswers) {
        return sqlResourceTest.deleteTestAnswers(testAnswers);
    }

    public int getIdTestQuestion(TestQuestion testQuestion) {
        return sqlResourceTest.getIdTestQuestion(testQuestion);
    }

    public LinkedHashSet<Integer> getIdTestAnswers(int idQuestion) {
        return sqlResourceTest.getIdTestAnswers(idQuestion);
    }

    public LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> selectCompleteTest(int idTest) {
        return sqlResourceTest.selectCompleteTest(idTest);
    }

    public boolean existsSolvedTest(int idTest, int idUser) {
        return sqlResourceTest.existsSolvedTest(idTest, idUser);
    }

    public int getIdSolvedTest(int idOriginalTest, int idStudent) {
        return sqlResourceTest.getIdSolvedTest(idOriginalTest, idStudent);
    }

    public String registerSolvedTest(int idOriginalTest, int idStudent) {
        return sqlResourceTest.registerSolvedTest(idOriginalTest, idStudent);
    }

    /*
        public String registerSolvedTestRelation(int idOrignialTest, int idUser) {
            return sqlResourceTest.registerSolvedTestRelation(idOrignialTest, idUser);
        }

        public int getIdSolvedTestQuestion(TestQuestion testQuestion, int idTest, int idUser) {
            return sqlResourceTest.getIdSolvedTestQuestion(testQuestion, idTest, idUser);
        }

        public String registerSolvedTestQuestions(LinkedHashSet<TestQuestion> solvedTestQuestions, int idTest, int idUser) {
            return sqlResourceTest.registerSolvedTestQuestions(solvedTestQuestions, idTest, idUser);
        }
    */
    public String registerSolvedTestAnswer(int idSolvedTest, LinkedHashSet<TestAnswer> solvedTestAnswers) {
        return sqlResourceTest.registerSolvedTestAnswer(idSolvedTest, solvedTestAnswers);
    }

    public String setScore(int idTest, int idUser, double score) {
        return sqlResourceTest.setScore(idTest, idUser, score);
    }

    public String deleteSolvedTestByOriginalTest(int idOriginalTest) { // Para el borrado de Test
        return sqlResourceTest.deleteSolvedTestByOriginalTest(idOriginalTest);
    }

    public String deleteSolvedTestByStudentId(int idStudent) { // Para el cierre del curso
        return sqlResourceTest.deleteSolvedTestByOriginalTest(idStudent);
    }

    public String getFilePath(int idFile) {
        return sqlResourceFile.getFilePath(idFile);
    }
}
