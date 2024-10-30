package com.pe.controller.administrador.productos;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.model.html.CategoriaHtml;
import com.pe.model.html.ProductoHtml;
import com.pe.model.service.CategoriaService;
import com.pe.model.service.ProductoService;
import com.pe.model.service.TamanioService;
import com.pe.model.service.VarianteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/producto/agregar")
public class AgregarProductoServlet extends BaseServlet {

    private static final Logger logger = LoggerFactory.getLogger(AgregarProductoServlet.class);
    private final CategoriaService categoriaService;
    private final TamanioService tamanioService;
    private final ProductoService productoService;
    private final VarianteService varianteService;

    public AgregarProductoServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
        this.tamanioService = new TamanioService();
        this.productoService = new ProductoService();
        this.varianteService = new VarianteService();
    }

    @Override
    protected String getContentPage() {
        return "/agregar_producto.html";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            String action = request.getParameter("action");

            if ("generarCamposVariantes".equals(action)) {
                int numVariantes = Integer.parseInt(request.getParameter("numVariantes"));
                TreeSet<Tamanio> tamanios = tamanioService.getTamanios();
                String html = ProductoHtml.generarCamposVariantes(numVariantes, tamanios);
                response.setContentType("text/html");
                response.getWriter().write(html);
            } else {
                // Cargar categorías y tamaños
                categoriaService.cargarCategorias();
                tamanioService.cargarTamanios();

                // Obtener nombres de categorías y tamaños
                TreeSet<Categoria> categorias = categoriaService.getCategorias();
                TreeSet<Tamanio> tamanios = tamanioService.getTamanios();

                String html = new String(Files.readAllBytes(Paths.get("src/main/resources/html/admin/agregar_producto.html")));

                // Reemplazar las opciones de categorías
                html = html.replace("${categoriasOptions}", CategoriaHtml.generarOpcionesCategorias(categorias));
                request.setAttribute("content", html);
                super.doGet(request, response);
            }
        } catch (SQLException e) {
            logger.error("Error al cargar datos: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al cargar datos: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String mensaje;
        String redirigirUrl = "/producto/agregar";

        try {
            // Obtener parámetros del formulario
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String categoriaStr = request.getParameter("categoria");
            String numVariantesStr = request.getParameter("num-variantes");

            if (nombre == null || nombre.isEmpty() || descripcion == null || descripcion.isEmpty() || categoriaStr == null || categoriaStr.isEmpty()) {
                throw new IllegalArgumentException("Nombre, descripción y categoría son obligatorios.");
            }

            int idCategoria = Integer.parseInt(categoriaStr);
            int numVariantes = Integer.parseInt(numVariantesStr);

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setIdCategoria(idCategoria);

            TreeSet<Variante> variantes = new TreeSet<>();

            for (int i = 1; i <= numVariantes; i++) {
                String codigo = request.getParameter("codigo-" + i);
                String precioStr = request.getParameter("precio-" + i);
                String imagen = request.getParameter("imagen-" + i);
                String stockStr = request.getParameter("stock-" + i);
                String cantidadStr = request.getParameter("cantidad-" + i);
                String idTamanioStr = request.getParameter("id-tamanio-" + i);

                if (codigo == null || codigo.isEmpty() || precioStr == null || precioStr.isEmpty() ||
                        imagen == null || imagen.isEmpty() || stockStr == null || stockStr.isEmpty() ||
                        cantidadStr == null || cantidadStr.isEmpty() || idTamanioStr == null || idTamanioStr.isEmpty()) {
                    throw new IllegalArgumentException("Todos los campos de variante son obligatorios.");
                }

                BigDecimal precio = new BigDecimal(precioStr);
                int stock = Integer.parseInt(stockStr);
                int cantidad = Integer.parseInt(cantidadStr);
                int idTamanio = Integer.parseInt(idTamanioStr);

                Variante variante = new Variante();
                variante.setCodigo(codigo);
                variante.setPrecio(precio);
                variante.setImagen(imagen);
                variante.setStock(stock);
                variante.setCantidad(cantidad);
                variante.setIdTamanio(idTamanio);

                variantes.add(variante);
            }

            // Agregar el producto y sus variantes
            productoService.agregarProducto(producto, variantes);
            mensaje = "Producto agregado exitosamente!";
            logger.info("Producto agregado: {}", producto.getNombre());

        } catch (SQLException e) {
            mensaje = "Error al agregar el producto";
            logger.warn(mensaje);
        }
        // Generar el script de alerta y redirección
        String alertScript = ProductoHtml.generarMensajeAlerta(mensaje, redirigirUrl);
        response.setContentType("text/html");
        response.getWriter().write(alertScript);
    }
}
