package objects;

public class ResourceFile extends Resource {

    /*
    CREATE TABLE pcf_archive(
	id_archive INT PRIMARY KEY, -- FOREIGN KEY id_resource
	path_archive VARCHAR(500) NOT NULL
    );
     */
    private String fileName;

    // CONSTRUCTORES
    public ResourceFile(Resource resource, String fileName) {
        super(resource);
        this.fileName = fileName;
    }

    public ResourceFile(String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String fileName) {
        super(titleResource, presentation, type, order, hidden, idUnit);
        this.fileName = fileName;
    }
/*
    public ResourceFile(int idResource, String titleResource, String presentation, String type, int order, boolean hidden, int idUnit, String fileName) {
        super(idResource, titleResource, presentation, type, order, hidden, idUnit);
        this.fileName = fileName;
    }
*/
    // GETTERS && SETTERS
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
