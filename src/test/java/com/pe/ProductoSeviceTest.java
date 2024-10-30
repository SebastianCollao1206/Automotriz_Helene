package com.pe.model.service;

import com.pe.model.dao.ProductoDAO;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Variante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.sql.SQLException;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductoServiceTest {
    private ProductoService productoService;
    private ProductoDAO productoDAOMock;
    private VarianteDAO varianteDAOMock;

    @BeforeEach
    public void setUp() throws SQLException {
        productoDAOMock = mock(ProductoDAO.class);
        varianteDAOMock = mock(VarianteDAO.class);
        productoService = new ProductoService(productoDAOMock, varianteDAOMock);
    }

    @Test
    public void testCargarProductos() throws SQLException {
        TreeSet<Producto> productos = new TreeSet<>();
        Producto producto = new Producto(1, "Producto Test", "Descripción", 1);
        productos.add(producto);

        doAnswer(invocation -> {
            productos.clear();
            productos.add(producto);
            return null;
        }).when(productoDAOMock).cargarProductos(any(TreeSet.class));

        productoService.cargarProductos();

        assertNotNull(productoService.getProductos(), "El conjunto de productos no debería ser nulo.");
        assertTrue(productoService.getProductos().contains(producto), "Debería contener el producto cargado.");
    }

    @Test
    public void testAgregarProducto() throws SQLException {
        Producto producto = new Producto();
        producto.setNombre("Producto Nuevo");
        producto.setDescripcion("Descripción");
        producto.setIdCategoria(1);

        TreeSet<Variante> variantes = new TreeSet<>();
        Variante variante = new Variante(1, "Color", new BigDecimal("10.00"));
        variantes.add(variante);

        when(productoDAOMock.agregarProducto(producto)).thenReturn(1);
        
        productoService.agregarProducto(producto, variantes);

        verify(productoDAOMock, times(1)).agregarProducto(producto);
    }

    @Test
    public void testBuscarProductos() {
        Producto producto1 = new Producto(1, "Producto Test", "Descripción", 1);
        Producto producto2 = new Producto(2, "Producto Test 2", "Descripción", 1);
        productoService.getProductos().add(producto1);
        productoService.getProductos().add(producto2);

        TreeSet<Producto> resultado = productoService.buscarProductos("Test", "1");

        assertTrue(resultado.contains(producto1));
        assertTrue(resultado.contains(producto2));
    }

    @Test
    public void testObtenerNombreProductoPorId() throws SQLException {
        Producto producto = new Producto(1, "Producto Test", "Descripción", 1);
        when(productoDAOMock.obtenerProductoPorId(1)).thenReturn(producto);

        String nombre = productoService.obtenerNombreProductoPorId(1);

        assertEquals("Producto Test", nombre);
    }

    @Test
    public void testActualizarProducto() throws SQLException {
        productoService.actualizarProducto(1, "Nombre Actualizado", "Descripción actualizada", 1);

        verify(productoDAOMock, times(1)).actualizarProducto(1, "Nombre Actualizado", "Descripción actualizada", 1);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        productoService = null;
        productoDAOMock = null;
        varianteDAOMock = null;
    }
}
