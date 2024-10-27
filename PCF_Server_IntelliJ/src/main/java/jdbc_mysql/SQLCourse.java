/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.Course;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.Resource;
import objects.Unit;
import tools.Conversions;

/**
 * @author william
 */
public class SQLCourse {
    /*
    CREATE TABLE pcf_course(
        id_course INT AUTO_INCREMENT PRIMARY KEY,
        name_course VARCHAR(50) NOT NULL,
        short_presentation_course VARCHAR(500) NOT NULL,
        long_presentation_course VARCHAR(1000), -- Opcional
        start_date_course DATE NOT NULL,
        end_date_course DATE NOT NULL, -- Para el acceso al Aula Virtual
        hidden_course BOOLEAN DEFAULT true, -- Para el Motor de Búsquedas
        closed_course BOOLEAN DEFAULT false, -- Da acceso al Aula Virtual de forma prematura y da por finalizado el curso: Debe ser ejecutado manualmente por el profesor
        id_teacher_user INT NOT NULL
    );
    ALTER TABLE pcf_course ADD UNIQUE KEY(name_course, id_teacher_user); -- Para evitar duplicados: Seguridad en el registro
     */

    private Connection connection;
    private PreparedStatement pstm;

    private Conversions conversions;

    private final String courseTable;

    private final String idCourseColumn;
    private final String nameCourseColumn;
    private final String shortPresentationCourseColumn;
    private final String longPresentationCourseColumn;
    private final String startDateCourseColumn;
    private final String endDateCourseColumn;
    private final String hiddenCourseColumn;
    private final String closedCourseColumn;
    private final String idTeacherColumn;

    public SQLCourse(Connection connection) {
        this.connection = connection;
        this.conversions = new Conversions();

        this.courseTable = "pcf_course";
        this.idCourseColumn = courseTable + ".id_course"; // 0
        this.nameCourseColumn = courseTable + ".name_course"; // 1
        this.shortPresentationCourseColumn = courseTable + ".short_presentation_course"; // 2
        this.longPresentationCourseColumn = courseTable + ".long_presentation_course"; // 3
        this.startDateCourseColumn = courseTable + ".start_date_course"; // 4
        this.endDateCourseColumn = courseTable + ".end_date_course"; // 5
        this.hiddenCourseColumn = courseTable + ".hidden_course"; // 6
        this.closedCourseColumn = courseTable + ".closed_course"; // 7
        this.idTeacherColumn = courseTable + ".id_teacher_user"; // 8
    }

    public int getIdCourse(Course course) {
        int idCourse = -1;
        try {
            pstm = connection.prepareStatement("SELECT " + idCourseColumn +
                    " FROM " + courseTable +
                    " WHERE " + nameCourseColumn + " = ?" + // (Nombre, idProfesor) => Clave Única
                    " AND " + idTeacherColumn + " = ?;");

            pstm.setString(1, course.getName());
            pstm.setInt(2, course.getIdTeacher());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    idCourse = rst.getInt(idCourseColumn);
                }
                return idCourse;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idCourse;
    }

    public boolean isStudentRegistered(int idUser, int idCourse) { // Un usuario no puede apuntarse multiples veces a un mismo curso
        try {
            /*
            CREATE TABLE pcf_user_receives_course(
                id_user INT,
                id_course INT
            );
             */
            pstm = connection.prepareStatement("SELECT * FROM pcf_user_receives_course WHERE id_user = ? AND id_course = ?;");

            pstm.setInt(1, idUser);
            pstm.setInt(2, idCourse);

            try (ResultSet rsts = pstm.executeQuery()) {
                while (rsts.next()) { // Si encuentra una sola coincidencia es que ya existe
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String registerStudent(int idUser, int idCourse) {
        try {
            /*
            CREATE TABLE pcf_user_receives_course(
                id_user INT,
                id_course INT
            );
             */
            pstm = connection.prepareStatement("INSERT INTO pcf_user_receives_course (id_user, id_course) VALUES (?,?);");

            pstm.setInt(1, idUser);
            pstm.setInt(2, idCourse);

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

    public String registerCourseNewTags(LinkedList<String> tags) { // Para el REGISTRO
        int index = 0;
        try {
            for (index = 0; index < tags.size(); index++) { // Guardo las Tags
                pstm = connection.prepareStatement("INSERT INTO pcf_course_tag (word_tag) VALUES (?);");
                pstm.setString(1, tags.get(index));
                pstm.executeUpdate();
                pstm.close();
            }
            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            LinkedList<String> restOfTheTags = new LinkedList<>(tags.subList(index + 1, tags.size())); // Si ya existe -> recursivo
            registerCourseNewTags(restOfTheTags);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public ArrayList<Integer> getTagsIds(LinkedList<String> tags) {
        try {
            ArrayList<Integer> idTags = new ArrayList();
            pstm = connection.prepareStatement("SELECT * FROM pcf_course_tag;");

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    for (String str : tags) {
                        if (rst.getString("word_tag").equals(str)) { // Compruebo coincidencias
                            idTags.add(rst.getInt("id_tag"));
                        }
                    }
                }

                return idTags;
            }

        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void registerCourseTagsRelation(int idCourse, ArrayList<Integer> tagsIds) {
        int index = 0;
        try {
            for (index = 0; index < tagsIds.size(); index++) {
                pstm = connection.prepareStatement("INSERT INTO pcf_tag_defines_course (id_tag, id_course) VALUES (?,?);"); // Almaceno la relación (Curso <---> Tag)

                pstm.setInt(1, tagsIds.get(index));
                pstm.setInt(2, idCourse);

                pstm.executeUpdate();
                pstm.close();
            }
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            ArrayList<Integer> restOfTagsIds = new ArrayList<>(tagsIds.subList(index + 1, tagsIds.size()));
            registerCourseTagsRelation(idCourse, restOfTagsIds);
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // REGISTRO CURSO: HECHO
    public String registerCourse(Course course, LinkedList<String> tags) { // Para el REGISTRO
        try {
            pstm = connection.prepareStatement("INSERT INTO " + courseTable + " ("
                    + nameCourseColumn + ", "
                    + shortPresentationCourseColumn + ", "
                    + longPresentationCourseColumn + ", "
                    + startDateCourseColumn + ", "
                    + endDateCourseColumn + ", " // [6][7] <- hidden DEFAULT true
                    + idTeacherColumn + ") VALUES (?, ?, ?, ?, ?, ?);");

            pstm.setString(1, course.getName());
            pstm.setString(2, course.getShortPresentation());
            pstm.setString(3, course.getLongPresentation());
            pstm.setDate(4, course.getStartDate());
            pstm.setDate(5, course.getEndDate());
            pstm.setInt(6, course.getIdTeacher());

            pstm.executeUpdate();
            pstm.close();

            int idCourse = getIdCourse(course); // Recojo el id del Curso para las siguientes operaciones

            tags.add("*"); // Tag que todos los cursos debe tener

            registerCourseNewTags(tags); // Registro las Tags en la BBDD
            ArrayList<Integer> idsTags = getTagsIds(tags); // Recojo las ids de las Tags
            registerCourseTagsRelation(idCourse, idsTags); // Registro la relación entre el Curso y las Tags

            return String.valueOf(idCourse);
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // UPDATE CURSO (Para modificaciones y Publicaciones): HECHO
    public String updateCourse(Course course, LinkedList<String> tags) {
        try {
            pstm = connection.prepareStatement("UPDATE " + courseTable + " SET "
                    + nameCourseColumn + " = ?, "
                    + shortPresentationCourseColumn + " = ?, "
                    + longPresentationCourseColumn + " = ?, "
                    + startDateCourseColumn + " = ?, "
                    + endDateCourseColumn + " = ?, "
                    + hiddenCourseColumn + " = ?, " // [7] <- closed DEFAULT true
                    + idTeacherColumn + " = ? WHERE " + idCourseColumn + " = ?;");

            pstm.setString(1, course.getName());
            pstm.setString(2, course.getShortPresentation());
            pstm.setString(3, course.getLongPresentation());
            pstm.setDate(4, course.getStartDate());
            pstm.setDate(5, course.getEndDate());
            pstm.setBoolean(6, course.isHidden());
            pstm.setInt(7, course.getIdTeacher());

            pstm.setInt(8, course.getIdCourse());

            pstm.executeUpdate();
            pstm.close();

            if (!tags.isEmpty()) {
                registerCourseNewTags(tags); // Registro las Nuevas Tags en la BBDD
                ArrayList<Integer> idsTags = getTagsIds(tags); // Recojo las ids de las Tags
                registerCourseTagsRelation(course.getIdCourse(), idsTags); // Registro la relación entre el Curso y las Tags
            }

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    // OPEN CURSO: HECHO
    public boolean openCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("UPDATE " + courseTable +
                    " SET " + closedCourseColumn + " = false" +
                    " WHERE " + idCourseColumn + " = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // CLOSE CURSO: HECHO
    public boolean closeCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("UPDATE " + courseTable +
                    " SET " + closedCourseColumn + " = true" +
                    " WHERE " + idCourseColumn + " = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // FINAL CURSO
    public boolean deleteStudents(int idCourse) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM pcf_user_receives_course WHERE id_course = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean deleteCourse(int idCourse) { // ON DELETE CASCADE
        try {
            pstm = connection.prepareStatement("DELETE FROM pcf_course WHERE id_course = ?;");

            pstm.setInt(1, idCourse);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // BUSCAR CURSO: HECHO
    public Course searchCourse(int idCourse) {
        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM " + courseTable +
                    " WHERE " + idCourseColumn + " = ?;");
            pstm.setInt(1, idCourse);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    String name = rst.getString(nameCourseColumn);
                    String shortPresentation = rst.getString(shortPresentationCourseColumn);
                    String longPresentation = rst.getString(longPresentationCourseColumn);
                    Date startDate = rst.getDate(startDateCourseColumn);
                    Date endDate = rst.getDate(endDateCourseColumn);
                    boolean hidden = rst.getBoolean(hiddenCourseColumn);
                    boolean closed = rst.getBoolean(closedCourseColumn);
                    int idTeacher = rst.getInt(idTeacherColumn);

                    return new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher);
                }
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Lista Cursos que Imparto: HECHO
    public LinkedList<Course> searchCoursesITeach(int idTeacher) {
        LinkedList<Course> courses = new LinkedList();
        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM " + courseTable +
                    " WHERE " + idTeacherColumn + " = ?" +
                    " ORDER BY " + startDateCourseColumn + ";");
            pstm.setInt(1, idTeacher);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idCourse = rst.getInt(idCourseColumn);
                    String name = rst.getString(nameCourseColumn);
                    String shortPresentation = rst.getString(shortPresentationCourseColumn);
                    String longPresentation = rst.getString(longPresentationCourseColumn);
                    Date startDate = rst.getDate(startDateCourseColumn);
                    Date endDate = rst.getDate(endDateCourseColumn);
                    boolean hidden = rst.getBoolean(hiddenCourseColumn);
                    boolean closed = rst.getBoolean(closedCourseColumn);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Lista Cursos que Recibo: HECHO
    public LinkedList<Course> searchCoursesIReceive(int studentId) {
        LinkedList<Course> courses = new LinkedList();

        String userReceivesCourseTable = "pcf_user_receives_course";
        String idCourseUserReceivesCourseColumn = userReceivesCourseTable + ".id_course";
        String idUserUserReceivesCourseColumn = userReceivesCourseTable + ".id_user";

        try {
            pstm = connection.prepareStatement("SELECT " + idCourseColumn + ", " +
                    nameCourseColumn + ", " +
                    shortPresentationCourseColumn + ", " +
                    longPresentationCourseColumn + ", " +
                    startDateCourseColumn + ", " +
                    endDateCourseColumn + ", " +
                    hiddenCourseColumn + ", " +
                    closedCourseColumn + ", " +
                    idTeacherColumn + " " +
                    "FROM " + courseTable + ", " + userReceivesCourseTable + " " +
                    "WHERE " + idCourseColumn + " = " + idCourseUserReceivesCourseColumn + " " +
                    "AND " + endDateCourseColumn + " > CURRENT_DATE() " + // Si la fecha de finalización ha pasado no se recoge
                    "AND " + idUserUserReceivesCourseColumn + " = ? " +
                    "ORDER BY " + startDateCourseColumn + ";");
            pstm.setInt(1, studentId);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idCourse = rst.getInt(idCourseColumn);
                    String name = rst.getString(nameCourseColumn);
                    String shortPresentation = rst.getString(shortPresentationCourseColumn);
                    String longPresentation = rst.getString(longPresentationCourseColumn);
                    Date startDate = rst.getDate(startDateCourseColumn);
                    Date endDate = rst.getDate(endDateCourseColumn);
                    boolean hidden = rst.getBoolean(hiddenCourseColumn);
                    boolean closed = rst.getBoolean(closedCourseColumn);
                    int idTeacher = rst.getInt(idTeacherColumn);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // Para el Motor de Busquedas: HECHO
    public LinkedList<Course> searchMultipleCourses(LinkedList<String> parameters) { // 0.CurrentDate, 1.NombreCurso/none, 2.Provincia/none, 3.Municipio/none, 4.Tag/none, 5.Tag?, 6.Tag?, etc.
        LinkedList<Course> courses = new LinkedList();
        java.sql.Date sqlCoursestartDate = conversions.convertStringToDate(parameters.get(0));

        String courseTagTable = "pcf_course_tag";
        String idTagCourseTagColumn = courseTagTable + ".id_tag";
        String wordTagCourseTagColumn = courseTagTable + ".word_tag";

        String tagDefinesCourseTable = "pcf_tag_defines_course";
        String idCourseTagDefinesCourseColumn = tagDefinesCourseTable + ".id_course";
        String idTagTagDefinesCourseColumn = tagDefinesCourseTable + ".id_tag";

        String userTable = "pcf_user";
        String idUserUserColumn = userTable + ".id_user";
        String provinceUserColumn = userTable + ".province_user";
        String townshipUserColumn = userTable + ".township_user";

        try {
            String selection = "SELECT " + idCourseColumn + "," +
                    " " + nameCourseColumn + "," +
                    " " + shortPresentationCourseColumn + "," +
                    " " + longPresentationCourseColumn + "," +
                    " " + startDateCourseColumn + "," +
                    " " + endDateCourseColumn + "," +
                    " " + hiddenCourseColumn + "," +
                    " " + closedCourseColumn + "," +
                    " " + idTeacherColumn +
                    " FROM " + courseTable + "," +
                    " " + courseTagTable + "," +
                    " " + tagDefinesCourseTable + "," +
                    " " + userTable +
                    " WHERE " + idCourseColumn + " = " + idCourseTagDefinesCourseColumn +   // Siempre que Curso sea definido por una Tag
                    " AND " + idTagCourseTagColumn + " = " + idTagTagDefinesCourseColumn +  // Y Tag defina a un Curso
                    " AND " + idTeacherColumn + " = " + idUserUserColumn +                  // Y Profesor sea Usuario
                    " AND " + hiddenCourseColumn + " = false" +
                    " AND " + startDateCourseColumn + " > '" + sqlCoursestartDate + "'";    // Y la fecha de inicio no se haya pasado

            if (!parameters.get(1).equals("none")) { // Nombre Curso
                selection += " AND " + nameCourseColumn + " LIKE '" + parameters.get(1) + "%'";
            }
            if (!parameters.get(2).equals("none")) { // Provincia
                selection += " AND " + provinceUserColumn + " LIKE '" + parameters.get(2) + "%'";
            }
            if (!parameters.get(3).equals("none")) { // Municipio
                selection += " AND " + townshipUserColumn + " LIKE '" + parameters.get(3) + "%'";
            }
            if (!parameters.get(4).equals("none")) { // Primera Tag
                selection += " AND " + wordTagCourseTagColumn + " LIKE '" + parameters.get(4) + "%'";
            } else {
                selection += " AND " + wordTagCourseTagColumn + " LIKE '%'"; // La etiqueta que TODOS los cursos tienen
            }

            for (int i = 5; i < parameters.size(); i++) {
                selection += " AND " + wordTagCourseTagColumn + " LIKE '" + parameters.get(i) + "%'"; // Si hay más etiquetas...
            }

            pstm = connection.prepareStatement(selection +
                    " GROUP BY " + idCourseColumn +
                    " ORDER BY " + startDateCourseColumn + ";");

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idCourse = rst.getInt(idCourseColumn);
                    String name = rst.getString(nameCourseColumn);
                    String shortPresentation = rst.getString(shortPresentationCourseColumn);
                    String longPresentation = rst.getString(longPresentationCourseColumn);
                    Date startDate = rst.getDate(startDateCourseColumn);
                    Date endDate = rst.getDate(endDateCourseColumn);
                    boolean hidden = rst.getBoolean(hiddenCourseColumn);
                    boolean closed = rst.getBoolean(closedCourseColumn);
                    int idTeacher = rst.getInt(idTeacherColumn);

                    courses.add(new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher));
                }
                return courses;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassTeacher(int idCourseVirtualClass) {
        LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap = new LinkedHashMap<>();
        Unit unit = new Unit();
        LinkedList<Resource> resources = new LinkedList<>();

        String unitTable = "pcf_unit";
        String idUnitUnitColumn = unitTable + ".id_unit";
        String titleUnitUnitColumn = unitTable + ".title_unit";
        String orderUnitUnitColumn = unitTable + ".order_unit";
        String hiddenUnitUnitColumn = unitTable + ".hidden_unit";
        String percentageExercisesUnitColumn = unitTable + ".percentage_exercises";
        String percentageControlsUnitColumn = unitTable + ".percentage_controls";
        String percentageExamsUnitColumn = unitTable + ".percentage_exams";
        String percentageTestsUnitColumn = unitTable + ".percentage_tests";
        String idCourseUnitColumn = unitTable + ".id_course";

        String resourceTable = "pcf_resource";
        String idResourceResourceColumn = resourceTable + ".id_resource";
        String titleResourceResourceColumn = resourceTable + ".title_resource";
        String presentationResourceResourceColumn = resourceTable + ".presentation_resource";
        String typeResourceResourceColumn = resourceTable + ".type_resource";
        String orderResourceResourceColumn = resourceTable + ".order_resource";
        String hiddenResourceResourceColumn = resourceTable + ".hidden_resource";
        String idUnitResourceColumn = resourceTable + ".id_unit";

        try {
            pstm = connection.prepareStatement("SELECT " + idUnitUnitColumn + // Datos Temas
                    ", " + titleUnitUnitColumn +
                    ", " + orderUnitUnitColumn +
                    ", " + hiddenUnitUnitColumn +
                    ", " + percentageExercisesUnitColumn +
                    ", " + percentageControlsUnitColumn +
                    ", " + percentageExamsUnitColumn +
                    ", " + percentageTestsUnitColumn +
                    ", " + idCourseUnitColumn +
                    ", " + idResourceResourceColumn + // Datos Recursos
                    ", " + titleResourceResourceColumn +
                    ", " + presentationResourceResourceColumn +
                    ", " + typeResourceResourceColumn +
                    ", " + orderResourceResourceColumn +
                    ", " + hiddenResourceResourceColumn +
                    ", " + idUnitResourceColumn +
                    " FROM " + courseTable + "," +
                    " " + unitTable +
                    " LEFT JOIN " + resourceTable +
                    " ON " + idUnitUnitColumn + " = " + idUnitResourceColumn +
                    " WHERE " + idCourseColumn + " = " + idCourseUnitColumn +
                    " AND " + idCourseColumn + " = ?" +
                    " ORDER BY " + orderUnitUnitColumn + " ASC," +
                    " " + orderResourceResourceColumn + " ASC;");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    // TODOS LOS DATOS DE TEMA
                    int idUnit = rst.getInt(idUnitUnitColumn);
                    String titleUnit = rst.getString(titleUnitUnitColumn);
                    int orderUnit = rst.getInt(orderUnitUnitColumn);
                    boolean hiddenUnit = rst.getBoolean(hiddenUnitUnitColumn);
                    int percentageExercises = rst.getInt(percentageExercisesUnitColumn);
                    int percentageControls = rst.getInt(percentageControlsUnitColumn);
                    int percentageExams = rst.getInt(percentageExamsUnitColumn);
                    int percentageTests = rst.getInt(percentageTestsUnitColumn);
                    int idCourse = rst.getInt(idCourseUnitColumn);

                    if (unit.getIdUnit() != idUnit) {
                        unit = new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
                        resources = new LinkedList<>();
                    }

                    // TODOS LOS DATOS DE RECURSOS
                    int idResource = rst.getInt(idResourceResourceColumn);
                    String titleResource = rst.getString(titleResourceResourceColumn);
                    String presentationResource = rst.getString(presentationResourceResourceColumn);
                    String typeResource = rst.getString(typeResourceResourceColumn);
                    int orderResource = rst.getInt(orderResourceResourceColumn);
                    boolean hiddenResource = rst.getBoolean(hiddenResourceResourceColumn);
                    int idUnitResource = rst.getInt(idUnitResourceColumn);

                    Resource resource = new Resource(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
                    resources.add(resource);

                    virtualClassMap.put(unit, resources);
                }
                return virtualClassMap;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedHashMap<Unit, LinkedList<Resource>> consultVirtualClassStudent(int idCourseVirtualClass) {
        LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap = new LinkedHashMap<>();
        Unit unit = new Unit();
        LinkedList<Resource> resources = new LinkedList<>();

        String unitTable = "pcf_unit";
        String idUnitUnitColumn = unitTable + ".id_unit";
        String titleUnitUnitColumn = unitTable + ".title_unit";
        String orderUnitUnitColumn = unitTable + ".order_unit";
        String hiddenUnitUnitColumn = unitTable + ".hidden_unit";
        String percentageExercisesUnitColumn = unitTable + ".percentage_exercises";
        String percentageControlsUnitColumn = unitTable + ".percentage_controls";
        String percentageExamsUnitColumn = unitTable + ".percentage_exams";
        String percentageTestsUnitColumn = unitTable + ".percentage_tests";
        String idCourseUnitColumn = unitTable + ".id_course";

        String resourceTable = "pcf_resource";
        String idResourceResourceColumn = resourceTable + ".id_resource";
        String titleResourceResourceColumn = resourceTable + ".title_resource";
        String presentationResourceResourceColumn = resourceTable + ".presentation_resource";
        String typeResourceResourceColumn = resourceTable + ".type_resource";
        String orderResourceResourceColumn = resourceTable + ".order_resource";
        String hiddenResourceResourceColumn = resourceTable + ".hidden_resource";
        String idUnitResourceColumn = resourceTable + ".id_unit";

        try {
            pstm = connection.prepareStatement("SELECT *" +
                    " FROM " + courseTable + "," +
                    " " + unitTable +
                    " LEFT JOIN " + resourceTable +
                    " ON " + idUnitUnitColumn + " = " + idUnitResourceColumn +
                    " WHERE " + idCourseColumn + " = " + idCourseUnitColumn +
                    " AND " + idCourseColumn + " = ?" +
                    " AND " + hiddenUnitUnitColumn + " = false" +
                    " AND " + hiddenResourceResourceColumn + " = false" +
                    " ORDER BY " + orderUnitUnitColumn + " ASC," +
                    " " + orderResourceResourceColumn + " ASC;");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    // TODOS LOS DATOS DE TEMA
                    int idUnit = rst.getInt(idUnitUnitColumn);
                    String titleUnit = rst.getString(titleUnitUnitColumn);
                    int orderUnit = rst.getInt(orderUnitUnitColumn);
                    boolean hiddenUnit = rst.getBoolean(hiddenUnitUnitColumn);
                    int percentageExercises = rst.getInt(percentageExercisesUnitColumn);
                    int percentageControls = rst.getInt(percentageControlsUnitColumn);
                    int percentageExams = rst.getInt(percentageExamsUnitColumn);
                    int percentageTests = rst.getInt(percentageTestsUnitColumn);
                    int idCourse = rst.getInt(idCourseUnitColumn);

                    if (unit.getIdUnit() != idUnit) {
                        unit = new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
                        resources = new LinkedList<>();
                    }

                    // TODOS LOS DATOS DE RECURSOS
                    int idResource = rst.getInt(idResourceResourceColumn);
                    String titleResource = rst.getString(titleResourceResourceColumn);
                    String presentationResource = rst.getString(presentationResourceResourceColumn);
                    String typeResource = rst.getString(typeResourceResourceColumn);
                    int orderResource = rst.getInt(orderResourceResourceColumn);
                    boolean hiddenResource = rst.getBoolean(hiddenResourceResourceColumn);
                    int idUnitResource = rst.getInt(idUnitResourceColumn);

                    Resource resource = new Resource(idResource, titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
                    resources.add(resource);

                    virtualClassMap.put(unit, resources);
                }
                return virtualClassMap;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
