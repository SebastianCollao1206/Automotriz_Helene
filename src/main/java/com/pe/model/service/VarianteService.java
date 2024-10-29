package com.pe.model.service;

import com.pe.model.dao.VarianteDAO;
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
        cargarVariantes();
    }

    // Cargar todas las variantes
    public void cargarVariantes() throws SQLException {
        // Aquí puedes cargar todas las variantes si es necesario
        // varianteDAO.cargarVariantes(variantes); // Descomentar si tienes un método para cargar variantes
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

    // Metodo para buscar variantes por filtros (puedes agregar más filtros según sea necesario)
    public TreeSet<Variante> filtrarVariantes(String codigo, String tamaño, BigDecimal precioMin, BigDecimal precioMax) {
        TreeSet<Variante> variantesFiltradas = new TreeSet<>(Variante.VARIANTE_COMPARATOR_NATURAL_ORDER);
        for (Variante variante : variantes) {
            if (verificarVariante(variante, codigo, tamaño, precioMin, precioMax)) {
                variantesFiltradas.add(variante);
            }
        }
        return variantesFiltradas;
    }

    public void actualizarStock(int idVariante, int nuevoStock) throws SQLException {
        try {
            varianteDAO.actualizarStock(idVariante, nuevoStock);
        } catch (SQLException e) {
            // Manejo de excepciones
            throw new SQLException("Error en el servicio al actualizar el stock: " + e.getMessage(), e);
        }
    }

    // Verificar si la variante cumple con los filtros
    private boolean verificarVariante(Variante variante, String codigo, String tamaño, BigDecimal precioMin, BigDecimal precioMax) {
        boolean valido = true;

        // Filtrar por código
        if (codigo != null && !codigo.isEmpty() && !variante.getCodigo().toLowerCase().contains(codigo.toLowerCase())) {
            valido = false;
        }

        // Filtrar por tamaño
        if (tamaño != null && !tamaño.isEmpty() && variante.getIdTamanio() != Integer.parseInt(tamaño)) {
            valido = false;
        }

        // Filtrar por rango de precios
        if (precioMin != null && variante.getPrecio().compareTo(precioMin) < 0) {
            valido = false;
        }
        if (precioMax != null && variante.getPrecio().compareTo(precioMax) > 0) {
            valido = false;
        }

        return valido;
    }
}
