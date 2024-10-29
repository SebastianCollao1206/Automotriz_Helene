package com.pe;

import com.pe.controller.*;
import com.pe.util.DBConnection;
import com.pe.util.JettyUTP;

public class App {
    public static void main(String[] args) {
        // Crear la conexión a la base de datos
        DBConnection dbConnection = new DBConnection();

        try {
            // Intentar establecer la conexión
            dbConnection.getConnection();
            System.out.println("¡Conexión exitosa a la base de datos!");

            // Configurar Jetty
            String path = "C:\\Users\\HP\\IdeaProjects\\Automotriz_Helene\\src\\main\\resources\\html\\admin";
            JettyUTP webserver = new JettyUTP(8081, path);

            //agregar los servlets

            webserver.addServlet(AgregarUsuarioServlet.class, "/usuario/agregar");
            webserver.addServlet(LoginServlet.class, "/login");
            webserver.addServlet(LogoutServlet.class, "/logout");
            webserver.addServlet(UsuariosServlet.class, "/usuario/listar");
            webserver.addServlet(EliminarUsuarioServlet.class, "/usuario/eliminar");
            webserver.addServlet(EditarUsuarioServlet.class, "/usuario/editar");

            webserver.start();
            System.out.println("Servidor iniciado en http://localhost:8081");

        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } finally {
            // Cerrar la conexión
            dbConnection.closeConnection();
        }
    }
}
