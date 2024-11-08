package com.pe.model.dao;

import com.pe.model.entidad.Tamanio;
import com.pe.model.service.TamanioService;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class TamanioDAO {
    private Connection connection;

    public TamanioDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarTamanios(TreeSet<Tamanio> tamanios) throws SQLException {
        String query = "SELECT * FROM tamanio";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            tamanios.clear();
            while (rs.next()) {
                Tamanio tamanio = new Tamanio(
                        rs.getInt("id_tamanio"),
                        rs.getString("unidad_medida"),
                        Tamanio.EstadoTamanio.valueOf(rs.getString("estado"))
                );
                tamanios.add(tamanio);
            }
        }
    }

    public void agregarTamanio(Tamanio tamanio) throws SQLException {
        String sql = "INSERT INTO tamanio (unidad_medida, estado) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tamanio.getUnidadMedida());
            statement.setString(2, tamanio.getEstado().name());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar tamaño: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarTamanio(Tamanio tamanio) throws SQLException {
        String sql = "UPDATE tamanio SET unidad_medida = ?, estado = ? WHERE id_tamanio = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tamanio.getUnidadMedida());
            stmt.setString(2, tamanio.getEstado().name());
            stmt.setInt(3, tamanio.getIdTamanio());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el tamaño, ID no encontrado");
            }
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
