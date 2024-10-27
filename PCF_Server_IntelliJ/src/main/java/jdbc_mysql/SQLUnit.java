/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import objects.Resource;
import objects.TestAnswer;
import objects.TestQuestion;
import objects.Unit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class SQLUnit {
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
    ALTER TABLE pcf_unit ADD UNIQUE KEY(title_unit, id_course);
     */

    private Connection connection;
    private PreparedStatement pstm;
    private String table;
    private String[] columns;
    private String primaryKey;

    public SQLUnit(Connection connection) {
        this.connection = connection;
        this.table = "pcf_unit";
        this.columns = new String[]{"id_unit", // 0
                "title_unit", // 1
                "order_unit", // 2
                "hidden_unit", // 3
                "percentage_exercises", // 4
                "percentage_controls", // 5
                "percentage_exams", // 6
                "percentage_tests", // 7
                "id_course"}; // 8
        this.primaryKey = columns[0];
    }

    public int getIdUnit(Unit unit) {
        int idUnit = -1;
        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table
                    + " WHERE " + columns[1] + " = ?" // (Nombre, idCurso) => Clave Única
                    + " AND " + columns[8] + " = ?;");

            pstm.setString(1, unit.getTitleUnit());
            pstm.setInt(2, unit.getIdCourse());

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    idUnit = rst.getInt(columns[0]);
                }
                return idUnit;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return idUnit;
    }

    public int getNewPositionUnit(int idCourse) {
        int position = 1;

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[8] + " = ?;");
            pstm.setInt(1, idCourse);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    position++;
                }
                return position;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    // RECISTRO TEMA: HECHO
    public String registerUnit(Unit unit) { //Unit(titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
        try {
            pstm = connection.prepareStatement("INSERT INTO " + table + " ("
                    + columns[1] + ", "
                    + columns[2] + ", "
                    + columns[3] + ", "
                    + columns[4] + ", "
                    + columns[5] + ", "
                    + columns[6] + ", "
                    + columns[7] + ", "
                    + columns[8] + ")" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?);");

            pstm.setString(1, unit.getTitleUnit());
            pstm.setInt(2, unit.getOrderUnit());
            pstm.setBoolean(3, unit.isHiddenUnit());
            pstm.setInt(4, unit.getPercentageExercises());
            pstm.setInt(5, unit.getPercentageControls());
            pstm.setInt(6, unit.getPercentageExams());
            pstm.setInt(7, unit.getPercentageTests());
            pstm.setInt(8, unit.getIdCourse());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public boolean reOrderUnit(int idUnit, int position) {
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET " + columns[2] + " = ? WHERE " + columns[0] + " = ?;");

            pstm.setInt(1, position);
            pstm.setInt(2, idUnit);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return false;
        }
    }

    // ACTUALIZAR TEMA
    //
    public String updateUnit(Unit unit) { // Unit(idUnit, titleUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse);
        try {
            pstm = connection.prepareStatement("UPDATE " + table + " SET "
                    + columns[1] + " = ?, "
                    // + columns[2] + " = ?, "
                    + columns[3] + " = ?, "
                    + columns[4] + " = ?, "
                    + columns[5] + " = ?, "
                    + columns[6] + " = ?, "
                    + columns[7] + " = ? WHERE " + columns[0] + " = ?;");

            pstm.setString(1, unit.getTitleUnit());
            // El orden no se altera aquí
            pstm.setBoolean(2, unit.isHiddenUnit());
            pstm.setInt(3, unit.getPercentageExercises());
            pstm.setInt(4, unit.getPercentageControls());
            pstm.setInt(5, unit.getPercentageExams());
            pstm.setInt(6, unit.getPercentageTests());
            // idCurso no es necesario
            pstm.setInt(7, unit.getIdUnit());

            pstm.executeUpdate();
            pstm.close();

            return "Ok";
        } catch (SQLIntegrityConstraintViolationException duplicate) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, duplicate);
            System.out.println("Clave primaria duplicada.");
            return "Clave";
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fallo de inserción.");
            return "Error";
        }
    }

    public boolean deleteUnit(int idUnit) {
        try {
            pstm = connection.prepareStatement("DELETE FROM " + table + " WHERE " + columns[0] + " = ?;");

            pstm.setInt(1, idUnit);

            pstm.executeUpdate();
            pstm.close();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // LISTAR TEMAS
    //
    public LinkedList<Unit> listUnitsTeacher(int idCourseVirtualClass) {
        LinkedList<Unit> units = new LinkedList();

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[8] + " = ? ORDER BY " + columns[2] + ";");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {

                    int idUnit = rst.getInt(columns[0]);
                    String titleUnit = rst.getString(columns[1]);
                    int orderUnit = rst.getInt(columns[2]);
                    boolean hiddenUnit = rst.getBoolean(columns[3]);
                    int percentageExercises = rst.getInt(columns[4]);
                    int percentageControls = rst.getInt(columns[5]);
                    int percentageExams = rst.getInt(columns[6]);
                    int percentageTests = rst.getInt(columns[7]);
                    int idCourse = rst.getInt(columns[8]);

                    units.add(new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse));
                }
                return units;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public LinkedList<Unit> listUnitsStudent(int idCourseVirtualClass) {
        LinkedList<Unit> units = new LinkedList();

        try {
            pstm = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + columns[8] + " = ? AND " + columns[3] + " = false ORDER BY " + columns[2] + ";");
            pstm.setInt(1, idCourseVirtualClass);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    int idUnit = rst.getInt(columns[0]);
                    String titleUnit = rst.getString(columns[1]);
                    int orderUnit = rst.getInt(columns[2]);
                    boolean hiddenUnit = rst.getBoolean(columns[3]);
                    int percentageExercises = rst.getInt(columns[4]);
                    int percentageControls = rst.getInt(columns[5]);
                    int percentageExams = rst.getInt(columns[6]);
                    int percentageTests = rst.getInt(columns[7]);
                    int idCourse = rst.getInt(columns[8]);

                    units.add(new Unit(idUnit, titleUnit, orderUnit, hiddenUnit, percentageExercises, percentageControls, percentageExams, percentageTests, idCourse));
                }
                return units;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
