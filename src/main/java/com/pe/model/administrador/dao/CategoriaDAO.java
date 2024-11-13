package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.Categoria;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class CategoriaDAO {
    private Connection connection;

    public CategoriaDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarCategorias(TreeSet<Categoria> categorias) throws SQLException {
        String query = "SELECT * FROM categoria";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            categorias.clear();
            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre"),
                        Categoria.EstadoCategoria.valueOf(rs.getString("estado"))
                );
                categorias.add(categoria);
            }
        }
    }

    public void agregarCategoria(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categoria (nombre, estado) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNombre());
            statement.setString(2, categoria.getEstado().name());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar categoría: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarCategoria(Categoria categoria) throws SQLException {
        String sql = "UPDATE categoria SET nombre = ?, estado = ? WHERE id_categoria = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoria.getNombre());
            stmt.setString(2, categoria.getEstado().name());
            stmt.setInt(3, categoria.getIdCategoria());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar la categoría, ID no encontrado");
            }
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
