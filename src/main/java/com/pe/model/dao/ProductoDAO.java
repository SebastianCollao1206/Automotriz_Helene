package com.pe.model.dao;

import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Variante;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class ProductoDAO {
    private Connection connection;

    public ProductoDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarProductos(TreeSet<Producto> productos) throws SQLException {
        String query = "SELECT * FROM producto";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            productos.clear();
            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("id_categoria")
                );
                productos.add(producto);
            }
        }
    }

    public int agregarProducto(Producto producto) throws SQLException {
        String sql = "INSERT INTO producto (nombre, descripcion, id_categoria) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, producto.getNombre());
            statement.setString(2, producto.getDescripcion());
            statement.setInt(3, producto.getIdCategoria());

            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Retorna el ID del producto insertado
                    }
                }
            }
            return -1;
        }
    }

    public void actualizarProducto(int id, String nombre, String descripcion, int idCategoria) throws SQLException {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, id_categoria = ? WHERE id_producto = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setInt(3, idCategoria);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
