package operators;

import objects.Record;
import objects.Resource;
import objects.ResourceArchive;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ResourceArchiveOperator {
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

    private String pathsArchives; // Archivo

    public ResourceArchiveOperator(ClientServiceThread clientService, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = clientService;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String registerArchive(String[] clientArguments) {
        titleResource = clientArguments[1];
        presentationResource = clientArguments[2];
        typeResource = clientArguments[3];
        orderResource = sharedObject.getMysqlConnection().getNewPositionResource(Integer.parseInt(clientArguments[4]));
        hiddenResource = Boolean.parseBoolean(clientArguments[5]);
        idUnitResource = Integer.parseInt(clientArguments[4]);

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
        pathsArchives = clientArguments[6];

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
        serviceThread.receiveArchive("users" + File.separator + serviceThread.getUser().getEmail() // usuario@gmail / idCurso / idTema / idRecurso / nombreArchivo
                + File.separator + serviceThread.getActiveVirtualClass().getIdCourse() // irCurso
                + File.separator + idUnitResource // idTema
                + File.separator + idResource // idRecurso
                + File.separator + clientArguments[8]); // nombreArchivo

        return "A";
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

    public String consultArchive(String[] clientArguments){
        ResourceArchive resourceArchive = sharedObject.getMysqlConnection().selectArchive(Integer.parseInt(clientArguments[1]));

        // REGISTRO
        //
        if (serviceThread.getUser().getIdUser() == serviceThread.getActiveVirtualClass().getIdTeacher()) {
            RecordOperator recordOperator = new RecordOperator(serviceThread, sharedObject, conversions);
            String event = "Accedido al recurso: " + resourceArchive.getTitleResource() + ".";
            recordOperator.registerRecord(event, resourceArchive.getIdResource());
        }

        File file = new File(resourceArchive.getPathsArchives());
        return file.getName();
    }
}
