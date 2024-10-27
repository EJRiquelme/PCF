package objects;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ResourceTest extends Resource {
    /*
    CREATE TABLE pcf_test(
	    id_test INT PRIMARY KEY, -- FOREIGN KEY id_resource
	    questions_quantity INT NOT NULL,
	    init_date_test DATE NOT NULL,
        init_time_test TIME NOT NULL,
        end_date_test DATE NOT NULL,
        end_time_test TIME NOT NULL,
	    percentage_test INT NOT NULL
    );
    ALTER TABLE pcf_test ADD FOREIGN KEY(id_test) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Test - Recurso
    */

    private int questionsQuantity;
    private LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test;
    private Date openDate;
    private Time openTime;
    private Date closeDate;
    private Time closeTime;
    private int percentage;

    public ResourceTest(String titleResource, String presentationResource, boolean isHidden, int questionsQuantity, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage) {
        super(titleResource, presentationResource, isHidden);
        this.questionsQuantity = questionsQuantity;
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
    }

    public ResourceTest(String titleResource, String presentationResource, int questionsQuantity, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage) {
        super(titleResource, presentationResource);
        this.questionsQuantity = questionsQuantity;
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
    }

    public ResourceTest(Resource resource, int questionsQuantity, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage) {
        super(resource);
        this.questionsQuantity = questionsQuantity;
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
    }

    public ResourceTest(Resource resource, int questionsQuantity, LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage) {
        super(resource);
        this.questionsQuantity = questionsQuantity;
        this.test = test;
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
    }

    public int getQuestionsQuantity() {
        return questionsQuantity;
    }

    public void setQuestionsQuantity(int questionsQuantity) {
        this.questionsQuantity = questionsQuantity;
    }

    public LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> getTest() {
        return test;
    }

    public void setTest(LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test) {
        this.test = test;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Time getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Time openTime) {
        this.openTime = openTime;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public Time getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Time closeTime) {
        this.closeTime = closeTime;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
