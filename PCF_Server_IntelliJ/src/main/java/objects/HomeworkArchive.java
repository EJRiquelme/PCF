package objects;

public class HomeworkArchive {

    /*
    CREATE TABLE pcf_user_upload_homework_archive( --Usuario<->Tarea<->Archivo
        id_user INT,
        id_homework_archive INT,
        id_homework INT,
        score_homework INT
    );

    CREATE TABLE pcf_homework_archive( -- Del Alumno, obviamente
        id_homework_archive INT AUTO_INCREMENT PRIMARY KEY,
        path_homework_archive VARCHAR(500) NOT NULL
    );
    */
    int idHomeworkArchive;
    String path;

    // CONSTRUCTORES
    public HomeworkArchive(String path) {
        this.path = path;
    }

    public HomeworkArchive(int idHomeworkArchive, String path) {
        this.idHomeworkArchive = idHomeworkArchive;
        this.path = path;
    }

    // GETTERS && SETTERS
    public int getIdHomeworkArchive() {
        return idHomeworkArchive;
    }

    public void setIdHomeworkArchive(int idHomeworkArchive) {
        this.idHomeworkArchive = idHomeworkArchive;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
