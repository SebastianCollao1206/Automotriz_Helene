package com.pe.model.service;

import com.pe.model.dao.VarianteDAO;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;
import com.pe.util.Validaciones;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.TreeSet;

public class VarianteService {
    private final VarianteDAO varianteDAO;
    private final TreeSet<Variante> variantes;
    private final TamanioService tamanioService;

    public VarianteService() throws SQLException {
        this.varianteDAO = new VarianteDAO();
        this.variantes = new TreeSet<>(Variante.VARIANTE_COMPARATOR_NATURAL_ORDER);
        this.tamanioService = new TamanioService();
    }

    public void agregarVariantes(TreeSet<Variante> variantes, int productoId) throws SQLException {
        Validaciones.validarVariantes(variantes);
        varianteDAO.agregarVariantes(variantes, productoId);
    }

    public void agregarVariante(Variante variante) throws SQLException {
        TreeSet<Variante> variantes = new TreeSet<>();
        variantes.add(variante);
        Validaciones.validarVariantes(variantes);
        varianteDAO.agregarVariantes(variantes, variante.getIdProducto());
    }

    public TreeSet<Variante> obtenerVariantesPorProducto(int idProducto) throws SQLException {
        return varianteDAO.obtenerVariantesPorProducto(idProducto);
    }

    public TreeSet<Variante> getVariantes() {
        return variantes;
    }

    public Tamanio obtenerTamanioPorId(int idTamanio) throws SQLException {
        return tamanioService.obtenerTamanioPorId(idTamanio);
    }


    public void actualizarStock(int idVariante, int nuevoStock) throws SQLException {
        System.out.println("Llamando a actualizarStock con ID: " + idVariante + " y nuevo stock: " + nuevoStock);
        try {
            varianteDAO.actualizarStock(idVariante, nuevoStock);
        } catch (SQLException e) {
            throw new SQLException("Error en el servicio al actualizar el stock: " + e.getMessage(), e);
        }
    }

    public void actualizarVariante(int id, String codigo, int idTamanio, int idProducto, BigDecimal precio, String imagen, int stock, int cantidad) throws SQLException {
        varianteDAO.actualizarVariante(id, codigo, idTamanio, idProducto, precio, imagen, stock, cantidad);
    }

    public Variante obtenerVariantePorId(int id) throws SQLException {
        return varianteDAO.obtenerVariantePorId(id);
    }

    public int obtenerIdProductoPorVariante(int idVariante) throws SQLException {
        return varianteDAO.obtenerIdProductoPorVariante(idVariante);
    }
}
