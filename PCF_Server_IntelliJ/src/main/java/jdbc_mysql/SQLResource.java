/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class SQLResource {
    /*
    CREATE TABLE pcf_resource(
        id_resource INT AUTO_INCREMENT PRIMARY KEY,
        title_resource VARCHAR(50) NOT NULL,
        presentation_resource VARCHAR(30), -- Un Test por ejemplo no tiene porque tener enunciado
        type_resource VARCHAR(50) NOT NULL, -- Archivo / Tarea / Enlace / Test
        order_resource INT NOT NULL, -- Dentro del Tema
        hidden_resource BOOLEAN DEFAULT false,
        id_unit INT NOT NULL
    );

    CREATE TABLE pcf_archive(
        id_archive INT PRIMARY KEY, -- FOREIGN KEY id_resource
        path_archive VARCHAR(500) NOT NULL
    );

    CREATE TABLE pcf_link(
        id_link INT PRIMARY KEY, -- FOREIGN KEY id_resource
        url_link VARCHAR(500) NOT NULL
    );

    CREATE TABLE pcf_homework(
        id_homework INT PRIMARY KEY, -- FOREIGN KEY id_resource
        init_date_homework DATE NOT NULL,
        init_time_homework TIME NOT NULL,
        end_date_homework DATE NOT NULL,
        end_time_homewok TIME NOT NULL,
        percentage_homework INT NOT NULL
    );
    */

    private Connection connection;
    private PreparedStatement pstm;

    private String resourceTable;
    private String[] resourceColumns;
    private String primaryKey;
    private String unitKey;

    private String archiveTable;
    private String[] archiveColumns;

    private String linkTable;
    private String[] linkColumns;

    private String homeworkTable;
    private String[] homeworkColumns;

    public SQLResource(Connection connection) {
        this.connection = connection;
        this.resourceTable = "pcf_resource";
        this.resourceColumns = new String[]{"id_resource", // 0
                "title_resource", // 1
                "presentation_resource", // 2
                "type_resource", // 3
                "order_resource", // 4
                "hidden_resource", // 5
                "id_unit"}; // 6
        this.primaryKey = resourceColumns[0];
        this.unitKey = resourceColumns[6];

        this.linkTable = "pcf_link";
        this.linkColumns = new String[]{"id_link", // 0
                "url_link"}; // 1

        this.archiveTable = "pcf_archive";
        this.archiveColumns = new String[]{"id_archive", // 0
                "path_archive"}; // 1

        this.homeworkTable = "pcf_homework";
        this.homeworkColumns = new String[]{"id_homework", // 0
                "init_date_homework", // 1
                "init_time_homework", // 2
                "end_date_homework", // 3
                "end_time_homewok", // 4
                "percentage_homework"}; // 6
    }

    public int getNewPositionResource(int idUnit) {
        int position = 1;

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + resourceTable + " WHERE " + resourceColumns[6] + " = ?;");
            pstm.setInt(1, idUnit);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    position++;
                }
                return position;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return position;
    }

    // CONSULTAS
    //
    public ResourceLink selectLink(int idLink) {
        try {
            String selection = "SELECT * FROM " + resourceTable + ", " + linkTable
                    + " WHERE pcf_resource.id_resource = pcf_link.id_link"
                    + " AND " + linkColumns[0] + " = ?";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idLink);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idResource = rst.getInt(resourceColumns[0]);
                    String titleResource = rst.getString(resourceColumns[1]);
                    String presentationResource = rst.getString(resourceColumns[2]);
                    String typeResource = rst.getString(resourceColumns[3]);
                    int orderResource = rst.getInt(resourceColumns[4]);
                    boolean hiddenResource = rst.getBoolean(resourceColumns[5]);
                    int idUnit = rst.getInt(resourceColumns[6]);
                    String urlLink = rst.getString(linkColumns[1]);

                    return new ResourceLink(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnit, urlLink);
                }
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResourceArchive selectArchive(int idArchive) {
        try {
            String selection = "SELECT * FROM " + resourceTable + ", " + archiveTable
                    + " WHERE pcf_resource.id_resource = pcf_archive.id_archive"
                    + " AND " + archiveColumns[0] + " = ?";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idArchive);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idResource = rst.getInt(resourceColumns[0]);
                    String titleResource = rst.getString(resourceColumns[1]);
                    String presentationResource = rst.getString(resourceColumns[2]);
                    String typeResource = rst.getString(resourceColumns[3]);
                    int orderResource = rst.getInt(resourceColumns[4]);
                    boolean hiddenResource = rst.getBoolean(resourceColumns[5]);
                    int idUnit = rst.getInt(resourceColumns[6]);
                    String pathsArchive = rst.getString(archiveColumns[1]);

                    return new ResourceArchive(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnit, pathsArchive);
                }
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // LISTADOS
    //
    public LinkedList<Resource> listResourcesTeacher(int idUnit) { // // Listar Recursos en Aula Virtual: Rol Profesor
        LinkedList<Resource> resources = new LinkedList();
        try {
            String selection = "SELECT *"
                    + " FROM pcf_resource"
                    + " WHERE pcf_resource.id_unit = ?"
                    + " ORDER BY pcf_resource.order_resource;"; // Respetar el Orden

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idUnit);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int id_resource = rst.getInt(resourceColumns[0]);
                    String title_resource = rst.getString(resourceColumns[1]);
                    String presentation_resource = rst.getString(resourceColumns[2]);
                    String type_resource = rst.getString(resourceColumns[3]);
                    int order_resource = rst.getInt(resourceColumns[4]);
                    boolean hidden_resource = rst.getBoolean(resourceColumns[5]);
                    int id_unit = rst.getInt(resourceColumns[6]);

                    resources.add(new Resource(id_resource, title_resource, presentation_resource, type_resource, order_resource, hidden_resource, id_unit));
                }
                return resources;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resources;
    }

    public LinkedList<Resource> listResourcesStudent(int idUnit) { // Listar Recursos en Aula Virtual: Rol Alumno
        LinkedList<Resource> resources = new LinkedList();
        try {
            String selection = "SELECT *"
                    + " FROM pcf_resource"
                    + " WHERE pcf_resource.id_unit = ?"
                    + " AND pcf_resource.hidden_resource = false" // Si están Ocultos no los devuelve
                    + " ORDER BY pcf_resource.order_resource;"; // Respetar el Orden

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idUnit);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int id_resource = rst.getInt(resourceColumns[0]);
                    String title_resource = rst.getString(resourceColumns[1]);
                    String presentation_resource = rst.getString(resourceColumns[2]);
                    String type_resource = rst.getString(resourceColumns[3]);
                    int order_resource = rst.getInt(resourceColumns[4]);
                    boolean hidden_resource = rst.getBoolean(resourceColumns[5]);
                    int id_unit = rst.getInt(resourceColumns[6]);

                    resources.add(new Resource(id_resource, title_resource, presentation_resource, type_resource, order_resource, hidden_resource, id_unit));
                }
                return resources;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resources;
    }

    // ALTAS RECURSOS
    //
    public String registerResource(Resource resource) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + resourceTable + " ("
                    + resourceColumns[1] + ", "
                    + resourceColumns[2] + ", "
                    + resourceColumns[3] + ", "
                    + resourceColumns[4] + ", "
                    + resourceColumns[5] + ", "
                    + resourceColumns[6] + ") VALUES (?, ?, ?, ?, ?, ?);");

            pstm.setString(1, resource.getTitleResource());
            pstm.setString(2, resource.getPresentation());
            pstm.setString(3, resource.getType());
            pstm.setInt(4, resource.getOrder());
            pstm.setBoolean(5, resource.isHidden());
            pstm.setInt(6, resource.getIdUnit());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public int getIdNewResource(Resource resource) { // Clave única: (Titulo, Tema)
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + resourceTable + " WHERE " + resourceColumns[1] + " = ? AND " + unitKey + " = ?;");

            pstm.setString(1, resource.getTitleResource());
            pstm.setInt(2, resource.getIdUnit());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getInt(primaryKey);
                }
            }
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    public String registerLink(ResourceLink link) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + linkTable + " ("
                    + linkColumns[0] + ", "
                    + linkColumns[1] + ") VALUES (?, ?);");

            pstm.setInt(1, link.getIdResource());
            pstm.setString(2, link.getUrlLink());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public String registerArchive(ResourceArchive archive) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + archiveTable + " ("
                    + archiveColumns[0] + ", "
                    + archiveColumns[1] + ") VALUES (?, ?);");

            pstm.setInt(1, archive.getIdResource());
            pstm.setString(2, archive.getPathsArchives());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // UPDATES
    //
    public boolean updateResource(Resource resource) { // 0.C31, 1.idRecurso, 2.tituloRecurso, 3.presentación, 4.tipo, 5.oculto/no
        try {
            pstm = connection.prepareStatement("UPDATE " + resourceTable + " SET "
                    + resourceColumns[1] + " = ?, "
                    + resourceColumns[2] + " = ?, "
                    + resourceColumns[3] + " = ?, "
                    + resourceColumns[5] + " = ? WHERE " + primaryKey + " = ?;");

            pstm.setString(1, resource.getTitleResource());
            pstm.setString(2, resource.getPresentation());
            pstm.setString(3, resource.getType());
            pstm.setBoolean(4, resource.isHidden());
            pstm.setInt(5, resource.getIdResource());

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean updateLink(ResourceLink link) {
        try {
            pstm = connection.prepareStatement("UPDATE " + linkTable + " SET "
                    + linkColumns[1] + " = ? WHERE " + linkColumns[0] + " = ?;");

            pstm.setString(1, link.getUrlLink());
            pstm.setInt(2, link.getIdResource());

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean updateArchive(ResourceArchive archive) {
        try {
            pstm = connection.prepareStatement("UPDATE " + archiveTable + " SET "
                    + archiveColumns[1] + " = ? WHERE " + archiveColumns[0] + " = ?;");

            pstm.setString(1, archive.getPathsArchives());
            pstm.setInt(2, archive.getIdResource());

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // UPDATE (TEMA - TAREAS) (Parte 2: Actualizar Porcentaje)
    //
    public boolean updateHomeworkPercentage(int idHomework, int percentage) {
        try {
            pstm = connection.prepareStatement("UPDATE " + resourceTable + " SET "
                    + resourceColumns[5] + " = ? WHERE " + resourceColumns[0] + " = ?;");
            pstm.setInt(1, percentage);
            pstm.setInt(2, idHomework);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // REORDENACIÓN
    public boolean reOrderResources(int idUnit, int position) {
        try {
            pstm = connection.prepareStatement("UPDATE " + resourceTable + " SET "
                    + resourceColumns[4] + " = ? WHERE " + resourceColumns[0] + " = ?;");

            pstm.setInt(1, position);
            pstm.setInt(2, idUnit);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // DELETE
    //
    public boolean deleteResource(int idResource) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM " + resourceTable + " WHERE " + primaryKey + " = ?;");

            pstm.setInt(1, idResource);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
    public int listHomeworkArchives (int idHomework) { // Clave única: (Titulo, Tema)
        try {
            pstm = connection.prepareStatement("SELECT pcf_homework_archive.id_homework_archive, pcf_homework_archive.path_homework_archive" + //idArchivoSubido, RutaArchivoSubido
                    " FROM pcf_user, pcf_homework_archive, pcf_homework, pcf_user_upload_homework_archive" +
                    " WHERE pcf_user_upload_homework_archive.id_user = pcf_user.id_user" + //-- Subido por un alumno
                    " AND pcf_user_upload_homework_archive.id_homework_archive = pcf_homework_archive.id_homework_archive" + //-- Subido archivo
                    " AND pcf_user_upload_homework_archive.id_homework = pcf_homework.id_homework" +
                    " AND pcf_homework.id_homework = ?;"); //-- A una tarea

            pstm.setInt(1, idHomework);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getInt("id_homework_archive");
                }
            }
            return 0;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    */
    public LinkedList<User> listHomeworkArchivesStudentsForTeacher1(int idHomeworkResource) { // <- CORREGIR
        try {
            LinkedList<User> users = new LinkedList<>();

            pstm = connection.prepareStatement("SELECT" + // Listado Alumnos
                    " pcf_user.id_user, pcf_user.photo_user, pcf_user.name_user," +
                    " pcf_user.email_user, pcf_user.province_user, pcf_user.township_user," +
                    " pcf_user.short_presentation_user, pcf_user.long_presentation_user" +

                    " FROM pcf_user, pcf_homework_archive, pcf_homework, pcf_user_upload_homework_archive" +
                    " WHERE pcf_user_upload_homework_archive.id_user = pcf_user.id_user" + // Que han subido archivos
                    " AND pcf_user_upload_homework_archive.id_homework_archive = pcf_homework_archive.id_homework_archive" +
                    " AND pcf_user_upload_homework_archive.id_homework = pcf_homework.id_homework" +
                    " AND pcf_homework.id_homework = ?" + // <- A una tarea
                    " GROUP BY pcf_user.id_user;");

            pstm.setInt(1, idHomeworkResource);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idUser = rst.getInt("id_user");
                    boolean photo = rst.getBoolean("photo_user");
                    String name = rst.getString("name_user");
                    String emailUser = rst.getString("email_user");
                    String province = rst.getString("province_user");
                    String township = rst.getString("township_user");
                    String shortPresentation = rst.getString("short_presentation_user");
                    String longPresentation = rst.getString("long_presentation_user");

                    users.add(new User(idUser, photo, name, emailUser, province, township, shortPresentation, longPresentation));
                }
            }
            return users;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedList<HomeworkArchive> listHomeworkArchivesStudentsForTeacher2(int idStudent) { // <- CORREGIR AL ALUMNO...
        try {
            LinkedList<HomeworkArchive> homeworkArchives = new LinkedList<>();

            pstm = connection.prepareStatement("SELECT" + // Listado Alumnos
                    " pcf_homework_archive.id_homework_archive, pcf_homework_archive.path_homework_archive" +
                    " FROM pcf_user, pcf_homework_archive, pcf_homework, pcf_user_upload_homework_archive" +
                    " WHERE pcf_user_upload_homework_archive.id_user = pcf_user.id_user" + // Que han subido archivo
                    " AND pcf_user_upload_homework_archive.id_homework_archive = pcf_homework_archive.id_homework_archive" + //
                    " AND pcf_user_upload_homework_archive.id_homework = pcf_homework.id_homework" +
                    " AND pcf_user.id_user = ?;"); // subidas por un alumno

            pstm.setInt(1, idStudent);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idHomeworkArchive = rst.getInt("id_homework_archive");
                    String pathHomeworkArchive = rst.getString("path_homework_archive");

                    homeworkArchives.add(new HomeworkArchive(idHomeworkArchive, pathHomeworkArchive));
                }
                return homeworkArchives;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void registerRecord(Record record) { // (void: No hay ningún tipo de notificaciones para nadie).
        try {
            pstm = connection.prepareStatement("INSERT INTO pcf_record" +
                    " (date_record, time_record, event_record, id_student, id_resource)" + // [3] <- score DEFAULT 0
                    " VALUES (?,?,?,?,?);");

            pstm.setDate(1, record.getDateRecord());
            pstm.setTime(2, record.getTimeRecord());
            pstm.setString(3, record.getEvent());
            pstm.setInt(4, record.getIdStudent());
            pstm.setInt(5, record.getIdResource());

            pstm.executeUpdate();
            pstm.close();

        } catch (SQLException ex) {
            System.out.println("Error en la inserción del Registro.");
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public LinkedList<Record> listStudentRecord(int idStudent) {

        try {
            /*
            CREATE TABLE pcf_record(
                id_record INT AUTO_INCREMENT PRIMARY KEY,
                date_record DATE NOT NULL,
                time_record TIME NOT NULL,

                event_record VARCHAR(500) NOT NULL,
                id_student INT NOT NULL,
                id_resource INT NOT NULL
            );
            */
            LinkedList<Record> records = new LinkedList<>();

            pstm = connection.prepareStatement("SELECT  pcf_record.id_record, pcf_record.date_record," +
                    " pcf_record.time_record, pcf_record.event_record, pcf_record.id_student, pcf_record.id_resource" +
                    " FROM pcf_record, pcf_user" +
                    " WHERE pcf_record.id_student = pcf_user.id_user" +
                    " AND pcf_user.id_user = ?;");

            pstm.setInt(1, idStudent);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idRecord = rst.getInt("id_record");
                    Date dateRecord = rst.getDate("date_record");
                    Time timeRecord = rst.getTime("time_record");
                    String event = rst.getString("event_record");
                    // idStudent;
                    int idResource = rst.getInt("id_resource");

                    records.add(new Record(idRecord, dateRecord, timeRecord, event, idStudent, idResource));
                }
                return records;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
