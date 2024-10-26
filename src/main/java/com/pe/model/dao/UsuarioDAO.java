package com.pe.model.dao;

import com.pe.model.entidad.Usuario;
import com.pe.util.DBConnection;

import java.sql.*;
import java.util.TreeSet;

public class UsuarioDAO {

    private Connection connection;

    // Constructor para inicializar la conexión
    public UsuarioDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    //Traer los usuarios de la BD
    public void cargarUsuarios(TreeSet<Usuario> usuarios) throws SQLException {
        String query = "SELECT * FROM usuario";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
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
                usuarios.add(usuario); // Aquí se agrega al TreeSet pasado como argumento
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
            throw e; // Propagar la excepción
        }
    }

    // Metodo para obtener usuario por correo
    public Usuario obtenerUsuarioPorCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, correo);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setNombre(resultSet.getString("nombre"));
                usuario.setCorreo(resultSet.getString("correo"));
                usuario.setContrasena(resultSet.getBytes("contrasena"));
                usuario.setTipoUsuario(Usuario.TipoUsuario.valueOf(resultSet.getString("tipo_usuario")));
                usuario.setEstado(Usuario.EstadoUsuario.valueOf(resultSet.getString("estado")));
                usuario.setFechaRegistro(resultSet.getDate("fecha_registro").toLocalDate());
                usuario.setDni(resultSet.getString("dni"));
                return usuario;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por correo: " + e.getMessage());
            throw e;
        }
    }

    // Metodo para cerrar la conexión
    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
