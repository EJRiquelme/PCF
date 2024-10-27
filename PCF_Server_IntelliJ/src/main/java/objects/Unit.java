/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package objects;

/**
 *
 * @author william
 */
public class Unit {

    /*
    CREATE TABLE pcf_unit(
        id_unit INT AUTO_INCREMENT PRIMARY KEY,
        title_unit VARCHAR(50) NOT NULL,
        order_unit INT NOT NULL, -- Dentro del Curso
        hidden_unit BOOLEAN DEFAULT false,
        percentage_exercises INT DEFAULT 0,
        percentage_controls INT DEFAULT 0,
        percentage_exams INT DEFAULT 0,
        percentage_tests INT DEFAULT 0,
        id_course INT NOT NULL
    );
    ALTER TABLE pcf_unit ADD UNIQUE KEY(title_unit, id_course); -- Para evitar duplicados:  SELECT Id Tema
     */
    private int idUnit;
    private String titleUnit;
    private int orderUnit;
    private boolean hiddenUnit;
    private int percentageExercises;
    private int percentageControls;
    private int percentageExams;
    private int percentageTests;
    private int idCourse;

    // CONSTRUCTORES
    public Unit() {

    }

    public Unit(int id_unit, String title_unit, boolean hidden_unit) {
        this.idUnit = id_unit;
        this.titleUnit = title_unit;
        this.hiddenUnit = hidden_unit;
    }

    public Unit(String title_unit, int order_unit, boolean hidden_unit, int id_course) {
        this.titleUnit = title_unit;
        this.orderUnit = order_unit;
        this.hiddenUnit = hidden_unit;
        this.idCourse = id_course;
    }

    public Unit(int id_unit, String title_unit, int order_unit, boolean hidden_unit, int id_course) {
        this.idUnit = id_unit;
        this.titleUnit = title_unit;
        this.orderUnit = order_unit;
        this.hiddenUnit = hidden_unit;
        this.idCourse = id_course;
    }

    public Unit(String titleUnit, int orderUnit, boolean hiddenUnit, int percentageExercises, int percentageControls, int percentageExams, int percentageTests, int idCourse) {
        this.titleUnit = titleUnit;
        this.orderUnit = orderUnit;
        this.hiddenUnit = hiddenUnit;
        this.percentageExercises = percentageExercises;
        this.percentageControls = percentageControls;
        this.percentageExams = percentageExams;
        this.percentageTests = percentageTests;
        this.idCourse = idCourse;
    }

    public Unit(int idUnit, String titleUnit, boolean hiddenUnit, int percentageExercises, int percentageControls, int percentageExams, int percentageTests, int idCourse) {
        this.idUnit = idUnit;
        this.titleUnit = titleUnit;
        // this.orderUnit = orderUnit;
        this.hiddenUnit = hiddenUnit;
        this.percentageExercises = percentageExercises;
        this.percentageControls = percentageControls;
        this.percentageExams = percentageExams;
        this.percentageTests = percentageTests;
        this.idCourse = idCourse;
    }

    public Unit(int idUnit, String titleUnit, int orderUnit, boolean hiddenUnit, int percentageExercises, int percentageControls, int percentageExams, int percentageTests, int idCourse) {
        this.idUnit = idUnit;
        this.titleUnit = titleUnit;
        this.orderUnit = orderUnit;
        this.hiddenUnit = hiddenUnit;
        this.percentageExercises = percentageExercises;
        this.percentageControls = percentageControls;
        this.percentageExams = percentageExams;
        this.percentageTests = percentageTests;
        this.idCourse = idCourse;
    }

    // GETTERS & SETTERS
    public int getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(int idUnit) {
        this.idUnit = idUnit;
    }

    public String getTitleUnit() {
        return titleUnit;
    }

    public void setTitleUnit(String titleUnit) {
        this.titleUnit = titleUnit;
    }

    public int getOrderUnit() {
        return orderUnit;
    }

    public void setOrderUnit(int orderUnit) {
        this.orderUnit = orderUnit;
    }

    public boolean isHiddenUnit() {
        return hiddenUnit;
    }

    public void setHiddenUnit(boolean hiddenUnit) {
        this.hiddenUnit = hiddenUnit;
    }

    public int getPercentageExercises() {
        return percentageExercises;
    }

    public void setPercentageExercises(int percentageExercises) {
        this.percentageExercises = percentageExercises;
    }

    public int getPercentageControls() {
        return percentageControls;
    }

    public void setPercentageControls(int percentageControls) {
        this.percentageControls = percentageControls;
    }

    public int getPercentageExams() {
        return percentageExams;
    }

    public void setPercentageExams(int percentageExams) {
        this.percentageExams = percentageExams;
    }

    public int getPercentageTests() {
        return percentageTests;
    }

    public void setPercentageTests(int percentageTests) {
        this.percentageTests = percentageTests;
    }

    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

}
