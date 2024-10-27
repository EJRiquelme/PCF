package objects;

public class CourseTag {
    /*
    CREATE TABLE pcf_course_tag( -- Para el Motor de Busqueda
        id_tag INT AUTO_INCREMENT PRIMARY KEY,
        word_tag VARCHAR(50) UNIQUE NOT NULL
    );
    */
    private int idTag;
    private String wordTag;

    public CourseTag(String wordTag) { // INSERT
        this.wordTag = wordTag;
    }

    public CourseTag(int idTag, String wordTag) { // UPDATE
        this.idTag = idTag;
        this.wordTag = wordTag;
    }

    public int getIdTag() {
        return idTag;
    }

    public void setIdTag(int idTag) {
        this.idTag = idTag;
    }

    public String getWordTag() {
        return wordTag;
    }

    public void setWordTag(String wordTag) {
        this.wordTag = wordTag;
    }
}
