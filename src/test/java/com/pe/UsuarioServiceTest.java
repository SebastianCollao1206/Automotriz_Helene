package com.pe.model.service;

import com.pe.model.dao.UsuarioDAO;
import com.pe.model.entidad.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioDAO usuarioDAO;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() throws SQLException {
        usuarioDAO = mock(UsuarioDAO.class);
        usuarioService = new UsuarioService(usuarioDAO);
    }

    @Test
    void testAgregarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setCorreo("juan@example.com");
        usuario.setDni("12345678");
        usuario.setTipoUsuario(Usuario.TipoUsuario.Administrador);
        usuario.setEstado(Usuario.EstadoUsuario.Activo);
        usuario.setFechaRegistro(LocalDate.now());
        byte[] contrasenaEncriptada = usuarioService.encryptPassword("contrasena123");
        usuario.setContrasena(contrasenaEncriptada);

        usuarioService.agregarUsuario(usuario);

        // Verificar que se llama a agregarUsuario en el DAO
        verify(usuarioDAO).agregarUsuario(any(Usuario.class));
    }

    @Test
    void testAutenticarUsuario_Exito() throws Exception {
        String correo = "juan@example.com";
        String contrasena = "contrasena123";

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setContrasena(usuarioService.encryptPassword(contrasena));

        when(usuarioDAO.obtenerUsuarioPorCorreo(correo)).thenReturn(usuario);

        Usuario result = usuarioService.autenticarUsuario(correo, contrasena);

        assertNotNull(result);
        assertEquals(correo, result.getCorreo());
    }

    @Test
    void testAutenticarUsuario_Fallo() throws Exception {
        String correo = "juan@example.com";
        String contrasena = "incorrecta";

        when(usuarioDAO.obtenerUsuarioPorCorreo(correo)).thenReturn(null);

        Usuario result = usuarioService.autenticarUsuario(correo, contrasena);

        assertNull(result);
    }

    @Test
    void testActualizarUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Juan Actualizado");
        usuario.setCorreo("juan_actualizado@example.com");
        usuario.setDni("12345678");
        usuario.setTipoUsuario(Usuario.TipoUsuario.Administrador);
        usuario.setEstado(Usuario.EstadoUsuario.Activo);

        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuario);

        usuarioService.actualizarUsuario(1, "Juan Actualizado", "juan_actualizado@example.com", "12345678",
                "Administrador", "Activo");

        // Verificar que se llama a actualizarUsuario en el DAO
        verify(usuarioDAO).actualizarUsuario(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario() throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEstado(Usuario.EstadoUsuario.Activo);

        when(usuarioDAO.obtenerUsuarioPorId(1)).thenReturn(usuario);

        usuarioService.eliminarUsuario(1);

        // Verificar que se llama a actualizarUsuario en el DAO
        verify(usuarioDAO).actualizarUsuario(any(Usuario.class));
    }

    @Test
    void testBuscarUsuarios() throws SQLException {
        // Configurar usuarios de prueba
        TreeSet<Usuario> usuarios = new TreeSet<>();
        Usuario usuario1 = new Usuario();
        usuario1.setNombre("Juan");
        usuario1.setDni("12345678");
        usuario1.setTipoUsuario(Usuario.TipoUsuario.Administrador);
        usuario1.setEstado(Usuario.EstadoUsuario.Activo);
        usuarios.add(usuario1);

        Usuario usuario2 = new Usuario();
        usuario2.setNombre("Pedro");
        usuario2.setDni("87654321");
        usuario2.setTipoUsuario(Usuario.TipoUsuario.Usuario);
        usuario2.setEstado(Usuario.EstadoUsuario.Inactivo);
        usuarios.add(usuario2);

        when(usuarioDAO.cargarUsuarios(any(TreeSet.class))).thenAnswer(invocation -> {
            TreeSet<Usuario> arg = invocation.getArgument(0);
            arg.addAll(usuarios);
            return null;
        });

        usuarioService.cargarUsuarios();

        TreeSet<Usuario> result = usuarioService.buscarUsuarios("Juan", null, null, null);

        assertEquals(1, result.size());
        assertEquals("Juan", result.first().getNombre());
    }
}
