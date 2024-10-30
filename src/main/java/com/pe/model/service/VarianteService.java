package com.pe.model.service;

import com.google.errorprone.annotations.Var;
import com.pe.model.dao.VarianteDAO;
import com.pe.model.entidad.Producto;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Variante;

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

    // Agregar una nueva variante
    public void agregarVariantes(TreeSet<Variante> variantes, int productoId) throws SQLException {
        varianteDAO.agregarVariantes(variantes, productoId);
    }

    public TreeSet<Variante> obtenerVariantesPorProducto(int idProducto) throws SQLException {
        return varianteDAO.obtenerVariantesPorProducto(idProducto);
    }

    // Obtener todas las variantes
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
            // Manejo de excepciones
            throw new SQLException("Error en el servicio al actualizar el stock: " + e.getMessage(), e);
        }
    }
    public int obtenerIdProductoPorVariante(int idVariante) throws SQLException {
        return varianteDAO.obtenerIdProductoPorVariante(idVariante);
    }
}
