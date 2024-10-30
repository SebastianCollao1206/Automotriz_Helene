package com.pe;

import com.pe.model.dao.CategoriaDAO;
import com.pe.model.entidad.Categoria;
import com.pe.model.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
public class CategoriaServiceTest {
    private CategoriaDAO categoriaDAO;
    private CategoriaService categoriaService;

    @BeforeEach
    public void setUp() throws SQLException {
        categoriaDAO = new CategoriaDAO();
        categoriaService = new CategoriaService();
    }

    @Test
    public void testAgregarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Nueva Categoria");
        categoria.setEstado(Categoria.EstadoCategoria.Activo);

        categoriaService.agregarCategoria(categoria);

        assertTrue(categoriaService.getCategorias().contains(categoria), "La categoría debería estar en el conjunto.");
    }

    @Test
    public void testCargarCategorias() throws SQLException {
        categoriaService.cargarCategorias();

        assertNotNull(categoriaService.getCategorias(), "El conjunto de categorías no debería ser nulo.");
        assertNotEquals(0, categoriaService.getCategorias().size(), "Debería haber al menos una categoría cargada.");
    }
}
