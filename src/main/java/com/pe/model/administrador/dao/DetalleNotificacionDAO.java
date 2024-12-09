package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.notificaciones.DetalleNotificacion;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DetalleNotificacionDAO {
    private Connection connection;

    public DetalleNotificacionDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void insertarDetallesNotificacion(int idNotificacion, List<Integer> usuarioIds) throws SQLException {
        String query = "INSERT INTO detalle_notificacion (id_usuario, id_notificacion, leida) VALUES (?, ?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (Integer usuarioId : usuarioIds) {
                stmt.setInt(1, usuarioId);
                stmt.setInt(2, idNotificacion);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void cargarDetallesNotificacion(List<DetalleNotificacion> detallesNotificacion) throws SQLException {
        String query = "SELECT id_detalle_notificacion, id_usuario, id_notificacion, leida " +
                "FROM detalle_notificacion ORDER BY id_detalle_notificacion";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            detallesNotificacion.clear();

            while (rs.next()) {
                DetalleNotificacion detalle = new DetalleNotificacion(
                        rs.getInt("id_detalle_notificacion"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_notificacion"),
                        rs.getBoolean("leida")
                );
                detallesNotificacion.add(detalle);
            }
        }
    }

    public void marcarTodasNotificacionesComoLeidas(int usuarioId) throws SQLException {
        String query = "UPDATE detalle_notificacion SET leida = 1 " +
                "WHERE id_usuario = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
