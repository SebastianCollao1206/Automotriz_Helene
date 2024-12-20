package com.pe;

import com.pe.controller.administrador.LoginServlet;
import com.pe.controller.administrador.LogoutServlet;
import com.pe.controller.administrador.Slider.*;
import com.pe.controller.administrador.categorias.*;
import com.pe.controller.administrador.comentarios.ComentariosServlet;
import com.pe.controller.administrador.comentarios.EstadoComentarioServlet;
import com.pe.controller.administrador.notificaciones.MarcarNotificacionesServlet;
import com.pe.controller.administrador.productos.ProductosServlet;
import com.pe.controller.administrador.productos.AgregarProductoServlet;
import com.pe.controller.administrador.productos.BuscarProductoServlet;
import com.pe.controller.administrador.productos.EditarProductoServlet;
import com.pe.controller.administrador.reportes.ReportesServlet;
import com.pe.controller.administrador.reportes.TopCategoriasServlet;
import com.pe.controller.administrador.reportes.TopProductosServlet;
import com.pe.controller.administrador.reportes.VentaMesServlet;
import com.pe.controller.administrador.tamanios.AgregarTamanioServlet;
import com.pe.controller.administrador.tamanios.EditarTamanioServlet;
import com.pe.controller.administrador.tamanios.EliminarTamanioServlet;
import com.pe.controller.administrador.tamanios.TamaniosServlet;
import com.pe.controller.administrador.usuarios.*;
import com.pe.controller.administrador.usuarios.recuperarContraseña.CambiarContrasenaServlet;
import com.pe.controller.administrador.usuarios.recuperarContraseña.RecuperarContrasenaServlet;
import com.pe.controller.administrador.usuarios.recuperarContraseña.VerificarCodigoServlet;
import com.pe.controller.administrador.variantes.*;
import com.pe.controller.cliente.*;
import com.pe.util.DBConnection;
import com.pe.util.JettyUTP;

public class App {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        try {
            dbConnection.getConnection();
            System.out.println("¡Conexión exitosa a la base de datos!");

            // Configurar Jetty
            //String path = System.getProperty("user.dir") + "\\src\\main\\resources\\html";
            String path = "src/main/resources/html";
            JettyUTP webserver = new JettyUTP(8080, path);

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
            webserver.addServlet(ExportarUsuarioExcelServlet.class, "/usuario/exportar-excel");
            webserver.addServlet(ComentariosServlet.class, "/comentario/listar");
            webserver.addServlet(EstadoComentarioServlet.class, "/comentario/estado");
            webserver.addServlet(AgregarSliderServlet.class, "/slider/agregar");
            webserver.addServlet(BuscarCategoriaServlet.class, "/buscarCategoria");
            webserver.addServlet(BuscarVarianteServlet.class, "/buscarVariante");
            webserver.addServlet(BuscarProductServlet.class, "/buscarProduct");
            webserver.addServlet(SlidersServlet.class, "/slider/listar");
            webserver.addServlet(EliminarSliderServlet.class, "/slider/eliminar");
            webserver.addServlet(EditarSliderServlet.class, "/slider/editar");
            webserver.addServlet(ReportesServlet.class, "/reportes/pagina");
            webserver.addServlet(MarcarNotificacionesServlet.class, "/marcar-notificaciones");

            //graficos
            webserver.addServlet(TopProductosServlet.class, "/productos-mas-vendidos");
            webserver.addServlet(TopCategoriasServlet.class, "/top-categorias-vendidas");
            webserver.addServlet(VentaMesServlet.class, "/ventas-por-mes");

            //servlet de recuperacion de contraseña
            webserver.addServlet(RecuperarContrasenaServlet.class, "/recuperar/solicitar");
            webserver.addServlet(VerificarCodigoServlet.class, "/recuperar/verificar");
            webserver.addServlet(CambiarContrasenaServlet.class, "/recuperar/cambiar");

            //servlets de cliente
            webserver.addServlet(LoginClienteServlet.class, "/cliente/login");
            webserver.addServlet(IndexClienteServlet.class, "/cliente/");
            webserver.addServlet(RegistroClienteServlet.class, "/cliente/registro");
            webserver.addServlet(LogoutClienteServlet.class, "/cliente/logout");
            webserver.addServlet(ProductosClienteServlet.class, "/cliente/productos");
            webserver.addServlet(DetalleProductoClienteServlet.class, "/cliente/detalle-producto");
            webserver.addServlet(CarritoServlet.class, "/cliente/carrito");
            webserver.addServlet(EliminarItemCarritoServlet.class, "/cliente/carrito/eliminar");
            webserver.addServlet(CompraServlet.class, "/cliente/compra");
            webserver.addServlet(VerificarCompraServlet.class, "/cliente/verificar-compra");
            webserver.addServlet(ServicioServlet.class, "/cliente/servicio");
            webserver.addServlet(ContactoServlet.class, "/cliente/contacto");

            webserver.start();
            System.out.println("Servidor iniciado en http://localhost:8080");

        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } finally {
            // Cerrar la conexión
            dbConnection.closeConnection();
        }
    }
}
