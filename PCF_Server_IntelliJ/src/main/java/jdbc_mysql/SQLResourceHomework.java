package jdbc_mysql;

import objects.*;

import java.io.File;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLResourceHomework {
    /*
    CREATE TABLE pcf_homework(
            id_homework INT PRIMARY KEY, -- FOREIGN KEY id_resource
            init_date_homework DATE NOT NULL,
            init_time_homework TIME NOT NULL,
            end_date_homework DATE NOT NULL,
            end_time_homework TIME NOT NULL,
            percentage_homework INT NOT NULL
    );
    */

    private Connection connection;
    private PreparedStatement pstm;

    private String resourceTable;
    private String[] resourceColumns;

    private String homeworkTable;
    private String[] homeworkColumns;

    public SQLResourceHomework(Connection connection) {
        this.connection = connection;

        this.resourceTable = "pcf_resource";
        this.resourceColumns = new String[]{"id_resource", // 0
                "title_resource", // 1
                "presentation_resource", // 2
                "type_resource", // 3
                "order_resource", // 4
                "hidden_resource", // 5
                "id_unit"}; // 6

        this.homeworkTable = "pcf_homework";
        this.homeworkColumns = new String[]{"id_homework", // 0
                "init_date_homework", // 1
                "init_time_homework", // 2
                "end_date_homework", // 3
                "end_time_homework", // 4
                "percentage_homework"}; // 6
    }

    public String registerHomework(ResourceHomework homework) {
        try {
            pstm = connection.prepareStatement("INSERT INTO " + homeworkTable + " ("
                    + homeworkColumns[0] + ", "
                    + homeworkColumns[1] + ", "
                    + homeworkColumns[2] + ", "
                    + homeworkColumns[3] + ", "
                    + homeworkColumns[4] + ", "
                    + homeworkColumns[5] + ")" +
                    " VALUES ((SELECT id_resource FROM pcf_resource WHERE order_resource = ? AND id_unit = ?), ?, ?, ?, ?, ?);");

            pstm.setInt(1, homework.getOrder());
            pstm.setInt(2, homework.getIdUnit());
            pstm.setDate(3, homework.getOpenDate());
            pstm.setTime(4, homework.getOpenTime());
            pstm.setDate(5, homework.getCloseDate());
            pstm.setTime(6, homework.getCloseTime());
            pstm.setInt(7, homework.getPercentage());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public boolean updateHomework(ResourceHomework homework) {
        try {
            pstm = connection.prepareStatement("UPDATE " + homeworkTable + " SET "
                    + homeworkColumns[1] + " = ?, "
                    + homeworkColumns[2] + " = ?, "
                    + homeworkColumns[3] + " = ?, "
                    + homeworkColumns[4] + " = ?, " // ![5] -> El Porcentaje no se actualiza aquí
                    + homeworkColumns[5] + " = ? WHERE " + homeworkColumns[0] + " = ?;");

            pstm.setDate(1, homework.getOpenDate());
            pstm.setTime(2, homework.getOpenTime());
            pstm.setDate(3, homework.getCloseDate());
            pstm.setTime(4, homework.getCloseTime());
            pstm.setInt(5, homework.getPercentage());
            pstm.setInt(6, homework.getIdResource());

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public ResourceHomework selectHomework(int idHomework) {
        try {
            String selection = "SELECT * FROM " + resourceTable + ", " + homeworkTable
                    + " WHERE pcf_resource.id_resource = pcf_homework.id_homework"
                    + " AND " + homeworkColumns[0] + " = ?";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idHomework);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idResource = rst.getInt(resourceColumns[0]);
                    String titleResource = rst.getString(resourceColumns[1]);
                    String presentationResource = rst.getString(resourceColumns[2]);
                    String typeResource = rst.getString(resourceColumns[3]);
                    int orderResource = rst.getInt(resourceColumns[4]);
                    boolean hiddenResource = rst.getBoolean(resourceColumns[5]);
                    int idUnit = rst.getInt(resourceColumns[6]);

                    Date initDate = rst.getDate(homeworkColumns[1]);
                    Time initTime = rst.getTime(homeworkColumns[2]);
                    Date endDate = rst.getDate(homeworkColumns[3]);
                    Time endtTime = rst.getTime(homeworkColumns[4]);
                    int percentage = rst.getInt(homeworkColumns[5]);

                    Resource resource = new Resource(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnit);
                    return new ResourceHomework(resource, initDate, initTime, endDate, endtTime, percentage);
                }
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public char registerHomeworkArchivePart1(String path) {
        try {
            pstm = connection.prepareStatement("INSERT INTO pcf_homework_archive (path_homework_archive) VALUES (?);");
            pstm.setString(1, path);

            pstm.executeUpdate();
            pstm.close();

            return 'A';
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 'B';
        }
    }

    public char registerHomeworkArchivePart2(int idUser, String homeworkArchivePath, int idHomework) {
        try {
            /*
            CREATE TABLE pcf_user_upload_homework_archive( -- Usuario <-> Tarea <-> Archivo
                id_user INT,
                id_homework_archive INT,
                id_homework INT,
                score_homework INT
            );
            */

            pstm = connection.prepareStatement("INSERT INTO pcf_user_upload_homework_archive" +
                    " (id_user, id_homework_archive, id_homework)" + // [3] <- score DEFAULT 0
                    " VALUES (?, (SELECT pcf_homework_archive.id_homework_archive FROM pcf_homework_archive WHERE path_homework_archive = ?), ?);");
            pstm.setInt(1, idUser);
            pstm.setString(2, homeworkArchivePath);
            pstm.setInt(3, idHomework);

            pstm.executeUpdate();
            pstm.close();

            return 'A';
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 'B';
        }
    }

    public boolean deleteFileHomework(int idFile) { // ON DELETE CASCADE hace el resto
        try {
            pstm = connection.prepareStatement("DELETE FROM pcf_homework_archive WHERE id_homework_archive = ?;");

            pstm.setInt(1, idFile);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public LinkedList<HomeworkArchive> selectHomeworkFiles(int idUser, int idHomework) {
        LinkedList<HomeworkArchive> homeworkArchives = new LinkedList<>();
        /*
        CREATE TABLE pcf_homework_archive( -- Del Alumno, obviamente
            id_homework_archive INT AUTO_INCREMENT PRIMARY KEY,
            path_homework_archive VARCHAR(500) NOT NULL
        );
        CREATE TABLE pcf_user_upload_homework_archive( --Usuario<->Tarea<->Archivo
            id_user INT,
            id_homework_archive INT,
            id_homework INT,
            score_homework INT
        );
        */
        try {
            String selection = "SELECT * FROM pcf_homework_archive, pcf_user_upload_homework_archive" +
                    " WHERE pcf_homework_archive.id_homework_archive = pcf_user_upload_homework_archive.id_homework_archive" +
                    " AND pcf_user_upload_homework_archive.id_user = ?" +
                    " AND pcf_user_upload_homework_archive.id_homework = ?";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idUser);
            pstm.setInt(2, idHomework);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idHomeworkArchive = rst.getInt("pcf_homework_archive.id_homework_archive");
                    String path = rst.getString("pcf_homework_archive.path_homework_archive");

                    HomeworkArchive homeworkArchive = new HomeworkArchive(idHomeworkArchive, path);
                    homeworkArchives.add(homeworkArchive);
                }
                return homeworkArchives;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedHashMap<User, LinkedList<HomeworkArchive>> selectHomeworkStudentsFiles(int idHomework) {
        LinkedHashMap<User, LinkedList<HomeworkArchive>> studentsFiles = new LinkedHashMap<>();
        User user = new User();
        LinkedList<HomeworkArchive> homeworkArchives = new LinkedList<>();
        /*
        CREATE TABLE pcf_homework_archive( -- Del Alumno, obviamente
            id_homework_archive INT AUTO_INCREMENT PRIMARY KEY,
            path_homework_archive VARCHAR(500) NOT NULL
        );
        CREATE TABLE pcf_user_upload_homework_archive( --Usuario<->Tarea<->Archivo
            id_user INT,
            id_homework_archive INT,
            id_homework INT,
            score_homework INT
        );
        */
        try {
            String selection = "SELECT *" +
                    " FROM pcf_user, pcf_user_upload_homework_archive, pcf_homework_archive" +
                    " WHERE pcf_user.id_user = pcf_user_upload_homework_archive.id_user" +
                    " AND pcf_user_upload_homework_archive.id_homework_archive = pcf_homework_archive.id_homework_archive" +
                    " AND pcf_user_upload_homework_archive.id_homework = ?" +
                    " ORDER BY pcf_user.id_user ASC;";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idHomework);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idUser = rst.getInt("pcf_user.id_user");
                    String name = rst.getString("pcf_user.name_user");
                    double score = rst.getDouble("pcf_user_upload_homework_archive.score");

                    if (user.getIdUser() != idUser) {
                        user = new User(idUser, name, score);
                        homeworkArchives = new LinkedList<>();
                    }

                    int idHomeworkArchive = rst.getInt("pcf_homework_archive.id_homework_archive");
                    String path = rst.getString("pcf_homework_archive.path_homework_archive");

                    HomeworkArchive homeworkArchive = new HomeworkArchive(idHomeworkArchive, path);
                    homeworkArchives.add(homeworkArchive);

                    studentsFiles.put(user, homeworkArchives);
                }
            }
            return studentsFiles;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // ROL PROFESOR
    //
    public boolean updateScore(double score, int idStudent, int idHomework) {
        try {
            pstm = connection.prepareStatement("UPDATE pcf_user_upload_homework_archive SET"
                    + " score = ? WHERE id_user = ? AND id_homework = ?;");

            pstm.setDouble(1, score);
            pstm.setInt(2, idStudent);
            pstm.setInt(3, idHomework);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public String getPathArchiveHomework(int idArchive) {
        try {
            String selection = "SELECT pcf_homework_archive.path_homework_archive FROM pcf_homework_archive" +
                    " WHERE pcf_homework_archive.id_homework_archive = ?";

            pstm = connection.prepareStatement(selection);
            pstm.setInt(1, idArchive);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    return rst.getString("pcf_homework_archive.path_homework_archive");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return "Error";
    }
}
