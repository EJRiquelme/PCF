/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

/**
 * @author william
 */
public class Resource {

    /*
    CREATE TABLE pcf_resource(
        id_resource INT AUTO_INCREMENT PRIMARY KEY,
        title_resource VARCHAR(50) NOT NULL,
        presentation_resource VARCHAR(30), -- Un Test por ejemplo no tiene porque tener enunciado
        type_resource VARCHAR(50) NOT NULL, -- Archivo / Tarea / Enlace / Test
        order_resource INT NOT NULL, -- Dentro del Tema
        hidden_resource BOOLEAN DEFAULT false,
        id_unit INT NOT NULL
    );
     */
    private int idResource;
    private String titleResource;
    private String presentation;
    private String type;
    private int order;
    private boolean hidden;
    private int idUnit;

    // CONSTRUCTORES

    public Resource() {
    }

    public Resource(int idResource) {
        this.idResource = idResource;
    }

    public Resource(String titleResource, String presentation) {
        this.titleResource = titleResource;
        this.presentation = presentation;
    }
    public Resource(String titleResource, String presentation, boolean isHidden) {
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.hidden = isHidden;
    }

    public Resource(String titleResource, int idUnit) {
        this.titleResource = titleResource;
        this.idUnit = idUnit;
    }

    public Resource(int idResource, String titleResource, String presentation, String type, boolean hidden) {
        this.idResource = idResource;
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.type = type;
        this.hidden = hidden;
    }

    public Resource(int idResource, String titleResource, String presentation, boolean hidden) {
        this.idResource = idResource;
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.hidden = hidden;
        this.idUnit = idUnit;
    }

    public Resource(int idResource, String titleResource, String presentation, boolean hidden, int idUnit) {
        this.idResource = idResource;
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.hidden = hidden;
        this.idUnit = idUnit;
    }

    public Resource(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit) {
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.type = type;
        this.order = order;
        this.hidden = hidden;
        this.idUnit = idUnit;
    }

    public Resource(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit) {
        this.idResource = idResource;
        this.titleResource = titleResource;
        this.presentation = presentation;
        this.type = type;
        this.order = order;
        this.hidden = hidden;
        this.idUnit = idUnit;
    }

    public Resource(Resource resource) { // Para los Constructores: ResourceLink, ResourceFile, ResourceHomework, ResourceTest
        this.idResource = resource.getIdResource();
        this.titleResource = resource.getTitleResource();
        this.presentation = resource.getPresentation();
        this.type = resource.getType();
        this.order = resource.getOrder();
        this.hidden = resource.isHidden();
        this.idUnit = resource.getIdUnit();
    }

    // GETTERS && SETTERS
    public int getIdResource() {
        return idResource;
    }

    public void setIdResource(int idResource) {
        this.idResource = idResource;
    }

    public String getTitleResource() {
        return titleResource;
    }

    public void setTitleResource(String titleResource) {
        this.titleResource = titleResource;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(int idUnit) {
        this.idUnit = idUnit;
    }

}
