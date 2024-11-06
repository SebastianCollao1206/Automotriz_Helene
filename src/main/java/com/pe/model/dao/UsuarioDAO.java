package com.pe.model.dao;

import com.pe.model.entidad.Usuario;
import com.pe.util.DBConnection;

import java.sql.*;
import java.util.TreeSet;

public class UsuarioDAO {

    private Connection connection;

    public UsuarioDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    //Cragar Usuarios
    public void cargarUsuarios(TreeSet<Usuario> usuarios) throws SQLException {
        String query = "SELECT * FROM usuario";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            usuarios.clear();
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getBytes("contrasena"),
                        Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario")),
                        Usuario.EstadoUsuario.valueOf(rs.getString("estado")),
                        rs.getTimestamp("fecha_registro").toLocalDateTime().toLocalDate(),
                        rs.getString("dni")
                );
                usuarios.add(usuario);
            }
        }
    }

    // Metodo para agregar usuario
    public void agregarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, correo, contrasena, tipo_usuario, estado, fecha_registro, dni) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getCorreo());
            statement.setBytes(3, usuario.getContrasena());
            statement.setString(4, usuario.getTipoUsuario().name());
            statement.setString(5, usuario.getEstado().name());
            statement.setObject(6, usuario.getFechaRegistro());
            statement.setString(7, usuario.getDni());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
            throw e;
        }
    }

    //Modificar usuario
    public void actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, correo = ?, dni = ?, " +
                "tipo_usuario = ?, estado = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getCorreo());
            stmt.setString(3, usuario.getDni());
            stmt.setString(4, usuario.getTipoUsuario().name());
            stmt.setString(5, usuario.getEstado().name());
            stmt.setInt(6, usuario.getIdUsuario());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el usuario, ID no encontrado");
            }
        }
    }

    // Metodo para cerrar la conexión
    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
