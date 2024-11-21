package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.Slider;
import com.pe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public class SliderDAO {

    private Connection connection;

    public SliderDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarSliders(TreeSet<Slider> sliders) throws SQLException {
        String query = "SELECT * FROM slider";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            sliders.clear();
            while (rs.next()) {
                Slider slider = new Slider(
                        rs.getInt("id_slider"),
                        rs.getString("titulo"),
                        rs.getString("eslogan"),
                        rs.getTimestamp("fecha_inicio").toLocalDateTime().toLocalDate(),
                        rs.getString("imagen"),
                        rs.getTimestamp("fecha_fin").toLocalDateTime().toLocalDate(),
                        Slider.EstadoSlider.valueOf(rs.getString("estado")),
                        rs.getInt("id_variante"),

                        rs.getString("relacionar_con")
                );
                sliders.add(slider);
            }
        }
    }

    public void agregarSlider(Slider slider) throws SQLException {
        String sql = "INSERT INTO slider (titulo, eslogan, fecha_inicio, imagen, fecha_fin, estado, id_variante, relacionar_con) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, slider.getTitulo());
            statement.setString(2, slider.getEslogan());
            statement.setObject(3, slider.getFechaInicio());
            statement.setString(4, slider.getImagen());
            statement.setObject(5, slider.getFechaFin());
            statement.setString(6, slider.getEstado().name());
            statement.setInt(7, slider.getIdVariante());
            statement.setString(8, slider.getRelacionarCon());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al agregar slider: " + e.getMessage());
            throw e;
        }
    }

    public void actualizarSlider(Slider slider) throws SQLException {
        String sql = "UPDATE slider SET titulo = ?, eslogan = ?, fecha_inicio = ?, imagen = ?, fecha_fin = ?, estado = ?, id_variante = ?, relacionar_con = ? WHERE id_slider = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, slider.getTitulo());
            stmt.setString(2, slider.getEslogan());
            stmt.setObject(3, slider.getFechaInicio());
            stmt.setString(4, slider.getImagen());
            stmt.setObject(5, slider.getFechaFin());
            stmt.setString(6, slider.getEstado().name());
            stmt.setInt(7, slider.getIdVariante());
            stmt.setString(8, slider.getRelacionarCon());
            stmt.setInt(9, slider.getIdSlider());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo actualizar el slider, ID no encontrado");
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar slider: " + e.getMessage());
            throw e;
        }
    }

    public void eliminarSlider(int idSlider) throws SQLException {
        String sql = "DELETE FROM slider WHERE id_slider = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idSlider);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se pudo eliminar el slider, ID no encontrado");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar slider: " + e.getMessage());
            throw e;
        }
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
