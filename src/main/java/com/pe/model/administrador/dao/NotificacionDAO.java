package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.notificaciones.Notificacion;
import com.pe.util.DBConnection;

import java.sql.*;
import java.util.List;

public class NotificacionDAO {
    private Connection connection;

    public NotificacionDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public int insertarNotificacion(Notificacion notificacion) throws SQLException {
        String query = "INSERT INTO notificacion (mensaje) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, notificacion.getMensaje());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserción de notificación fallida, no se generó ID");
                }
            }
        }
    }

    public void cargarNotificaciones(List<Notificacion> notificaciones) throws SQLException {
        String query = "SELECT id_notificacion, mensaje, fecha FROM notificacion ORDER BY fecha DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            notificaciones.clear();

            while (rs.next()) {
                Notificacion notificacion = new Notificacion(
                        rs.getInt("id_notificacion"),
                        rs.getString("mensaje"),
                        rs.getTimestamp("fecha").toLocalDateTime().toLocalDate()
                );
                notificaciones.add(notificacion);
            }
        }
    }

    public int contarNotificacionesNoLeidasPorUsuario(int usuarioId) throws SQLException {
        String query = "SELECT COUNT(*) AS total_alertas_no_leidas " +
                "FROM detalle_notificacion " +
                "WHERE id_usuario = ? AND leida = 0";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_alertas_no_leidas");
                }
            }
        }

        return 0;
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
