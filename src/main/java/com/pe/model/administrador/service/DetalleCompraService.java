package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.DetalleCompraDAO;
import com.pe.model.administrador.entidad.DetalleCompra;
import java.sql.SQLException;
import java.util.TreeSet;

public class DetalleCompraService {
    private final DetalleCompraDAO detalleCompraDAO;
    private final TreeSet<DetalleCompra> detallesCompras;

    public DetalleCompraService() throws SQLException {
        this.detalleCompraDAO = new DetalleCompraDAO();
        this.detallesCompras = new TreeSet<>();
        cargarDetallesCompras();
    }

    public TreeSet<DetalleCompra> getDetallesCompras() {
        return detallesCompras;
    }

    public void agregarDetalleCompra(DetalleCompra detalleCompra) throws SQLException {
        detalleCompraDAO.agregarDetalleCompra(detalleCompra);
        cargarDetallesCompras();
    }

    public void cargarDetallesCompras() throws SQLException {
        detalleCompraDAO.cargarDetallesCompras(detallesCompras);
    }

    public DetalleCompra obtenerDetalleCompraPorId(int id) {
        return detallesCompras.stream()
                .filter(d -> d.getIdDetalleCompra() == id)
                .findFirst()
                .orElse(null);
    }

//    public void agregarDetalleCompra(int cantidad, double precioUnitario, Integer idCompra, Integer idVariante) throws SQLException {
//        DetalleCompra detalleCompra = new DetalleCompra();
//        detalleCompra.setCantidad(cantidad);
//        detalleCompra.setPrecioUnitario(precioUnitario);
//        detalleCompra.setIdCompra(idCompra);
//        detalleCompra.setIdVariante(idVariante);
//
//        agregarDetalleCompra(detalleCompra);
//    }
    public void agregarDetalleCompra(int cantidad, double precioUnitario, Integer idCompra, Integer idVariante) throws SQLException {
        DetalleCompra detalleCompra = new DetalleCompra();
        detalleCompra.setCantidad(cantidad);
        detalleCompra.setPrecioUnitario(precioUnitario);
        detalleCompra.setIdCompra(idCompra);
        detalleCompra.setIdVariante(idVariante);

        detalleCompraDAO.agregarDetalleCompra(detalleCompra);
        cargarDetallesCompras();
    }
}
