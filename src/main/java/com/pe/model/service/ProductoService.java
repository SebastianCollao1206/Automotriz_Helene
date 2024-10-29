package com.pe.model.service;

import com.pe.model.dao.ProductoDAO;
import com.pe.model.dao.VarianteDAO;
import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Variante;

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

    // Cargar todos los productos
    public void cargarProductos() throws SQLException {
        productoDAO.cargarProductos(productos);
    }

    public void agregarProducto(Producto producto, TreeSet<Variante> variantes) throws SQLException {
        // Agregar el producto y obtener su ID
        int productoId = productoDAO.agregarProducto(producto);
        if (productoId != -1) {
            // Agregar las variantes asociadas al producto
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

    // Metodo en ProductoService
    public String obtenerNombreProductoPorId(int idProducto) throws SQLException {
        Producto producto = productoDAO.obtenerProductoPorId(idProducto);
        return (producto != null) ? producto.getNombre() : null;
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

    public TreeSet<Producto> getProductos() {
        return productos;
    }
}
