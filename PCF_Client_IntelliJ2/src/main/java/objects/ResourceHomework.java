package objects;

import java.time.LocalDate;
import java.time.LocalTime;

public class ResourceHomework extends Resource {

    /*
    CREATE TABLE pcf_homework(
	id_homework INT PRIMARY KEY, -- FOREIGN KEY id_resource
	init_date_homework DATE NOT NULL,
	init_time_homework TIME NOT NULL,
	end_date_homework DATE NOT NULL,
	end_time_homewok TIME NOT NULL,
	percentage_homework INT NOT NULL,
	type_homework VARCHAR(50) NOT NULL -- Ejercicio / Control / Examen  <------------------------------------------------ CAMBIAR
    );
     */
    private LocalDate openDate; // Se pueden introducir en la BBDD como String
    private LocalTime openTime; // Y los recibo como String desde el Cliente
    private LocalDate closeDate; // Se pueden introducir en la BBDD como String
    private LocalTime closeTime; // Y los recibo como String desde el Cliente
    private int percentage;

    // CONSTRUCTORES
    public ResourceHomework(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, LocalDate initDate, LocalTime initTime, LocalDate endDate, LocalTime endTime, int percentage) {
        super(titleResource, presentation, type, order, hidden, idUnit);
        this.openDate = initDate;
        this.openTime = initTime;
        this.closeDate = endDate;
        this.closeTime = endTime;
        this.percentage = percentage;
    }
/*
    public ResourceHomework(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, LocalDate initDate, LocalTime initTime, LocalDate endDate, LocalTime endTime, int percentage) {
        super(idResource, titleResource, presentation, type, order, hidden, idUnit);
        this.initDate = initDate;
        this.initTime = initTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.percentage = percentage;
    }
*/
    public ResourceHomework(Resource resource, LocalDate initDate, LocalTime initTime, LocalDate endDate, LocalTime endTime, int percentage) {
        super(resource);
        this.openDate = initDate;
        this.openTime = initTime;
        this.closeDate = endDate;
        this.closeTime = endTime;
        this.percentage = percentage;
    }

    // GETTERS & SETTERS
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

    @Override
    public void setIdUnit(int idUnit) {
        super.setIdUnit(idUnit);
    }

    @Override
    public int getIdUnit() {
        return super.getIdUnit();
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
    }

    @Override
    public boolean isHidden() {
        return super.isHidden();
    }

    @Override
    public void setOrder(int order) {
        super.setOrder(order);
    }

    @Override
    public int getOrder() {
        return super.getOrder();
    }

    @Override
    public void setType(String type) {
        super.setType(type);
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public void setPresentation(String presentation) {
        super.setPresentation(presentation);
    }

    @Override
    public String getPresentation() {
        return super.getPresentation();
    }

    @Override
    public void setTitleResource(String titleResource) {
        super.setTitleResource(titleResource);
    }

    @Override
    public String getTitleResource() {
        return super.getTitleResource();
    }

    @Override
    public void setIdResource(int idResource) {
        super.setIdResource(idResource);
    }

    @Override
    public int getIdResource() {
        return super.getIdResource();
    }

}
