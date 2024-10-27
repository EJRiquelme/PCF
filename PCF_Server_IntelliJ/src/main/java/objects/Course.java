/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

import java.sql.Date;

/**
 *
 * @author william
 */
public class Course {

    /*
    CREATE TABLE pcf_course(
        id_course INT AUTO_INCREMENT PRIMARY KEY,
        name_course VARCHAR(50) NOT NULL,
        short_presentation_course VARCHAR(500) NOT NULL,
        long_presentation_course VARCHAR(1000), -- Opcional
        start_date_course DATE NOT NULL,
        end_date_course DATE NOT NULL,
        hidden_course BOOLEAN DEFAULT true,
        closed_course BOOLEAN DEFAULT true,
        id_teacher_user INT NOT NULL
    );
    ALTER TABLE pcf_course ADD UNIQUE KEY(name_course, id_teacher_user); -- Para evitar duplicados: Seguridad en el registro
     */
    private int idCourse;
    private String name;
    private String shortPresentation;
    private String longPresentation;
    private Date startDate;
    private Date endDate;
    private boolean hidden;
    private boolean closed;
    private int idTeacher;

    // CONSTRUCTOR
    public Course() {
        this.name = "";
    }

    public Course(String name, int idTeacher) {
        this.name = name;
        this.idTeacher = idTeacher;
    }

    public Course(String name, String shortPresentation, String longPresentation, Date startDate, Date endDate, int idTeacher) {
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.idTeacher = idTeacher;
    }

    public Course(int idCourse, String name, String shortPresentation, String longPresentation, Date startDate, Date endDate, boolean hidden, int idTeacher) {
        this.idCourse = idCourse;
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hidden = hidden;
        this.idTeacher = idTeacher;
    }

    public Course(String name, String shortPresentation, String longPresentation, Date startDate, Date endDate, boolean hidden, boolean closed, int idTeacher) {
        this.name = name;
        this.shortPresentation = shortPresentation;
        this.longPresentation = longPresentation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hidden = hidden;
        this.closed = closed;
        this.idTeacher = idTeacher;
    }

    public Course(int idCourse, String name, String shortPresentation, String longPresentation, Date startDate, Date endDate, boolean hidden, boolean closed, int idTeacher) {
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
