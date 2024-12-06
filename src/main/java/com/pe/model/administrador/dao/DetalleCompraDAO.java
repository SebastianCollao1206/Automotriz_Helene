package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.DetalleCompra;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class DetalleCompraDAO {
    private Connection connection;

    public DetalleCompraDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarDetallesCompras(TreeSet<DetalleCompra> detallesCompras) throws SQLException {
        String query = "SELECT * FROM detalle_compra";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            detallesCompras.clear();
            while (rs.next()) {
                DetalleCompra detalleCompra = new DetalleCompra(
                        rs.getInt("id_detalle_compra"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getObject("id_compra", Integer.class),
                        rs.getObject("id_variante", Integer.class)
                );
                detallesCompras.add(detalleCompra);
            }
        }
    }

    public void agregarDetalleCompra(DetalleCompra detalleCompra) throws SQLException {
        String sql = "INSERT INTO detalle_compra (cantidad, precio_unitario, id_compra, id_variante) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, detalleCompra.getCantidad());
            statement.setDouble(2, detalleCompra.getPrecioUnitario());
            statement.setObject(3, detalleCompra.getIdCompra());
            statement.setObject(4, detalleCompra.getIdVariante());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar detalle de compra: " + e.getMessage());
            throw e;
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
