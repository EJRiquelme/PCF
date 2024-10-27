/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jdbc_mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author william
 */
public class SQLResourceFile {
    /*
    CREATE TABLE pcf_archive(
        id_archive INT PRIMARY KEY, -- FOREIGN KEY id_resource
        path_archive VARCHAR(500) NOT NULL
    );
     */

    private Connection connection;
    private PreparedStatement pstm;
    private String fileTable;
    private String[] fileTableColumns;

    public SQLResourceFile(Connection connection) {
        this.connection = connection;
        this.fileTable = "pcf_archive";
        this.fileTableColumns = new String[]{"id_archive", // 0
                "path_archive"}; // 1
    }

    public String getFilePath(int idFile) {
        String filePath = "noFile";
        try {
            pstm = connection.prepareStatement("SELECT " + fileTableColumns[1] +
                    " FROM " + fileTable +
                    " WHERE " + fileTableColumns[0] + " = ?;");

            pstm.setInt(1, idFile);

            try (ResultSet rst = pstm.executeQuery()) {
                while (rst.next()) {
                    filePath = rst.getString(fileTableColumns[1]);
                }
                return filePath;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PreparedConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filePath;
    }
}
