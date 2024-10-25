package com.pe.model.dao;

import com.pe.model.entidad.Usuario;
import com.pe.util.DBConnection;

import java.sql.*;

public class UsuarioDAO {
    // Metodo para agregar usuario
    public void agregarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, correo, contrasena, tipo_usuario, estado, fecha_registro, dni) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            DBConnection dbConnection = new DBConnection();
            connection = dbConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getCorreo());
            statement.setBytes(3, usuario.getContrasena());
            statement.setString(4, usuario.getTipoUsuario().name());
            statement.setString(5, usuario.getEstado().name());
            statement.setObject(6, usuario.getFechaRegistro());
            statement.setString(7, usuario.getDni());

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar el PreparedStatement: " + e.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    // Metodo para obtener usuario por correo
    public Usuario obtenerUsuarioPorCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            DBConnection dbConnection = new DBConnection();
            connection = dbConnection.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, correo);
            resultSet = statement.executeQuery();

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
            return null; // Usuario no encontrado
        } finally {
            // Cerrar recursos
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }
    }
}
