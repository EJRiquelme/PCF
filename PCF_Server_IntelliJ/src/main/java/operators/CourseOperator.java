package operators;

import objects.Course;
import objects.Resource;
import objects.Unit;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class CourseOperator {
    private ClientServiceThread serviceThread;
    private SharedObject sharedObject;
    private Conversions conversions;

    private String messageToClient;

    private int idCourse;
    private String name;
    private String shortPresentation;
    private String longPresentation;
    private Date startDate;
    private Date endDate;
    private boolean hidden;
    private boolean closed;
    private int idTeacher;

    public CourseOperator(ClientServiceThread serviceThread, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = serviceThread;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String registerCourse(String[] clientArguments) throws SQLException {
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(false);
        Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint();
        messageToClient = "#" + "Ok";

        try {
            name = clientArguments[1];
            shortPresentation = clientArguments[2];
            longPresentation = clientArguments[3];
            startDate = conversions.convertStringToDate(clientArguments[4]);
            endDate = conversions.convertStringToDate(clientArguments[5]);
            idTeacher = Integer.parseInt(clientArguments[6]);

            Course courseToRegister = new Course(name, shortPresentation, longPresentation, startDate, endDate, idTeacher);
            LinkedList<String> courseTags = new LinkedList<>(Arrays.asList(clientArguments).subList(7, clientArguments.length)); // Las etiquetas del curso

            String registerResult = sharedObject.getMysqlConnection().registerCourse(courseToRegister, courseTags);

            if (registerResult.equals("Error")) {
                return ".1" + "#" + "Error: Error de registro";
            }

            /* String registerTagsResult = sharedObject.getMysqlConnection().registerCourseNewTags(courseTags); // Registro las Tags en la BBDD

            if (registerTagsResult.equals("Clave")) { // TODO ¿?
                return ".1" + "#" + "Error: Etiqueta duplicada";
            }

            */
            idCourse = Integer.parseInt(registerResult);
            File courseDirectory = new File("users" + File.separator + serviceThread.getUser().getEmail() + File.separator + idCourse);
            courseDirectory.mkdirs(); // Nombre de carpeta: idCurso

            sharedObject.getMysqlConnection().getConnection().commit();
        } catch (SQLException ex) {
            sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
            messageToClient = ".1";
        }
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(true);

        return messageToClient;
    }

    public String updateCourse(String[] clientArguments) throws SQLException {
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(false);
        Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint();
        messageToClient = "#" + "Ok";

        try {
            idCourse = Integer.parseInt(clientArguments[1]);
            name = clientArguments[2];
            shortPresentation = clientArguments[3];
            longPresentation = clientArguments[4];
            startDate = conversions.convertStringToDate(clientArguments[5]);
            endDate = conversions.convertStringToDate(clientArguments[6]);
            hidden = Boolean.parseBoolean(clientArguments[7]);
            idTeacher = Integer.parseInt(clientArguments[8]);

            Course courseToUpdate = new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, idTeacher);
            LinkedList<String> courseTagsUpdate = new LinkedList<>(Arrays.asList(clientArguments).subList(9, clientArguments.length)); // Las etiquetas del curso

            String updateResult = sharedObject.getMysqlConnection().updateCourse(courseToUpdate, courseTagsUpdate);

            if (!courseTagsUpdate.isEmpty()) {
                sharedObject.getMysqlConnection().registerCourseNewTags(courseTagsUpdate); // Registro las Nuevas Tags en la BBDD
                ArrayList<Integer> idsTags = sharedObject.getMysqlConnection().getTagsIds(courseTagsUpdate); // Recojo las ids de las Tags
                sharedObject.getMysqlConnection().registerCourseTagsRelation(idCourse, idsTags); // Registro la relación entre el Curso y las Tags
            }
// TODO -> Probar
            if (updateResult.equals("Error")) {
                return "Error: Error de actualización";
            } else if (updateResult.equals("Clave")) {
                return "Error: Clave duplicada";
            }
            sharedObject.getMysqlConnection().getConnection().commit();
        } catch (SQLException ex) {
            sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
            messageToClient = ".1";
        }
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(true);

        return messageToClient;
    }

    public String deleteCourse(String[] clientArguments) {
        boolean deleteResult = sharedObject.getMysqlConnection().deleteCourse(Integer.parseInt(clientArguments[1]));

        if (!deleteResult) {
            return "Error: Error de borrado";
        }

        File directoryToDelete = new File("users" + File.separator + serviceThread.getUser().getEmail() + File.separator + clientArguments[1]);
        sharedObject.deleteDirectory(directoryToDelete);

        return "Ok";
    }

    public String searchCourses(String[] clientArguments) {

        LinkedList<String> searchCoursesParameters = new LinkedList<>(Arrays.asList(clientArguments).subList(1, clientArguments.length));

        LinkedList<Course> searchCourses = sharedObject.getMysqlConnection().searchMultipleCourses(searchCoursesParameters);

        if (searchCourses == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(22) + "#" + searchCourses.size()); // S16 # CantidadDeResultadosCursos

        for (Course c : searchCourses) {
            String startDate = conversions.convertDateToString(c.getStartDate());
            String endDate = conversions.convertDateToString(c.getEndDate());

            String courseToClient = c.getIdCourse()
                    + "#" + c.getName()
                    + "#" + c.getShortPresentation()
                    + "#" + c.getLongPresentation()
                    + "#" + startDate
                    + "#" + endDate
                    + "#" + c.isHidden()
                    + "#" + c.getIdTeacher();

            serviceThread.sendServerResponse(courseToClient);
        }
        return "Ok";
    }

    public String coursesITeach(String[] clientArguments) {
        int teacherId = Integer.parseInt(clientArguments[1]);
        LinkedList<Course> coursesITeach = sharedObject.getMysqlConnection().searchCoursesITeach(teacherId);

        if (coursesITeach == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(18) + "#" + coursesITeach.size()); // S18#Cantidad -> Cursos

        for (Course c : coursesITeach) {
            String startDate = conversions.convertDateToString(c.getStartDate());
            String endDate = conversions.convertDateToString(c.getEndDate());

            String courseToClient = c.getIdCourse()
                    + "#" + c.getName()
                    + "#" + c.getShortPresentation()
                    + "#" + c.getLongPresentation()
                    + "#" + startDate
                    + "#" + endDate
                    + "#" + c.isHidden()
                    + "#" + c.getIdTeacher();

            serviceThread.sendServerResponse(courseToClient);
        }
        return "Ok";
    }

    public String coursesIReceive(String[] clientArguments) {
        int studentId = Integer.parseInt(clientArguments[1]);
        LinkedList<Course> coursesIReceive = sharedObject.getMysqlConnection().searchCoursesIReceive(studentId);

        if (coursesIReceive == null) {
            return "Error: Error de lectura";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(19) + "#" + coursesIReceive.size()); // S19#Cantidad -> Cursos

        for (Course c : coursesIReceive) {
            String startDate = conversions.convertDateToString(c.getStartDate());
            String endDate = conversions.convertDateToString(c.getEndDate());

            String courseToClient = c.getIdCourse()
                    + "#" + c.getName()
                    + "#" + c.getShortPresentation()
                    + "#" + c.getLongPresentation()
                    + "#" + startDate
                    + "#" + endDate
                    + "#" + c.isHidden()
                    + "#" + c.isClosed()
                    + "#" + c.getIdTeacher();

            serviceThread.sendServerResponse(courseToClient);
        }
        return "Ok";
    }

    public String openCourse(String[] clientArguments) {
        boolean openResult = sharedObject.getMysqlConnection().openCourse(Integer.parseInt(clientArguments[1]));
        if (!openResult) {
            return "Error: Error de actualización";
        }
        return "Ok";
    }

    // AULA VIRTUAL (guardar en memoria "activeAulaVirtual", listar temas y recursos) -> 0.C27, 1.Id Curso
    //
    public String virtualClass(String[] clientArguments) {
        idCourse = Integer.parseInt(clientArguments[1]);
        Course activeVirtualClass = sharedObject.getMysqlConnection().searchCourse(idCourse);
        if (activeVirtualClass == null) {
            return "Error: Error de Lectura de Curso";
        }
        serviceThread.setActiveVirtualClass(activeVirtualClass); // Guardo en Memoria el Curso Activo para facilitar futuras operaciones

        LinkedHashMap<Unit, LinkedList<Resource>> virtualClass = null;

        if (serviceThread.getUser().getIdUser() == activeVirtualClass.getIdTeacher()) {
            virtualClass = sharedObject.getMysqlConnection().consultVirtualClassTeacher(idCourse);
        } else {
            virtualClass = sharedObject.getMysqlConnection().consultVirtualClassStudent(idCourse);
        }


        /*
        int idUnit;
        private String titleUnit;
        private int orderUnit;

        private boolean hiddenUnit;
        private int percentageExercises;
        private int percentageControls;

        private int percentageExams;
        private int percentageTests;
        private int idCourse;
        */
        messageToClient = "";
        for (Unit unit : virtualClass.keySet()) {
            messageToClient += "#" + unit.getIdUnit()
                    + "#" + unit.getTitleUnit()
                    + "#" + unit.getOrderUnit()

                    + "#" + unit.isHiddenUnit()
                    + "#" + unit.getPercentageExercises()
                    + "#" + unit.getPercentageControls()

                    + "#" + unit.getPercentageExams()
                    + "#" + unit.getPercentageTests()
                    + "#" + unit.getIdCourse();

            /*
            int idResource;
            private String titleResource;
            private String presentation;

            private String type;
            private int order;

            private boolean hidden;
            private int idUnit;

            */
            for (Resource resource : virtualClass.get(unit)) {
                messageToClient += "#" + resource.getIdResource()
                        + "#" + resource.getTitleResource()
                        + "#" + resource.getPresentation()

                        + "#" + resource.getType()
                        + "#" + resource.getOrder()

                        + "#" + resource.isHidden()
                        + "#" + resource.getIdUnit();
                ;
            }
            messageToClient += "#>>>>>>>>>>"; // <- Lo siguiente es un Tema
        }

        return messageToClient;
    }

    /*
    public String virtualClass(String[] clientArguments) {
        Course activeVirtualClass = sharedObject.getMysqlConnection().searchCourse(Integer.parseInt(clientArguments[1]));

        if (activeVirtualClass == null) {
            return "Error: Error de Lectura de Curso";
        }
        serviceThread.setActiveVirtualClass(activeVirtualClass); // Guardo en Memoria el Curso Activo para facilitar futuras operaciones

        LinkedList<Unit> units;

        if (serviceThread.getUser().getIdUser() == activeVirtualClass.getIdTeacher()) {
            units = sharedObject.getMysqlConnection().listUnitsTeacher(activeVirtualClass.getIdCourse());
        } else {
            units = sharedObject.getMysqlConnection().listUnitsStudent(activeVirtualClass.getIdCourse());
        }

        if (units == null) {
            return "Error: Error de Lectura de Temas";
        }

        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(24) + "#" + units.size()); // S26 # Cantidad -> Unidades

        for (Unit u : units) {
            LinkedList<Resource> resources;

            if (serviceThread.getUser().getIdUser() == activeVirtualClass.getIdTeacher()) {
                resources = sharedObject.getMysqlConnection().listResourcesTeacher(u.getIdUnit());
            } else {
                resources = sharedObject.getMysqlConnection().listResourcesStudent(u.getIdUnit());
            }

            if (resources == null) {
                return "Error: Error de Lectura de Recursos";
            }

            String unitToClient = u.getIdUnit()
                    + "#" + u.getTitleUnit()
                    + "#" + u.getOrderUnit()
                    + "#" + u.isHiddenUnit()
                    // + "#" + u.getPercentageUnit()
                    + "#" + u.getIdCourse()
                    + "#" + resources.size();

            serviceThread.sendServerResponse(unitToClient); // Unidad + Cantidad de Recursos Asignados a la Unidad

            for (Resource r : resources) {
                String resourcesToClient = r.getIdResource()
                        + "#" + r.getTitleResource()
                        + "#" + r.getPresentation()
                        + "#" + r.getType()
                        + "#" + r.getOrder()
                        + "#" + r.isHidden()
                        + "#" + r.getIdUnit();

                serviceThread.sendServerResponse(resourcesToClient); // Recursos Asignados a la Unidad
            }
        }
        serviceThread.setActiveResource(null);
        return "Ok";
    }
    */
}
