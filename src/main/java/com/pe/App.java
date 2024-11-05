package com.pe;

import com.pe.controller.administrador.LoginServlet;
import com.pe.controller.administrador.LogoutServlet;
import com.pe.controller.administrador.productos.ProductosServlet;
import com.pe.controller.administrador.categorias.AgregarCategoriaServlet;
import com.pe.controller.administrador.categorias.CategoriasServlet;
import com.pe.controller.administrador.categorias.EditarCategoriaServlet;
import com.pe.controller.administrador.categorias.EliminarCategoriaServlet;
import com.pe.controller.administrador.productos.AgregarProductoServlet;
import com.pe.controller.administrador.productos.BuscarProductoServlet;
import com.pe.controller.administrador.productos.EditarProductoServlet;
import com.pe.controller.administrador.tamanios.AgregarTamanioServlet;
import com.pe.controller.administrador.tamanios.EditarTamanioServlet;
import com.pe.controller.administrador.tamanios.EliminarTamanioServlet;
import com.pe.controller.administrador.tamanios.TamaniosServlet;
import com.pe.controller.administrador.usuarios.AgregarUsuarioServlet;
import com.pe.controller.administrador.usuarios.EditarUsuarioServlet;
import com.pe.controller.administrador.usuarios.EliminarUsuarioServlet;
import com.pe.controller.administrador.usuarios.UsuariosServlet;
import com.pe.controller.administrador.variantes.ActualizarStockServlet;
import com.pe.controller.administrador.variantes.AgregarVarianteServlet;
import com.pe.controller.administrador.variantes.EditarVarianteServlet;
import com.pe.controller.administrador.variantes.VarianteProductoServlet;
import com.pe.util.DBConnection;
import com.pe.util.JettyUTP;

public class App {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        try {
            dbConnection.getConnection();
            System.out.println("¡Conexión exitosa a la base de datos!");

            // Configurar Jetty
            String path = "C:\\Users\\HP\\IdeaProjects\\Automotriz_Helene\\src\\main\\resources\\html";
            JettyUTP webserver = new JettyUTP(8081, path);

            //agregar los servlets del admin
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
            webserver.addServlet(AgregarVarianteServlet.class, "/variante/agregar");
            webserver.addServlet(BuscarProductoServlet.class, "/buscarProducto");
            webserver.addServlet(EditarVarianteServlet.class, "/variante/editar");
            webserver.addServlet(EditarProductoServlet.class, "/producto/editar");

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
