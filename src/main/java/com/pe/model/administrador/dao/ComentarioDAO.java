package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.Comentario;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class ComentarioDAO {
    private Connection connection;

    public ComentarioDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarComentarios(TreeSet<Comentario> comentarios) throws SQLException {
        String query = "SELECT * FROM comentario";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            comentarios.clear();
            while (rs.next()) {
                Comentario comentario = new Comentario(
                        rs.getInt("id_comentario"),
                        rs.getString("comentario"),
                        rs.getTimestamp("fecha").toLocalDateTime().toLocalDate(),
                        rs.getInt("id_cliente"),
                        Comentario.EstadoComentario.valueOf(rs.getString("estado"))
                );
                comentarios.add(comentario);
            }
        }
    }

    public void actualizarComentario(Comentario comentario) throws SQLException {
        String sql = "UPDATE comentario SET comentario = ?, id_cliente = ?, estado = ? WHERE id_comentario = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, comentario.getComentario());
            stmt.setInt(2, comentario.getIdCliente());
            stmt.setString(3, comentario.getEstado().name());
            stmt.setInt(4, comentario.getIdComentario());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el comentario, ID no encontrado");
            }
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
