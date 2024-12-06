package com.pe.model.cliente.dao;

import com.pe.model.cliente.entidad.Cliente;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarClientes(TreeSet<Cliente> clientes) throws SQLException {
        String query = "SELECT * FROM cliente";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            clientes.clear();
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("id_cliente"),
                        rs.getString("correo"),
                        rs.getBytes("contrasena"),
                        rs.getTimestamp("fecha_registro") != null ? rs.getTimestamp("fecha_registro").toLocalDateTime().toLocalDate() : null,
                        rs.getString("dni"),
                        rs.getString("nombre")
                );
                clientes.add(cliente);
            }
        }
    }

    public void agregarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (correo, contrasena, fecha_registro, dni, nombre) VALUES (?, ?, NOW(), ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getCorreo());
            statement.setBytes(2, cliente.getContrasena());
            statement.setString(3, cliente.getDni());
            statement.setString(4, cliente.getNombre());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar cliente: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET dni = ?, nombre = ? WHERE correo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cliente.getDni());
            statement.setString(2, cliente.getNombre());
            statement.setString(3, cliente.getCorreo());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            throw e;
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
