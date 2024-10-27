package operators;

import objects.Record;
import objects.Resource;
import objects.ResourceLink;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class ResourceLinkOperator {
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

    private String urlLink;

    public ResourceLinkOperator(ClientServiceThread clientService, SharedObject sharedObject, Conversions conversions) {
        this.serviceThread = clientService;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String registerLink(String[] clientArguments) {
        titleResource = clientArguments[1];
        presentationResource = clientArguments[2];
        typeResource = clientArguments[3];
        orderResource = sharedObject.getMysqlConnection().getNewPositionResource(Integer.parseInt(clientArguments[4]));
        hiddenResource = Boolean.parseBoolean(clientArguments[5]);
        idUnitResource = Integer.parseInt(clientArguments[4]);

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
            return "Error: de lectura";
        }

        ResourceLink resourceLink = new ResourceLink(idResource, urlLink);
        String secondRegister = sharedObject.getMysqlConnection().registerLink(resourceLink);
        if (!secondRegister.equals("Ok")) {
            return "Error: de registro del Link";
        }
        return "Ok";
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
            return "Error: Error en la actualización del Recurso";
        }

        urlLink = clientArguments[6];
        ResourceLink resourceLink = new ResourceLink(idResource, urlLink);
        boolean secondResult = sharedObject.getMysqlConnection().updateLink(resourceLink);
        if (!secondResult) {
            return "Error: Error en la actualización del Enlace";
        }

        return "Ok";
    }

    public String consultLink(String[] clientArguments) {
        ResourceLink resourceLink = sharedObject.getMysqlConnection().selectLink(Integer.parseInt(clientArguments[1]));

        String encounteredLink = "#" + resourceLink.getUrlLink();

        // REGISTRO
        //
        if (serviceThread.getUser().getIdUser() == serviceThread.getActiveVirtualClass().getIdTeacher()) {
            RecordOperator recordOperator = new RecordOperator(serviceThread, sharedObject, conversions);
            String event = "Accedido al recurso: " + resourceLink.getTitleResource() + ".";
            recordOperator.registerRecord(event, resourceLink.getIdResource());
        }

        return encounteredLink;
    }
}
