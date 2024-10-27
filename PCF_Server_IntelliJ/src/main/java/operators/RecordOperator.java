package operators;

import objects.Record;
import objects.User;
import pcf_server.ClientServiceThread;
import pcf_server.SharedObject;
import tools.Conversions;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;

public class RecordOperator {
    private final ClientServiceThread serviceThread;
    private final SharedObject sharedObject;
    private Conversions conversions;

    public RecordOperator(ClientServiceThread serviceThread, SharedObject sharedObject, Conversions conversions){
        this.serviceThread = serviceThread;
        this.sharedObject = sharedObject;
        this.conversions = conversions;
    }

    public String listRecords(String[] clientArguments){
        LinkedList<User> users = new LinkedList<>();

        for (User u : users) {
            LinkedList<Record> records = sharedObject.getMysqlConnection().listStudentRecord(u.getIdUser());

            String userToClient = u.getIdUser()
                    + "#" + u.hasPhoto()
                    + "#" + u.getName()
                    + "#" + u.getEmail()
                    + "#" + u.getProvince()
                    + "#" + u.getTownship()
                    + "#" + u.getShortPresentation()
                    + "#" + u.getLongPresentation()
                    + "#" + records.size();

            serviceThread.sendServerResponse(userToClient); // Usuario + Cantidad de Recursos Asignados a la Unidad

            for (Record r : records) {
                String recordsToTeacher = r.getIdRecord()
                        + "#" + conversions.convertDateToString(r.getDateRecord())
                        + "#" + conversions.convertTimeToString(r.getTimeRecord())
                        + "#" + r.getEvent()
                        + "#" + r.getIdStudent()
                        + "#" + r.getIdResource();

                // System.out.println("CHIVATO: " + u.getIdUser());
                serviceThread.sendServerResponse(recordsToTeacher);
            }
        }

        return "Ok";
    }

    public String registerRecord(String event, int idResource){
        Date currentDate = Date.valueOf(LocalDate.now());
        Time currentTime = Time.valueOf(LocalTime.now());

        if (serviceThread.getUser().getIdUser() != serviceThread.getActiveVirtualClass().getIdTeacher()) {
            sharedObject.getMysqlConnection().registerRecord(new Record(currentDate, currentTime, event, serviceThread.getUser().getIdUser(), idResource));
        }

        return "Ok";
    }
}
