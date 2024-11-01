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

    // Constructor para inicializar la conexión
    public ProductoDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    // Metodo para cargar productos de la BD
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

    // Metodo para agregar un producto
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
            return -1; // Indica que no se insertó el producto
        }
    }

    // Metodo para obtener un producto por su ID
    public Producto obtenerProductoPorId(int idProducto) throws SQLException {
        Producto producto = null;
        String sql = "SELECT * FROM producto WHERE id_producto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("id_categoria")
                    );
                }
            }
        }

        return producto;
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

    // Metodo para buscar un producto por nombre
    public Producto buscarProductoPorNombre(String nombre) throws SQLException {
        Producto producto = null;
        String sql = "SELECT id_producto, nombre FROM producto WHERE nombre LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + nombre + "%");
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                producto = new Producto();
                producto.setIdProducto(resultSet.getInt("id_producto"));
                producto.setNombre(resultSet.getString("nombre"));
            }
        }
        return producto;
    }

    // Metodo para obtener el nombre del producto por su ID
    public String obtenerNombreProductoPorId(int id) throws SQLException {
        String nombre = null;
        String sql = "SELECT nombre FROM producto WHERE id_producto = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre");
            }
        }
        return nombre;
    }

    // Metodo para cerrar la conexión
    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
