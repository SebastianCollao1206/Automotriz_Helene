package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.MetodoPagoDAO;
import com.pe.model.administrador.entidad.MetodoPago;

import java.sql.SQLException;
import java.util.TreeSet;

public class MetodoPagoService {
    private final MetodoPagoDAO metodoPagoDAO;
    private final TreeSet<MetodoPago> metodosPago;

    public MetodoPagoService() throws SQLException {
        this.metodoPagoDAO = new MetodoPagoDAO();
        this.metodosPago = new TreeSet<>(MetodoPago.METODO_PAGO_COMPARATOR_NATURAL_ORDER);
        cargarMetodosPago();
    }

    public void cargarMetodosPago() throws SQLException {
        metodoPagoDAO.cargarMetodosPago(this.metodosPago);
    }

    public MetodoPago.EstadoMetodoPago obtenerEstadoMetodoPago(String estadoStr) {
        if (estadoStr != null && !estadoStr.isEmpty()) {
            try {
                return MetodoPago.EstadoMetodoPago.valueOf(estadoStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado de método de pago no válido: " + estadoStr);
            }
        }
        return null;
    }

    public TreeSet<MetodoPago> buscarMetodosPago(String nombre, MetodoPago.EstadoMetodoPago estado) {
        TreeSet<MetodoPago> metodosFiltrados = new TreeSet<>(MetodoPago.METODO_PAGO_COMPARATOR_NATURAL_ORDER);
        for (MetodoPago metodoPago : metodosPago) {
            if (verificarMetodoPago(metodoPago, nombre, estado)) {
                metodosFiltrados.add(metodoPago);
            }
        }
        return metodosFiltrados;
    }

    private boolean verificarMetodoPago(MetodoPago metodoPago, String nombre, MetodoPago.EstadoMetodoPago estado) {
        boolean valido = true;
        if (nombre != null && !nombre.isEmpty() && !metodoPago.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
            valido = false;
        }
        if (estado != null && !metodoPago.getEstado().equals(estado)) {
            valido = false;
        }
        return valido;
    }

    public TreeSet<MetodoPago> cargarMetodosActivos() {
        TreeSet<MetodoPago> metodosActivos = new TreeSet<>(MetodoPago.METODO_PAGO_COMPARATOR_NATURAL_ORDER);
        for (MetodoPago metodoPago : metodosPago) {
            if (metodoPago.getEstado() == MetodoPago.EstadoMetodoPago.Activo) {
                metodosActivos.add(metodoPago);
            }
        }
        return metodosActivos;
    }

    public Integer obtenerIdMetodoPagoPorNombre(String nombreMetodoPago) {
        for (MetodoPago metodoPago : metodosPago) {
            if (metodoPago.getNombre().equalsIgnoreCase(nombreMetodoPago)) {
                return metodoPago.getIdMetodoPago();
            }
        }
        return null;
    }
}
