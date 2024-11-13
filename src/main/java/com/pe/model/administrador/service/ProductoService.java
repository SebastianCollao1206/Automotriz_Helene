package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.ProductoDAO;
import com.pe.model.administrador.dao.VarianteDAO;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Variante;
import com.pe.util.Validaciones;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TreeSet;

public class ProductoService {
    private final ProductoDAO productoDAO;
    private final VarianteDAO varianteDAO;
    private final TreeSet<Producto> productos;
    private final TreeSet<Variante> variantes;
    private final CategoriaService categoriaService;

    public ProductoService() throws SQLException {
        this.productoDAO = new ProductoDAO();
        this.varianteDAO = new VarianteDAO();
        this.categoriaService = new CategoriaService();
        this.productos = new TreeSet<>(Producto.PRODUCTO_COMPARATOR_NATURAL_ORDER);
        this.variantes = new TreeSet<>(Variante.VARIANTE_COMPARATOR_NATURAL_ORDER);
        cargarProductos();
    }

    public void cargarProductos() throws SQLException {
        productoDAO.cargarProductos(productos);
    }

    public void agregarProducto(String nombre, String descripcion, int idCategoria, int numVariantes, HttpServletRequest request) throws SQLException {
        Validaciones.validarDescripcion(descripcion);
        Validaciones.validarNombreCategoria(String.valueOf(idCategoria));

        if (existeProducto(nombre)) {
            throw new IllegalArgumentException("El nombre del producto ya está registrado en el sistema");
        }

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

            Variante variante = new Variante();
            variante.setCodigo(codigo);
            variante.setPrecio(new BigDecimal(precioStr));
            variante.setImagen(imagen);
            variante.setStock(Integer.parseInt(stockStr));
            variante.setCantidad(Integer.parseInt(cantidadStr));
            variante.setIdTamanio(Integer.parseInt(idTamanioStr));

            variantes.add(variante);
        }
        int productoId = productoDAO.agregarProducto(producto);
        if (productoId != -1) {
            VarianteService varianteService = new VarianteService();
            varianteService.agregarVariantes(variantes, productoId);
        }
    }


    public TreeSet<Producto> buscarProductos(String nombre, String categoriaId) {
        TreeSet<Producto> productosFiltrados = new TreeSet<>(Producto.PRODUCTO_COMPARATOR_NATURAL_ORDER);
        for (Producto producto : productos) {
            if (verificarProducto(producto, nombre, categoriaId)) {
                productosFiltrados.add(producto);
            }
        }
        return productosFiltrados;
    }

    public String obtenerNombreCategoria(int idCategoria) throws SQLException {
        Categoria categoria = categoriaService.obtenerCategoriaPorId(idCategoria);
        return categoria != null ? categoria.getNombre() : "Sin categoría";
    }

    public String obtenerNombreProductoPorId(int idProducto) {
        Producto producto = obtenerProductoPorId(idProducto);
        return (producto != null) ? producto.getNombre() : null;
    }

    public Producto obtenerProductoPorId(int id) {
        return productos.stream()
                .filter(producto -> producto.getIdProducto() == id)
                .findFirst()
                .orElse(null);
    }

    public void actualizarProducto(int id, String nombre, String descripcion, int idCategoria) throws SQLException {
        Producto producto = obtenerProductoPorId(id);
        if (producto != null) {
            if (!producto.getNombre().equals(nombre)) {
                Validaciones.validarNombreCategoria(nombre);
                if (existeProducto(nombre)) {
                    throw new IllegalArgumentException("El nombre del producto ya está registrado en el sistema");
                }
            }
            if (!producto.getDescripcion().equals(descripcion)) {
                Validaciones.validarDescripcion(descripcion);
            }
            productoDAO.actualizarProducto(id, nombre, descripcion, idCategoria);
        } else {
            throw new SQLException("Producto no encontrado");
        }
    }

    private boolean existeProducto(String nombre) {
        return productos.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre.trim()));
    }

    private boolean verificarProducto(Producto producto, String nombre, String categoriaId) {
        boolean valido = true;

        if (nombre != null && !nombre.isEmpty() && !producto.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
            valido = false;
        }

        if (categoriaId != null && !categoriaId.isEmpty() && !String.valueOf(producto.getIdCategoria()).equals(categoriaId)) {
            valido = false;
        }

        return valido;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        String nombreBuscado = nombre.toLowerCase();
        return productos.stream()
                .filter(producto -> producto.getNombre().toLowerCase().contains(nombreBuscado))
                .findFirst()
                .orElse(null);
    }

    public TreeSet<Producto> getProductos() {
        return productos;
    }
}
