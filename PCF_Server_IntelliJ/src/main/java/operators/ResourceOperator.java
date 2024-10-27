package operators;

import objects.*;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ResourceOperator {
    private final ClientServiceThread serviceThread;
    private final SharedObject sharedObject;
    private Conversions conversions;

    private int idResource; // Base
    private String titleResource;
    private String presentationResource;
    private String typeResource;
    private int orderResource;
    private boolean hiddenResource;
    private int idUnitResource;

    private String urlLink; // Enlace

    private String pathsArchives; // Archivo

    private Date initDate; // Tarea
    private Time initTime;
    private Date endDate;
    private Time endTime;
    private int percentage;
    private String typeHomework;

    public ResourceOperator(ClientServiceThread clientService, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = clientService;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String reorderResource(String[] clientArguments) {

        for (int i = 1; i < clientArguments.length; i++) {
            if (i % 2 != 0) { // Id - Pos
                boolean reorderResult = sharedObject.getMysqlConnection().reOrderResources(Integer.parseInt(clientArguments[i]), Integer.parseInt(clientArguments[i + 1]));

                if (!reorderResult) {
                    return "Error: Error de actualización";
                }
            }
        }

        return "Ok";
    }


    public String registerLink(String[] clientArguments) {
        titleResource = clientArguments[1];
        presentationResource = clientArguments[2];
        typeResource = clientArguments[3];
        orderResource = sharedObject.getMysqlConnection().getNewPositionResource(Integer.parseInt(clientArguments[5]));
        hiddenResource = Boolean.parseBoolean(clientArguments[4]);
        idUnitResource = Integer.parseInt(clientArguments[5]);

        Resource resource = new Resource(titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
        String firstRegister = sharedObject.getMysqlConnection().registerResource(resource);

        if (firstRegister.equals("Clave")) {
            return "Error: Clave duplicada";
        } else if (firstRegister.equals("Error")) {
            return "Error: Error de registro del Recurso";
        }

        idResource = sharedObject.getMysqlConnection().getIdNewResource(resource);
        urlLink = clientArguments[6];
        if (idResource == 0) {
            return "Error: Error de lectura";
        }

        ResourceLink resourceLink = new ResourceLink(idResource, urlLink);
        String secondRegister = sharedObject.getMysqlConnection().registerLink(resourceLink);
        if (!secondRegister.equals("Ok")) {
            return "Error: Error de registro del Link";
        }
        return "Ok";
    }

    // ALTA ARCHIVOS -> 0.C28, 1.tituloRecurso, 2.presentación, 3.tipoRecurso, 4.oculto/no, 5.idUnit, 6.nombreArchivo
    //
    public String registerArchive(String[] clientArguments) {
        titleResource = clientArguments[1];
        presentationResource = clientArguments[2];
        typeResource = clientArguments[3];
        orderResource = sharedObject.getMysqlConnection().getNewPositionResource(Integer.parseInt(clientArguments[5]));
        hiddenResource = Boolean.parseBoolean(clientArguments[4]);
        idUnitResource = Integer.parseInt(clientArguments[5]);

        Resource resource = new Resource(titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
        String firstRegister = sharedObject.getMysqlConnection().registerResource(resource);

        if (!firstRegister.equals("Ok")) {
            if (firstRegister.equals("Clave")) {
                return "E";
            } else {
                return "D";
            }
        }

        idResource = sharedObject.getMysqlConnection().getIdNewResource(resource);
        pathsArchives = "users" + File.separator + // users / emailProfesor / idCurso / idTema / idRecurso
                serviceThread.getUser().getEmail() + File.separator +
                serviceThread.getActiveVirtualClass().getIdCourse() + File.separator +
                idUnitResource + File.separator +
                idResource;

        File directory = new File(pathsArchives); // Genera el directorio para el archivo
        directory.mkdirs();

        pathsArchives += File.separator + clientArguments[6]; // path / nombreArchivo

        if (idResource == 0) {
            return "C";
        }

        ResourceArchive resourceArchive = new ResourceArchive(idResource, pathsArchives);
        String secondRegister = sharedObject.getMysqlConnection().registerArchive(resourceArchive);
        if (!secondRegister.equals("Ok")) {
            return "B";
        }

        // Se hace el traspaso del archivo si no ha habido ningún problema
        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(30));
        serviceThread.receiveArchive(pathsArchives); // nombreArchivo

        return "A";
    }

    public String updateLink(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);
        titleResource = clientArguments[2];
        presentationResource = clientArguments[3];
        hiddenResource = Boolean.parseBoolean(clientArguments[4]);
        idUnitResource = Integer.parseInt(clientArguments[5]);

        Resource resource = new Resource(idResource, titleResource, presentationResource, hiddenResource, idUnitResource);
        boolean firstResult = sharedObject.getMysqlConnection().updateResource(resource);
        if (!firstResult) {
            return "Error: Error de actualización del recurso";
        }

        urlLink = clientArguments[6];
        ResourceLink resourceLink = new ResourceLink(idResource, urlLink);
        boolean secondResult = sharedObject.getMysqlConnection().updateLink(resourceLink);
        if (!secondResult) {
            return "Error: Error de actualizacion del enlace";
        }

        return "Ok";
    }

    public String updateArchive(String[] clientArguments) { // 0.C31, 1.idRecurso, 2.tituloRecurso, 3.presentación, 4.oculto/no, 5.nombreArchivo/noChanges, 6.antiguoNombre
        idResource = Integer.parseInt(clientArguments[1]);
        titleResource = clientArguments[2];
        presentationResource = clientArguments[3];
        hiddenResource = Boolean.parseBoolean(clientArguments[4]);
        idUnitResource = Integer.parseInt(clientArguments[5]);
        pathsArchives = clientArguments[6];

        Resource resource = new Resource(idResource, titleResource, presentationResource,"file", hiddenResource);
        boolean firstResult = sharedObject.getMysqlConnection().updateResource(resource);
        if (!firstResult) {
            return "C";
        }

        if (!pathsArchives.equals("noChanges")) {
            serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(35));

            File oldArchive = new File("users" + File.separator + serviceThread.getUser().getEmail() // usuario@gmail / idCurso / idTema / idRecurso / nombreAntiguoArchivo
                    + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // idCurso
                    + File.separator + idUnitResource // idTema
                    + File.separator + idResource // idRecurso
                    + File.separator + clientArguments[7]); // antiguoArchivo
            sharedObject.deleteFile(oldArchive);

            serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(35));
            serviceThread.receiveArchive("users" + File.separator + serviceThread.getUser().getEmail() // usuario@gmail / idCurso / idTema / idRecurso / nombreNuevoArchivo
                    + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // irCurso
                    + File.separator + idUnitResource // idTema
                    + File.separator + idResource // idRecurso
                    + File.separator + pathsArchives); // nombreArchivo

            ResourceArchive resourceArchive = new ResourceArchive(idResource, pathsArchives);
            boolean secondResult = sharedObject.getMysqlConnection().updateArchive(resourceArchive);
            if (!secondResult) {
                return "B";
            }
        }
        return "#" + "Ok";
    }

    public String updateHomework(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);
        titleResource = clientArguments[1];
        presentationResource = clientArguments[2];
        typeResource = clientArguments[3];
        orderResource = Integer.parseInt(clientArguments[4]);
        hiddenResource = Boolean.parseBoolean(clientArguments[5]);
        idUnitResource = Integer.parseInt(clientArguments[6]);

        Resource resource = new Resource(titleResource, presentationResource, typeResource, orderResource, hiddenResource, idUnitResource);
        boolean firstRegister = sharedObject.getMysqlConnection().updateResource(resource);
        if (!firstRegister) {
            return "C";
        }

        initDate = conversions.convertStringToDate(clientArguments[7]);
        initTime = conversions.convertStringToTime(clientArguments[8]);
        endDate = conversions.convertStringToDate(clientArguments[9]);
        endTime = conversions.convertStringToTime(clientArguments[10]);
        percentage = Integer.parseInt(clientArguments[11]);
        typeHomework = clientArguments[12];

        ResourceHomework resourceHomework = new ResourceHomework(idResource, initDate, initTime, endDate, endTime, percentage, typeHomework);
        boolean secondRegister = sharedObject.getMysqlConnection().updateHomework(resourceHomework);
        if (!secondRegister) {
            return "B";
        }
        return "A";
    }

    public String updateHomeworkPercentage(String[] clientArguments) { // UPDATE HOMEWORK PERCENTAGE ->
        // 0.C¿?, 1.Id Unit, 2.Titulo, 3.Posicion, 4.Oculto/No, 5.Porcenteja, 6.Id Curso, ¿7.IdTarea, ¿8.Porcentaje, ¿9.IdTarea, ¿10.Porcentaje...

        for (int i = 7; i < clientArguments.length; i++) {
            if (i % 2 != 0) {
                idResource = Integer.parseInt(clientArguments[i]);
                percentage = Integer.parseInt(clientArguments[i + 1]);
                boolean firstResult = sharedObject.getMysqlConnection().updateHomeworkPercentage(idResource, percentage);
                if (!firstResult) {
                    return "Error: Error de actualización de la nota";
                }
            }
        }
        return "Ok";
    }

    // C41 # material/tarea # idRecurso # nombreArchivo
    //
    public String downloadFile(String[] clientArguments) {
        serviceThread.sendServerResponse(sharedObject.getProtocolMessages().getServerMessage(41) + "#" + clientArguments[3]);

        int idResourceOrIdFileHomework = Integer.parseInt(clientArguments[2]);

        if(clientArguments[1].equals("file")) {
            pathsArchives = sharedObject.getMysqlConnection().getFilePath(idResourceOrIdFileHomework);
        } else {
            pathsArchives = sharedObject.getMysqlConnection().getPathArchiveHomework(idResourceOrIdFileHomework);
        }

        if (pathsArchives.equals("noFile")) {
            return "#" + "Error: Archivo inexistente.";
        }

        serviceThread.sendArchive(pathsArchives);

        // REGISTRO
        //
        Date currentDate = Date.valueOf(LocalDate.now());
        Time currentTime = Time.valueOf(LocalTime.now());

        if (serviceThread.getUser().getIdUser() != serviceThread.getActiveVirtualClass().getIdTeacher()) {
            sharedObject.getMysqlConnection().registerRecord(new Record(currentDate, currentTime,
                    "Descargado el archivo " + clientArguments[3] + ".",
                    serviceThread.getUser().getIdUser(), Integer.parseInt(clientArguments[2])));
        }

        return "#" + "Ok";
    }

    public String deleteResource(String[] clientArguments) {
        idResource = Integer.parseInt(clientArguments[1]);
        typeResource = clientArguments[2];

        if (typeResource.equals("exercise")
                || typeResource.equals("control")
                || typeResource.equals("exam")
                || typeResource.equals("file")) { // Si es archivo o tarea borro su carpeta, junto a su contenido
            File resourceDirectory = new File("users" + File.separator + serviceThread.getUser().getEmail() // usuario@gmail / idCurso / idTema / idRecurso / nombreAntiguoArchivo
                    + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // idCurso
                    + File.separator + clientArguments[3] // idTema
                    + File.separator + idResource); // idRecurso
            sharedObject.deleteDirectory(resourceDirectory);

        } else if (typeResource.equals("test")) {
            if (sharedObject.getMysqlConnection().existsSolvedTest(idResource, serviceThread.getUser().getIdUser())) {
                sharedObject.getMysqlConnection().deleteSolvedTestByOriginalTest(idResource);
            }
        }
        boolean firstResult = sharedObject.getMysqlConnection().deleteResource(idResource); // Borro el registro de la BBDD


        if (!firstResult) {
            return "Error: Error de borrado";
        }

        return "Ok";
    }
}
