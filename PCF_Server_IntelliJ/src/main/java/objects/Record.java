package objects;

import java.sql.Date;
import java.sql.Time;

public class Record {
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
    int idRecord;
    Date dateRecord;
    Time timeRecord;
    String event;
    int idStudent;
    int idResource;

    public Record(Date dateRecord, Time timeRecord, String event, int idStudent, int idResource) {
        this.dateRecord = dateRecord;
        this.timeRecord = timeRecord;
        this.event = event;
        this.idStudent = idStudent;
        this.idResource = idResource;
    }

    public Record(int idRecord, Date dateRecord, Time timeRecord, String event, int idStudent, int idResource) {
        this.idRecord = idRecord;
        this.dateRecord = dateRecord;
        this.timeRecord = timeRecord;
        this.event = event;
        this.idStudent = idStudent;
        this.idResource = idResource;
    }

    public int getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(int idRecord) {
        this.idRecord = idRecord;
    }

    public Date getDateRecord() {
        return dateRecord;
    }

    public void setDateRecord(Date dateRecord) {
        this.dateRecord = dateRecord;
    }

    public Time getTimeRecord() {
        return timeRecord;
    }

    public void setTimeRecord(Time timeRecord) {
        this.timeRecord = timeRecord;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public int getIdResource() {
        return idResource;
    }

    public void setIdResource(int idResource) {
        this.idResource = idResource;
    }
}
