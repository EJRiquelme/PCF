/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author william
 */
public class ResourceHomework extends Resource {
    /*
    CREATE TABLE pcf_homework(
        id_homework INT PRIMARY KEY, -- FOREIGN KEY id_resource
        init_date_homework DATE NOT NULL,
        init_time_homework TIME NOT NULL,
        end_date_homework DATE NOT NULL,
        end_time_homewok TIME NOT NULL,
        percentage_homework INT NOT NULL,
        type_homework VARCHAR(50) NOT NULL -- Ejercicio / Control / Examen
    );
    ALTER TABLE pcf_homework ADD FOREIGN KEY(id_homework) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Tarea - Recurso
     */

    private Date openDate; // Se pueden introducir en la BBDD como String
    private Time openTime; // Y los recibo como String desde el Cliente
    private Date closeDate; // Se pueden introducir en la BBDD como String
    private Time closeTime; // Y los recibo como String desde el Cliente
    private int percentage;
    private String typeHomework;

    // CONSTRUCTORES

    public ResourceHomework(Resource resource, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage) {
        super(resource);
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
    }

    public ResourceHomework(int idResource, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage, String typeHomework) {
        super(idResource);
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
        this.typeHomework = typeHomework;
    }

    public ResourceHomework(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage, String typeHomework) {
        super(titleResource, presentation, type, order, hidden, idUnit);
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
        this.typeHomework = typeHomework;
    }

    public ResourceHomework(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, Date openDate, Time openTime, Date closeDate, Time closeTime, int percentage, String typeHomework) {
        super(idResource, titleResource, presentation, type, order, hidden, idUnit);
        this.openDate = openDate;
        this.openTime = openTime;
        this.closeDate = closeDate;
        this.closeTime = closeTime;
        this.percentage = percentage;
        this.typeHomework = typeHomework;
    }

    // GETTERS & SETTERS
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

    public String getTypeHomework() {
        return typeHomework;
    }

    public void setTypeHomework(String typeHomework) {
        this.typeHomework = typeHomework;
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
