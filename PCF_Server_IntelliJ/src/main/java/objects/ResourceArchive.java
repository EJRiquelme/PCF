/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

/**
 *
 * @author william
 */
public class ResourceArchive extends Resource {
    /*
    CREATE TABLE pcf_archive(
        id_archive INT PRIMARY KEY, -- FOREIGN KEY id_resource
        path_archive VARCHAR(500) NOT NULL
    );
    ALTER TABLE pcf_archive ADD FOREIGN KEY(id_archive) REFERENCES pcf_resource(id_resource) ON DELETE CASCADE; -- Archivo - Recurso
     */

    private String pathsArchives;

    // CONSTRUCTORES
    public ResourceArchive(int idResource, String pathsArchives) {
        super(idResource);
        this.pathsArchives = pathsArchives;
    }

    public ResourceArchive(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String pathArchive) {
        super(titleResource, presentation, type, order, hidden, idUnit);
        this.pathsArchives = pathArchive;
    }
    
    public ResourceArchive(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String pathArchive) {
        super(idResource, titleResource, presentation, type, order, hidden, idUnit);
        this.pathsArchives = pathArchive;
    }

    // GETTERS && SETTERS
    public String getPathsArchives() {
        return pathsArchives;
    }

    public void setPathsArchives(String pathsArchives) {
        this.pathsArchives = pathsArchives;
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
