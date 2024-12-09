package com.pe.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/mecanica_helene";

    //private static final String URL = "jdbc:mysql://129.148.23.240:3306/mecanica_helene";
    private static final String USER = "root";
    private static final String PASS = "";
    private Connection connection;

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASS);
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: MySQL Driver no encontrado " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
