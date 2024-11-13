package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.VarianteDAO;
import com.pe.model.administrador.entidad.Tamanio;
import com.pe.model.administrador.entidad.Variante;
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
        cargarVariantes();
    }

    public TreeSet<Variante> getVariantes() {
        return variantes;
    }

    public void cargarVariantes() throws SQLException {
        varianteDAO.cargarVariantes(variantes);
    }

    public void agregarVariantes(TreeSet<Variante> variantes, int productoId) throws SQLException {
        Validaciones.validarVariantes(variantes);
        for (Variante variante : variantes) {
            if (existeVariante(variante)) {
                throw new IllegalArgumentException("El codigo '"+variante.getCodigo() +"' ya está registrado. Porfavor ingrese otro");
            }
        }
        varianteDAO.agregarVariantes(variantes, productoId);
        cargarVariantes();
    }

    public void agregarVariante(Variante variante) throws SQLException {
        TreeSet<Variante> variantes = new TreeSet<>();
        variantes.add(variante);
        Validaciones.validarVariantes(variantes);
        if (existeVariante(variante)) {
            throw new IllegalArgumentException("El codigo '"+variante.getCodigo() +"' ya está registrado. Porfavor ingrese otro");
        }
        varianteDAO.agregarVariantes(variantes, variante.getIdProducto());
    }


    public TreeSet<Variante> obtenerVariantesPorProducto(int idProducto) {
        TreeSet<Variante> variantesPorProducto = new TreeSet<>(Variante.VARIANTE_COMPARATOR_NATURAL_ORDER);
        for (Variante variante : variantes) {
            if (variante.getIdProducto() == idProducto) {
                variantesPorProducto.add(variante);
            }
        }
        return variantesPorProducto;
    }

    public Tamanio obtenerTamanioPorId(int idTamanio) throws SQLException {
        return tamanioService.obtenerTamanioPorId(idTamanio);
    }


    public void actualizarStock(int idVariante, int nuevoStock) throws SQLException {
        try {
            varianteDAO.actualizarStock(idVariante, nuevoStock);
        } catch (SQLException e) {
            throw new SQLException("Error en el servicio al actualizar el stock: " + e.getMessage(), e);
        }
    }

    public void actualizarVariante(int id, String codigo, int idTamanio, int idProducto, BigDecimal precio, String imagen, int stock, int cantidad) throws SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de la variante no puede estar vacío.");
        }
        Variante varianteExistente = obtenerVariantePorId(id);
        if (varianteExistente == null) {
            throw new IllegalArgumentException("No se encontró la variante con ID: " + id);
        }
        Variante varianteTemporal = new Variante(id, codigo, precio, imagen, idTamanio, stock, cantidad, idProducto);
        TreeSet<Variante> variantesTemp = new TreeSet<>();
        variantesTemp.add(varianteTemporal);

        Validaciones.validarVariantes(variantesTemp);

        if (!varianteExistente.getCodigo().equalsIgnoreCase(codigo) && existeVariante(varianteTemporal)) {
            throw new IllegalArgumentException("Este codigo ya está registrado para otro producto.");
        }
        varianteDAO.actualizarVariante(id, codigo, idTamanio, idProducto, precio, imagen, stock, cantidad);
    }

    public Variante obtenerVariantePorId(int id) {
        return variantes.stream()
                .filter(variante -> variante.getIdVariante() == id)
                .findFirst()
                .orElse(null);
    }

    public int obtenerIdProductoPorVariante(int idVariante) {
        Variante variante = variantes.stream()
                .filter(v -> v.getIdVariante() == idVariante)
                .findFirst()
                .orElse(null);
        return variante != null ? variante.getIdProducto() : 0;
    }

    private boolean existeVariante(Variante variante) {
        return variantes.stream().anyMatch(v -> v.getCodigo().equalsIgnoreCase(variante.getCodigo()));
    }
}
