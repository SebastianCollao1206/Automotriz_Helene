package com.pe.model.administrador.dao;

import com.pe.model.administrador.entidad.Compra;
import com.pe.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CompraDAO {
    private Connection connection;

    public CompraDAO() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        this.connection = dbConnection.getConnection();
    }

    public void cargarCompras(TreeSet<Compra> compras) throws SQLException {
        String query = "SELECT * FROM compra";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            compras.clear();
            while (rs.next()) {
                Compra compra = new Compra(
                        rs.getInt("id_compra"),
                        rs.getTimestamp("fecha").toLocalDateTime().toLocalDate(),
                        rs.getDouble("total"),
                        rs.getTimestamp("fecha_recojo").toLocalDateTime().toLocalDate(),
                        rs.getObject("id_cliente", Integer.class),
                        rs.getObject("id_usuario", Integer.class),
                        rs.getObject("id_metodo_pago", Integer.class)
                );
                compras.add(compra);
            }
        }
    }

    public Compra agregarCompra(Compra compra) throws SQLException {
        String sql = "INSERT INTO compra (fecha, total, fecha_recojo, id_cliente, id_usuario, id_metodo_pago) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, compra.getFecha());
            statement.setDouble(2, compra.getTotal());
            statement.setObject(3, compra.getFechaRecojo());
            statement.setObject(4, compra.getIdCliente());
            statement.setObject(5, compra.getIdUsuario());
            statement.setObject(6, compra.getIdMetodoPago());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating purchase failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    compra.setIdCompra(generatedId);
                    return compra;
                } else {
                    throw new SQLException("Creating purchase failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar compra: " + e.getMessage());
            throw e;
        }
    }

    public List<Object[]> obtenerVentasPorMes() throws SQLException {
        List<Object[]> ventasPorMes = new ArrayList<>();
        String sql = "{call obtenerVentasPorMes()}";

        try (CallableStatement callableStatement = connection.prepareCall(sql);
             ResultSet rs = callableStatement.executeQuery()) {
            while (rs.next()) {
                Object[] venta = new Object[4];
                venta[0] = rs.getInt("mes"); 
                venta[1] = rs.getString("nombre_mes");
                venta[2] = rs.getDouble("total_ventas");
                venta[3] = rs.getInt("anio");
                ventasPorMes.add(venta);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por mes: " + e.getMessage());
            throw e;
        }
        return ventasPorMes;
    }

    public void cerrarConexion() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
