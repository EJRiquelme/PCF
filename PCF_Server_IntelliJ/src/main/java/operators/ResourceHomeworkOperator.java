package operators;

import objects.*;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Time;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ResourceHomeworkOperator {
    private final ClientServiceThread serviceThread;
    private final SharedObject sharedObject;
    private Conversions conversions;

    private Resource resource;
    private ResourceHomework resourceHomework;

    private int idResource; // Base
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int orderResource;
    private boolean hiddenResource;
    private int idUnitResource;

    private Date openDate; // Tarea
    private Time openTime;
    private Date closeDate;
    private Time closeTime;
    private int percentage;
    private String typeHomework;

    private String messageToClient;

    public ResourceHomeworkOperator(ClientServiceThread clientService, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = clientService;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String registerHomework(String[] clientArguments) throws SQLException {
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(false);
        Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint();
        messageToClient = "#" + "Ok";

        try {
            titleResource = clientArguments[1];
            presentationResource = clientArguments[2];
            typeResource = clientArguments[3];
            hiddenResource = Boolean.parseBoolean(clientArguments[4]);
            idUnitResource = Integer.parseInt(clientArguments[5]);
            orderResource = sharedObject.getMysqlConnection().getNewPositionResource(idUnitResource);

            resource = new Resource(titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
            sharedObject.getMysqlConnection().registerResource(resource);

            openDate = conversions.convertStringToDate(clientArguments[6]);
            openTime = conversions.convertStringToTime(clientArguments[7]);
            closeDate = conversions.convertStringToDate(clientArguments[8]);
            closeTime = conversions.convertStringToTime(clientArguments[9]);
            percentage = Integer.parseInt(clientArguments[10]);

            resourceHomework = new ResourceHomework(resource, openDate, openTime, closeDate, closeTime, percentage);
            sharedObject.getMysqlConnection().registerHomework(resourceHomework);

            sharedObject.getMysqlConnection().getConnection().commit();

            idResource = sharedObject.getMysqlConnection().getIdNewResource(resource);
            File courseDirectory = new File("users" + File.separator + serviceThread.getUser().getEmail()
                    + File.separator + serviceThread.getActiveVirtualClass().getIdCourse()
                    + File.separator + idUnitResource
                    + File.separator + idResource);
            courseDirectory.mkdirs(); // Carpeta "38"

        } catch (SQLException ex) {
            sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
            messageToClient = ".1";
        }
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(true);

        return messageToClient;
    }

    public String updateHomework(String[] clientArguments) throws SQLException {
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(false);
        Savepoint savepoint = sharedObject.getMysqlConnection().getConnection().setSavepoint();
        messageToClient = "#" + "Ok";

        try {
            idResource = Integer.parseInt(clientArguments[1]);
            titleResource = clientArguments[2];
            presentationResource = clientArguments[3];
            typeResource = clientArguments[4];
            hiddenResource = Boolean.parseBoolean(clientArguments[5]);

            resource = new Resource(idResource, titleResource, presentationResource, typeResource, hiddenResource);
            sharedObject.getMysqlConnection().updateResource(resource);

            openDate = conversions.convertStringToDate(clientArguments[6]);
            openTime = conversions.convertStringToTime(clientArguments[7]);
            closeDate = conversions.convertStringToDate(clientArguments[8]);
            closeTime = conversions.convertStringToTime(clientArguments[9]);
            percentage = Integer.parseInt(clientArguments[10]);

            ResourceHomework resourceHomework = new ResourceHomework(resource, openDate, openTime, closeDate, closeTime, percentage);
            sharedObject.getMysqlConnection().updateHomework(resourceHomework);

            sharedObject.getMysqlConnection().getConnection().commit();
        } catch (SQLException ex) {
            sharedObject.getMysqlConnection().getConnection().rollback(savepoint);
            messageToClient = ".1";
        }
        sharedObject.getMysqlConnection().getConnection().setAutoCommit(true);

        return messageToClient;
    }

    /*
        public String updateHomeworkPercentage(String[] clientArguments) { // UPDATE HOMEWORK PERCENTAGE ->
            // 0.C¿?, 1.Id Unit, 2.Titulo, 3.Posicion, 4.Oculto/No, 5.Porcenteja, 6.Id Curso, ¿7.IdTarea, ¿8.Porcentaje, ¿9.IdTarea, ¿10.Porcentaje...

            for (int i = 7; i < clientArguments.length; i++) {
                if (i % 2 != 0) {
                    idResource = Integer.parseInt(clientArguments[i]);
                    percentage = Integer.parseInt(clientArguments[i + 1]);
                    boolean firstResult = sharedObject.getMysqlConnection().updateHomeworkPercentage(idResource, percentage);
                    if (!firstResult) {
                        return "Error: Error en la actualización del Porcentaje de Nota";
                    }
                }
            }
            return "Ok";
        }
    */

    // VER TAREA [Profesor y Alumno] -> 0.C42, 1.idResource
    //
    public String consultHomework(String[] clientArguments) {
        int idHomework = Integer.parseInt(clientArguments[1]);
        ResourceHomework resourceHomework = sharedObject.getMysqlConnection().selectHomework(idHomework);

        String consultedHomework = "#" + conversions.convertDateToString(resourceHomework.getOpenDate()) +
                "#" + conversions.convertTimeToString(resourceHomework.getOpenTime()) +
                "#" + conversions.convertDateToString(resourceHomework.getCloseDate()) +
                "#" + conversions.convertTimeToString(resourceHomework.getCloseTime()) +
                "#" + resourceHomework.getPercentage();

        if (serviceThread.getUser().getIdUser() != serviceThread.getActiveVirtualClass().getIdTeacher()) {
            LinkedList<HomeworkArchive> homeworkArchives = sharedObject.getMysqlConnection().selectHomeworkFiles(serviceThread.getUser().getIdUser(), idHomework);

            for (HomeworkArchive homeworkArchive : homeworkArchives) {
                consultedHomework += "#" + homeworkArchive.getIdHomeworkArchive() +
                        "#" + homeworkArchive.getPath();
            }


            // REGISTRO
            //
            RecordOperator recordOperator = new RecordOperator(serviceThread, sharedObject, conversions);
            String event = "Accedido al recurso: " + resourceHomework.getTitleResource() + ".";
            recordOperator.registerRecord(event, resourceHomework.getIdResource());
        } else {
            LinkedHashMap<User, LinkedList<HomeworkArchive>> studentsArchives = sharedObject.getMysqlConnection().selectHomeworkStudentsFiles(idHomework);
            for (User user : studentsArchives.keySet()) {
                consultedHomework += "#" + user.getIdUser() +
                        "#" + user.getName() +
                        "#" + user.getScore();
                for (HomeworkArchive homeworkArchive : studentsArchives.get(user)) {
                    consultedHomework += "#" + homeworkArchive.getIdHomeworkArchive() +
                            "#" + homeworkArchive.getPath();
                }
                consultedHomework += "#>>>>>>>>>>"; // Siguiente alumno
            }

        }
        return consultedHomework;
    }

    private File getDirectory(String[] clientArguments, String emailTeacher) {
        String homeworkDirectory = "users" + File.separator + emailTeacher // profesor@gmail / idCurso / idTema / idRecurso (Tarea) / alumno@gmail / archivo
                + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // idCurso
                + File.separator + clientArguments[1] // idTema
                + File.separator + clientArguments[2] // idRecurso (Tarea)
                + File.separator + serviceThread.getUser().getEmail();
        File directory = new File(homeworkDirectory); // Genera el directorio para el archivo
        return directory;
    }

    // SUBIR ARCHIVO/S (Tareas) -> 0.C43, 1.idUnit, 2.idResource, 3?.idArchivo, 4?.nombreArchivo, 5?.idArchivo, 6?.nombreArchivo...
    //
    public String uploadFiles(String[] clientArguments) {
        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(43)); // "Solicitud de envio" aceptada

        String emailTeacher = sharedObject.getMysqlConnection().getUserEmail(serviceThread.getActiveVirtualClass().getIdTeacher());

        File directory = getDirectory(clientArguments, emailTeacher);
        directory.mkdirs();

        int fileName = 4; // 6, 8, 10...
        for (int idFile = 3; idFile < clientArguments.length; idFile += 2) {

            int id = Integer.parseInt(clientArguments[idFile]);

            String archivePath = "users" + File.separator + emailTeacher // profesor@gmail / idCurso / idTema / idRecurso (Tarea) / alumno@gmail / archivo
                    + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // idCurso
                    + File.separator + clientArguments[1] // idTema
                    + File.separator + clientArguments[2] // idRecurso (Tarea)
                    + File.separator + serviceThread.getUser().getEmail() // alumno@gmail
                    + File.separator + clientArguments[fileName]; // nombreArchivo

            if (id != -1) { // Borramos
                sharedObject.deleteFile(new File(archivePath));
                sharedObject.getMysqlConnection().deleteFileHomework(id);
            } else { // Subimos
                serviceThread.receiveArchive(archivePath); // Recibe el archivo

                sharedObject.getMysqlConnection().registerHomeworkArchivePart1(archivePath); // Guardamos ruta en la tabla "pcf_homework_archive"
                sharedObject.getMysqlConnection().registerHomeworkArchivePart2(serviceThread.getUser().getIdUser(), archivePath, Integer.parseInt(clientArguments[2])); // Guardamos relación
            }
            fileName += 2;
        }

        // REGISTRO
        //
        if (serviceThread.getUser().getIdUser() == serviceThread.getActiveVirtualClass().getIdTeacher()) {
            RecordOperator recordOperator = new RecordOperator(serviceThread, sharedObject, conversions);

            String event = "";
            if (clientArguments.length > 4) {
                event = "Subido los archivos: ";
                for (int i = 3; i < clientArguments.length; i++) {
                    event += clientArguments[i];

                    if ((i + 1) < clientArguments.length) {
                        event += ", ";
                    }
                }
            } else {
                event = "Subido el archivo: " + clientArguments[4];
            }

            recordOperator.registerRecord(event, Integer.parseInt(clientArguments[2]));
        }

        return "Ok";
    }

    public String listHomeworkStudentsFiles(String[] clientArguments) {
        LinkedList<User> responsibleStudents = sharedObject.getMysqlConnection().listHomeworkArchivesStudentsForTeacher1(Integer.parseInt(clientArguments[1]));
        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(49) + "#" + responsibleStudents.size()); // Cantidad de Alumnos que han entregado el trabajo

        for (User u : responsibleStudents) {
            LinkedList<HomeworkArchive> homeworkArchives = sharedObject.getMysqlConnection().listHomeworkArchivesStudentsForTeacher2(u.getIdUser());

            String userToClient = u.getIdUser()
                    + "#" + u.hasPhoto()
                    + "#" + u.getName()
                    + "#" + u.getEmail()
                    + "#" + u.getProvince()
                    + "#" + u.getTownship()
                    + "#" + u.getShortPresentation()
                    + "#" + u.getLongPresentation()
                    + "#" + homeworkArchives.size();

            serviceThread.sendServerResponse(userToClient); // Usuario + Cantidad de Recursos Asignados a la Unidad

            for (HomeworkArchive r : homeworkArchives) {
                String HomeworkArchivesToTeacher = r.getIdHomeworkArchive()
                        + "#" + r.getPath();

                System.out.println("CHIVATO: Archivo " + r.getPath() + " del Usuario " + u.getIdUser()); // TODO
                serviceThread.sendServerResponse(HomeworkArchivesToTeacher);
            }
        }

        return "Ok";
    }
}
