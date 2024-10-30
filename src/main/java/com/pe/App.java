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
            webserver.addServlet(LoginServlet.class, "/login");
            webserver.addServlet(LogoutServlet.class, "/logout");

            webserver.addServlet(AgregarUsuarioServlet.class, "/usuario/agregar");
            webserver.addServlet(UsuariosServlet.class, "/usuario/listar");
            webserver.addServlet(EliminarUsuarioServlet.class, "/usuario/eliminar");
            webserver.addServlet(EditarUsuarioServlet.class, "/usuario/editar");

            webserver.addServlet(AgregarCategoriaServlet.class, "/categoria/agregar");
            webserver.addServlet(CategoriasServlet.class, "/categoria/listar");
            webserver.addServlet(EliminarCategoriaServlet.class, "/categoria/eliminar");
            webserver.addServlet(EditarCategoriaServlet.class, "/categoria/editar");

            webserver.addServlet(AgregarTamanioServlet.class, "/tamanio/agregar");
            webserver.addServlet(TamaniosServlet.class, "/tamanio/listar");
            webserver.addServlet(EliminarTamanioServlet.class, "/tamanio/eliminar");
            webserver.addServlet(EditarTamanioServlet.class, "/tamanio/editar");

            webserver.addServlet(AgregarProductoServlet.class, "/producto/agregar");
            webserver.addServlet(ProductosServlet.class, "/producto/listar");
            webserver.addServlet(VarianteProductoServlet.class, "/variante/producto");
            webserver.addServlet(ActualizarStockServlet.class, "/variante/actualizarStock");

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
