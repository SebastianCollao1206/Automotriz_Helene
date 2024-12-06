package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.CompraDAO;
import com.pe.model.administrador.entidad.Compra;
import com.pe.util.Validaciones;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeSet;

public class CompraService {
    private final CompraDAO compraDAO;
    private final TreeSet<Compra> compras;

    public CompraService() throws SQLException {
        this.compraDAO = new CompraDAO();
        this.compras = new TreeSet<>();
        cargarCompras();
    }

    public TreeSet<Compra> getCompras() {
        return compras;
    }

    public void agregarCompra(Compra compra) throws SQLException {
        compraDAO.agregarCompra(compra);
        cargarCompras();
    }

    public void cargarCompras() throws SQLException {
        compraDAO.cargarCompras(compras);
    }

    public Compra obtenerCompraPorId(int id) {
        return compras.stream()
                .filter(c -> c.getIdCompra() == id)
                .findFirst()
                .orElse(null);
    }

//    public Compra agregarCompra(LocalDate fecha, double total, LocalDate fechaRecojo, Integer idCliente, Integer idUsuario, Integer idMetodoPago) throws SQLException {
//        Validaciones.validarFechaRecojo(fechaRecojo);
//
//        Compra compra = new Compra();
//        compra.setFecha(fecha);
//        compra.setTotal(total);
//        compra.setFechaRecojo(fechaRecojo);
//        compra.setIdCliente(idCliente);
//        compra.setIdUsuario(idUsuario);
//        compra.setIdMetodoPago(idMetodoPago);
//
//        compraDAO.agregarCompra(compra);
//        return compra;
//    }

    public Compra agregarCompra(LocalDate fecha, double total, LocalDate fechaRecojo, Integer idCliente, Integer idUsuario, Integer idMetodoPago) throws SQLException {
        Validaciones.validarFechaRecojo(fechaRecojo);

        Compra compra = new Compra();
        compra.setFecha(fecha);
        compra.setTotal(total);
        compra.setFechaRecojo(fechaRecojo);
        compra.setIdCliente(idCliente);
        compra.setIdUsuario(idUsuario);
        compra.setIdMetodoPago(idMetodoPago);

        Compra compraConId = compraDAO.agregarCompra(compra);
        cargarCompras();
        return compraConId;
    }
}
