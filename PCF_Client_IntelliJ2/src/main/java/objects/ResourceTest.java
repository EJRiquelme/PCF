package objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
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
    private LocalDate openDate;
    private LocalTime openTime;
    private LocalDate closeDate;
    private LocalTime closeTime;
    private int percentage;

    public ResourceTest(Resource resource, int questionsQuantity, LinkedHashMap<TestQuestion, LinkedHashSet<TestAnswer>> test, LocalDate openDate, LocalTime openTime, LocalDate closeDate, LocalTime closeTime, int percentage) {
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

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
