package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.Variante;
import com.pe.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class VarianteDAO {
    private Connection connection;

    public VarianteDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarVariantes(TreeSet<Variante> variantes) throws SQLException {
        String query = "SELECT * FROM variante";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            variantes.clear();
            while (rs.next()) {
                Variante variante = new Variante();
                variante.setIdVariante(rs.getInt("id_variante"));
                variante.setCodigo(rs.getString("codigo"));
                variante.setPrecio(rs.getBigDecimal("precio"));
                variante.setImagen(rs.getString("imagen"));
                variante.setStock(rs.getInt("stock"));
                variante.setCantidad(rs.getInt("cantidad"));
                variante.setIdProducto(rs.getInt("id_producto"));
                variante.setIdTamanio(rs.getInt("id_tamanio"));
                variantes.add(variante);
            }
        }
    }

    public void agregarVariantes(TreeSet<Variante> variantes, int productoId) throws SQLException {
        String sql = "INSERT INTO variante (codigo, precio, imagen, stock, cantidad, id_producto, id_tamanio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Variante variante : variantes) {
                statement.setString(1, variante.getCodigo());
                statement.setBigDecimal(2, variante.getPrecio());
                statement.setString(3, variante.getImagen());
                statement.setInt(4, variante.getStock());
                statement.setInt(5, variante.getCantidad());
                statement.setInt(6, productoId);
                statement.setInt(7, variante.getIdTamanio());

                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public void actualizarVariante(int id, String codigo, int idTamanio, int idProducto, BigDecimal precio, String imagen, int stock, int cantidad) throws SQLException {
        String sql = "UPDATE variante SET codigo = ?, id_tamanio = ?, id_producto = ?, precio = ?, imagen = ?, stock = ?, cantidad = ? WHERE id_variante = ?"; // Cambiado a id_variante
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, idTamanio);
            stmt.setInt(3, idProducto);
            stmt.setBigDecimal(4, precio);
            stmt.setString(5, imagen);
            stmt.setInt(6, stock);
            stmt.setInt(7, cantidad);
            stmt.setInt(8, id);
            stmt.executeUpdate();
        }
    }

    public void actualizarStock(int idVariante, int nuevoStock) throws SQLException {
        String query = "UPDATE variante SET stock = ? WHERE id_variante = ?";
        try (PreparedStatement stm = connection.prepareStatement(query)) {
            stm.setInt(1, nuevoStock);
            stm.setInt(2, idVariante);
            int filasActualizadas = stm.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró la variante con ID: " + idVariante);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar el stock: " + e.getMessage(), e);
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
