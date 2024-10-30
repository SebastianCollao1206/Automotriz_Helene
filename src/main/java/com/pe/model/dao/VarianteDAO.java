package com.pe.model.dao;

import com.pe.model.entidad.Variante;
import com.pe.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeSet;

public class VarianteDAO {
    private Connection connection;

    // Constructor para inicializar la conexión
    public VarianteDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    // Metodo para agregar variantes
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

    public TreeSet<Variante> obtenerVariantesPorProducto(int idProducto) throws SQLException {
        TreeSet<Variante> variantes = new TreeSet<>();
        String query = "SELECT * FROM variante WHERE id_producto = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idProducto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Variante variante = new Variante(
                            rs.getInt("id_variante"),
                            rs.getString("codigo"),
                            rs.getBigDecimal("precio"),
                            rs.getString("imagen"),
                            rs.getInt("stock"),
                            rs.getInt("cantidad"),
                            idProducto, // Usar el idProducto pasado como parámetro
                            rs.getInt("id_tamanio")
                    );
                    variantes.add(variante);
                }
            }
        }
        return variantes;
    }

    // Metodo para obtener una variante por su ID
    public Variante obtenerVariantePorId(int id) throws SQLException {
        Variante variante = null;
        String sql = "SELECT * FROM variante WHERE id_variante = ?"; // Asegúrate de que sea id_variante

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                variante = new Variante();
                variante.setIdVariante(rs.getInt("id_variante")); // Asegúrate de que sea id_variante
                variante.setCodigo(rs.getString("codigo"));
                variante.setIdTamanio(rs.getInt("id_tamanio"));
                variante.setPrecio(rs.getBigDecimal("precio"));
                variante.setImagen(rs.getString("imagen"));
                variante.setStock(rs.getInt("stock"));
                variante.setCantidad(rs.getInt("cantidad"));
                variante.setIdProducto(rs.getInt("id_producto"));
            }
        }
        return variante;
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

            System.out.println("Ejecutando consulta: " + query + " con valores: " + nuevoStock + ", " + idVariante);

            int filasActualizadas = stm.executeUpdate();
            System.out.println("Filas actualizadas: " + filasActualizadas);

            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró la variante con ID: " + idVariante);
            }
        } catch (SQLException e) {
            // Manejo de excepciones
            throw new SQLException("Error al actualizar el stock: " + e.getMessage(), e);
        }
    }

    public int obtenerIdProductoPorVariante(int idVariante) throws SQLException {
        int idProducto = 0;
        String query = "SELECT id_producto FROM variante WHERE id_variante = ?"; // Ajusta la consulta según tu esquema
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idVariante);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                idProducto = rs.getInt("id_producto");
            }
        } catch (SQLException e) {
            throw new SQLException("Error al obtener el ID del producto: " + e.getMessage(), e);
        }
        return idProducto;
    }

    // Metodo para cerrar la conexión
    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
