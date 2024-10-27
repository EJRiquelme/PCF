package objects;

import java.time.LocalDate;

public class Course {

    /*
    CREATE TABLE pcf_course(
        id_course INT AUTO_INCREMENT PRIMARY KEY,
        name_course VARCHAR(50) NOT NULL,
        short_presentation_course VARCHAR(500) NOT NULL,
        long_presentation_course VARCHAR(1000), -- Opcional
        start_date_course DATE NOT NULL,
        end_date_course DATE NOT NULL, -- Para el acceso al Aula Virtual
        hidden_course BOOLEAN DEFAULT true, -- Para el Motor de Búsquedas
        closed_course BOOLEAN DEFAULT false, -- Da acceso al Aula Virtual de forma prematura y da por finalizado el curso: Debe ser ejecutado manualmente por el profesor
        id_teacher_user INT NOT NULL
    );
    ALTER TABLE pcf_course ADD UNIQUE KEY(name_course, id_teacher_user); -- Para evitar duplicados: Seguridad en el registro
     */
    private int idCourse;
    private String name;
    private String shortPresentation;
    private String longPresentation;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean hidden;
    private boolean closed;
    private int idTeacher;

    // CONSTRUCTOR
    public Course() {
        this.name = "prueba";
    }

    public Course(String name, String shortPresentation, String longPresentation, LocalDate startDate, LocalDate endDate, int idTeacher) {
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.idTeacher = idTeacher;
    }

    public Course(String name, String shortPresentation, String longPresentation, LocalDate startDate, LocalDate endDate, boolean hidden, int idTeacher) {
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hidden = hidden;
        this.idTeacher = idTeacher;
    }

    public Course(int idCourse, String name, String shortPresentation, String longPresentation, LocalDate startDate, LocalDate endDate, boolean hidden, int idTeacher) {
        this.idCourse = idCourse;
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hidden = hidden;
        this.idTeacher = idTeacher;
    }

    public Course(int idCourse, String name, String shortPresentation, String longPresentation, LocalDate startDate, LocalDate endDate, boolean hidden, boolean closed, int idTeacher) {
        this.idCourse = idCourse;
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hidden = hidden;
        this.closed = closed;
        this.idTeacher = idTeacher;
    }

    // GETTERS & SETTERS
    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortPresentation() {
        return shortPresentation;
    }

    public void setShortPresentation(String shortPresentation) {
        this.shortPresentation = shortPresentation;
    }

    public String getLongPresentation() {
        return longPresentation;
    }

    public void setLongPresentation(String longPresentation) {
        this.longPresentation = longPresentation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(int idTeacher) {
        this.idTeacher = idTeacher;
    }

}
