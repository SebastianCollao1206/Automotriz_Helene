import com.pe.model.dao.TamanioDAO;
import com.pe.model.entidad.Tamanio;
import com.pe.model.service.TamanioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TamanioServiceTest {

    @Mock
    private TamanioDAO tamanioDAOMock;

    @InjectMocks
    private TamanioService tamanioService;

    private Tamanio tamanioActivo;
    private Tamanio tamanioInactivo;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        // Inicializa instancias de Tamanio
        tamanioActivo = new Tamanio(1, "kg", Tamanio.EstadoTamanio.Activo);
        tamanioInactivo = new Tamanio(2, "litro", Tamanio.EstadoTamanio.Inactivo);

        // Inicializa el servicio y simula la carga inicial de tamaños
        tamanioService = new TamanioService();
    }

    @Test
    public void testAgregarTamanio() throws SQLException {
        // Simulación de agregar un tamaño
        tamanioService.agregarTamanio(tamanioActivo);

        // Verifica que el método en el DAO se llame una vez
        verify(tamanioDAOMock, times(1)).agregarTamanio(tamanioActivo);
    }

    @Test
    public void testObtenerNombreTamanioPorId() throws SQLException {
        // Configura el mock para retornar un nombre específico
        when(tamanioDAOMock.obtenerNombreTamanioPorId(1)).thenReturn("kg");

        // Ejecuta la prueba
        String nombre = tamanioService.obtenerNombreTamanioPorId(1);

        // Verifica el resultado
        assertEquals("kg", nombre);
        verify(tamanioDAOMock, times(1)).obtenerNombreTamanioPorId(1);
    }

    @Test
    public void testEliminarTamanio() throws SQLException {
        // Simulación de eliminar un tamaño activo
        when(tamanioDAOMock.obtenerTamanioPorId(1)).thenReturn(tamanioActivo);

        // Ejecuta el método de eliminación
        tamanioService.eliminarTamanio(1);

        // Verifica que el método de actualización en el DAO se llama
        verify(tamanioDAOMock, times(1)).actualizarTamanio(tamanioActivo);
        assertEquals(Tamanio.EstadoTamanio.Inactivo, tamanioActivo.getEstado());
    }

    @Test
    public void testActualizarTamanio() throws SQLException {
        // Simulación de obtener y actualizar un tamaño
        when(tamanioDAOMock.obtenerTamanioPorId(1)).thenReturn(tamanioActivo);

        // Actualiza el tamaño
        tamanioService.actualizarTamanio(1, "kg", "Inactivo");

        // Verifica que se llamaron los métodos correspondientes
        verify(tamanioDAOMock, times(1)).actualizarTamanio(any(Tamanio.class));
        assertEquals("kg", tamanioActivo.getUnidadMedida());
        assertEquals(Tamanio.EstadoTamanio.Inactivo, tamanioActivo.getEstado());
    }

    @Test
    public void testBuscarTamaniosPorUnidadMedidaYEstado() {
        // Configuración del conjunto de prueba
        TreeSet<Tamanio> tamanios = new TreeSet<>();
        tamanios.add(tamanioActivo);
        tamanios.add(tamanioInactivo);
        when(tamanioDAOMock.getTamanios()).thenReturn(tamanios);

        // Ejecuta la búsqueda
        TreeSet<Tamanio> tamaniosFiltrados = tamanioService.buscarTamanios("kg", Tamanio.EstadoTamanio.Activo);

        // Verifica los resultados
        assertEquals(1, tamaniosFiltrados.size());
        assertTrue(tamaniosFiltrados.contains(tamanioActivo));
        assertFalse(tamaniosFiltrados.contains(tamanioInactivo));
    }
}
