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

    // Constructor para inicializar la conexión
    public TamanioDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    // Metodo para cargar los tamaños de la BD
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

    // Metodo para agregar un tamaño
    public void agregarTamanio(Tamanio tamanio) throws SQLException {
        String sql = "INSERT INTO tamanio (unidad_medida, estado) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tamanio.getUnidadMedida());
            statement.setString(2, tamanio.getEstado().name());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar tamaño: " + e.getMessage());
            throw e; // Propagar la excepción
        }
    }

    // Metodo para actualizar un tamaño
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

    // Metodo para obtener un tamaño por ID
    public Tamanio obtenerTamanioPorId(int id) throws SQLException {
        String query = "SELECT * FROM tamanio WHERE id_tamanio = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Tamanio(
                            rs.getInt("id_tamanio"),
                            rs.getString("unidad_medida"),
                            Tamanio.EstadoTamanio.valueOf(rs.getString("estado"))
                    );
                } else {
                    return null;
                }
            }
        }
    }

    // Metodo para obtener el nombre del tamaño por su ID
    public String obtenerNombreTamanioPorId(int id) throws SQLException {
        String nombre = null;
        String sql = "SELECT unidad_medida FROM tamanio WHERE id_tamanio = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("unidad_medida");
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
