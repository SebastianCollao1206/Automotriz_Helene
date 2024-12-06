package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.MetodoPago;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class MetodoPagoDAO {
    private Connection connection;

    public MetodoPagoDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarMetodosPago(TreeSet<MetodoPago> metodosPago) throws SQLException {
        String query = "SELECT * FROM metodo_pago";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            metodosPago.clear();
            while (rs.next()) {
                MetodoPago metodoPago = new MetodoPago(
                        rs.getInt("id_metodo_pago"),
                        rs.getString("nombre"),
                        MetodoPago.EstadoMetodoPago.valueOf(rs.getString("estado"))
                );
                metodosPago.add(metodoPago);
            }
        }
    }

    public void agregarMetodoPago(MetodoPago metodoPago) throws SQLException {
        String sql = "INSERT INTO metodo_pago (nombre, estado) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, metodoPago.getNombre());
            statement.setString(2, metodoPago.getEstado().name());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar método de pago: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarMetodoPago(MetodoPago metodoPago) throws SQLException {
        String sql = "UPDATE metodo_pago SET nombre = ?, estado = ? WHERE id_metodo_pago = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, metodoPago.getNombre());
            stmt.setString(2, metodoPago.getEstado().name());
            stmt.setInt(3, metodoPago.getIdMetodoPago());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el método de pago, ID no encontrado");
            }
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
