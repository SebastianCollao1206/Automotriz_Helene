package com.pe.model.service;

import com.pe.model.dao.VarianteDAO;
import com.pe.model.entidad.Variante;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VarianteServiceTest {

    private VarianteService varianteService;
    private VarianteDAO varianteDAOMock;

    @BeforeEach
    void setUp() throws SQLException {
        varianteDAOMock = Mockito.mock(VarianteDAO.class);
        varianteService = new VarianteService(varianteDAOMock); // Constructor modificado
    }

    @Test
    void testAgregarVariantes() throws SQLException {
        TreeSet<Variante> variantes = new TreeSet<>();
        variantes.add(new Variante(1, "ABC123", BigDecimal.valueOf(100.00), "imagen.jpg", 10, 5, 1, 1));
        
        varianteService.agregarVariantes(variantes, 1);
        
        verify(varianteDAOMock, times(1)).agregarVariantes(variantes, 1);
    }

    @Test
    void testObtenerVariantesPorProducto() throws SQLException {
        int productoId = 1;
        TreeSet<Variante> expectedVariantes = new TreeSet<>();
        expectedVariantes.add(new Variante(1, "ABC123", BigDecimal.valueOf(100.00), "imagen.jpg", 10, 5, 1, 1));

        when(varianteDAOMock.obtenerVariantesPorProducto(productoId)).thenReturn(expectedVariantes);
        
        TreeSet<Variante> result = varianteService.obtenerVariantesPorProducto(productoId);
        
        Assertions.assertEquals(expectedVariantes, result);
    }

    @Test
    void testActualizarStock() throws SQLException {
        int idVariante = 1;
        int nuevoStock = 20;

        varianteService.actualizarStock(idVariante, nuevoStock);
        
        verify(varianteDAOMock, times(1)).actualizarStock(idVariante, nuevoStock);
    }

    @Test
    void testObtenerVariantePorId() throws SQLException {
        int idVariante = 1;
        Variante expectedVariante = new Variante(1, "ABC123", BigDecimal.valueOf(100.00), "imagen.jpg", 10, 5, 1, 1);

        when(varianteDAOMock.obtenerVariantePorId(idVariante)).thenReturn(expectedVariante);

        Variante result = varianteService.obtenerVariantePorId(idVariante);
        
        Assertions.assertEquals(expectedVariante, result);
    }

    @Test
    void testActualizarVariante() throws SQLException {
        Variante variante = new Variante(1, "ABC123", BigDecimal.valueOf(100.00), "imagen.jpg", 10, 5, 1, 1);

        varianteService.actualizarVariante(1, variante.getCodigo(), variante.getIdTamanio(), variante.getIdProducto(),
                variante.getPrecio(), variante.getImagen(), variante.getStock(), variante.getCantidad());

        verify(varianteDAOMock, times(1)).actualizarVariante(eq(1), eq(variante.getCodigo()), eq(variante.getIdTamanio()),
                eq(variante.getIdProducto()), eq(variante.getPrecio()), eq(variante.getImagen()), eq(variante.getStock()), eq(variante.getCantidad()));
    }

    @AfterEach
    void tearDown() {
        varianteService = null;
        varianteDAOMock = null;
    }
}
