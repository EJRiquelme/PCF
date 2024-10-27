package operators;

import communication.ConnectionToServer;
import communication.ListenServer;
import communication.SharedStore;
import objects.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class CoursesOperator {
    private ConnectionToServer connectionToServer;
    private ListenServer listener;
    private SharedStore sharedStore;

    private String[] finalArguments;

    private Course course;

    private int idCourse;
    private String name;
    private String shortPresentation;
    private String longPresentation;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean hidden;
    private boolean closed;
    private int idTeacher;

    public CoursesOperator(ConnectionToServer connectionToServer, ListenServer listener) {
        this.connectionToServer = connectionToServer;
        this.listener = listener;
        this.sharedStore = SharedStore.getInstance();
    }

    // Motor de busqueda -> Cursos
    //
    //
    public void searchCourse(String[] serverArguments) {
        sharedStore.setCoursesList(new LinkedList<Course>()); // Reinicio la lista
        int coursesQuantity = Integer.parseInt(serverArguments[1]); // Cantidad Cursos recibidos

        for (int i = 0; i < coursesQuantity; i++) { // Lectura de los Cursos
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idCourse = Integer.parseInt(finalArguments[0]);
            name = finalArguments[1];
            shortPresentation = finalArguments[2];
            longPresentation = finalArguments[3];
            startDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[4]);
            endDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[5]);
            hidden = Boolean.parseBoolean(finalArguments[6]);
            idTeacher = Integer.parseInt(finalArguments[7]);

            Course course = new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, idTeacher);
            sharedStore.getCoursesList().add(course); // Almacenamos los cursos
        }
        connectionToServer.receiveServerResponse(); // S16.0 -> Fin del envio
    }

    public void coursesITeach(String[] serverArguments) {
        int teacherCursesQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setCoursesListTeacher(new LinkedList<Course>()); // Reinicio la lista

        for (int i = 0; i < teacherCursesQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idCourse = Integer.parseInt(finalArguments[0]);
            name = finalArguments[1];
            shortPresentation = finalArguments[2];
            longPresentation = finalArguments[3];
            startDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[4]);
            endDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[5]);
            hidden = Boolean.parseBoolean(finalArguments[6]);
            idTeacher = Integer.parseInt(finalArguments[7]);

            Course course = new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, idTeacher);
            sharedStore.getCoursesListTeacher().add(course); // Almacenamos los Mensajes Recibidos
        }
        connectionToServer.receiveServerResponse(); // S16.0 -> Fin del envio
    }

    public void coursesIReceive(String[] serverArguments) {
        int studentCursesQuantity = Integer.parseInt(serverArguments[1]);

        sharedStore.setCoursesListStudent(new LinkedList<Course>()); // Reinicio la lista

        for (int i = 0; i < studentCursesQuantity; i++) {
            finalArguments = connectionToServer.receiveServerResponse().split("#");

            idCourse = Integer.parseInt(finalArguments[0]);
            name = finalArguments[1];
            shortPresentation = finalArguments[2];
            longPresentation = finalArguments[3];
            startDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[4]);
            endDate = sharedStore.getConversions().convertStringToLocalDate(finalArguments[5]);
            hidden = Boolean.parseBoolean(finalArguments[6]);
            closed = Boolean.parseBoolean(finalArguments[7]);
            idTeacher = Integer.parseInt(finalArguments[8]);

            Course course = new Course(idCourse, name, shortPresentation, longPresentation, startDate, endDate, hidden, closed, idTeacher);
            sharedStore.getCoursesListStudent().add(course); // Almacenamos los Mensajes Recibidos
        }
        connectionToServer.receiveServerResponse(); // S17.0 -> Fin del envio
    }

    public void chargeVirtualClass(String[] serverArguments) {

        LinkedHashMap<Unit, LinkedList<Resource>> virtualClassMap = new LinkedHashMap<>();
        Unit unit = new Unit();
        LinkedList<Resource> resources = new LinkedList<>();
        Resource resource = new Resource();

        for (int i = 1; i < serverArguments.length; i++) {

            int idUnit = Integer.parseInt(serverArguments[i]);
            String titleUnit = serverArguments[i + 1];
            int orderUnit = Integer.parseInt(serverArguments[i + 2]);
            boolean hiddenUnit = Boolean.parseBoolean(serverArguments[i + 3]);
            int percentageExercises = Integer.parseInt(serverArguments[i + 4]);
            int percentageControls = Integer.parseInt(serverArguments[i + 5]);
            int percentageExams = Integer.parseInt(serverArguments[i + 6]);
            int percentageTests = Integer.parseInt(serverArguments[i + 7]);
            int idCourse = Integer.parseInt(serverArguments[i + 8]);

            unit = new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
            resources = new LinkedList<>();
            i += 8;

            for (int z = i + 1; z < serverArguments.length; z++) {
                if (serverArguments[z].equals(">>>>>>>>>>")) {
                    i++;
                    break;
                }
                if (serverArguments[z + 1].equals("null")) {
                    z += 7;
                    i = z;
                    break;
                }
                int idResource = Integer.parseInt(serverArguments[z]);
                String titleResource = serverArguments[z + 1];
                String presentation = serverArguments[z + 2];
                String type = serverArguments[z + 3];
                int order = Integer.parseInt(serverArguments[z + 4]);
                boolean hidden = Boolean.parseBoolean(serverArguments[z + 5]);
                int idUnitResource = Integer.parseInt(serverArguments[z + 6]);

                resource = new Resource(idResource, titleResource, presentation, type, order, hidden, idUnitResource);
                resources.add(resource);
                z += 6;
                i = z;
            }
            virtualClassMap.put(unit, resources);
        }
        sharedStore.setVirtualClassMap(virtualClassMap);
    }
}
