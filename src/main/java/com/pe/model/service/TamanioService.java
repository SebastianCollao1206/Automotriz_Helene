package com.pe.model.service;

import com.pe.model.dao.TamanioDAO;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Usuario;
import com.pe.util.Validaciones;

import java.sql.SQLException;
import java.util.TreeSet;

public class TamanioService {
    private final TamanioDAO tamanioDAO;
    private final TreeSet<Tamanio> tamanios;

    public TamanioService() throws SQLException {
        this.tamanioDAO = new TamanioDAO();
        this.tamanios = new TreeSet<>(Tamanio.TAMANIO_COMPARATOR_NATURAL_ORDER);
        tamanioDAO.cargarTamanios(this.tamanios);
    }

    public TreeSet<Tamanio> getTamanios() {
        return tamanios;
    }

    public void agregarTamanio(Tamanio tamanio) throws SQLException {
        tamanioDAO.agregarTamanio(tamanio);
        cargarTamanios();
    }

    public void cargarTamanios() throws SQLException {
        tamanioDAO.cargarTamanios(tamanios);
    }

    public String obtenerNombreTamanioPorId(int id) {
        Tamanio tamanio = obtenerTamanioPorId(id);
        return tamanio != null ? tamanio.getUnidadMedida() : null;
    }

    public void eliminarTamanio(int id) throws SQLException {
        cargarTamanios();
        Tamanio tamanio = obtenerTamanioPorId(id);
        if (tamanio != null && tamanio.getEstado() == Tamanio.EstadoTamanio.Activo) {
            tamanio.setEstado(Tamanio.EstadoTamanio.Inactivo);
            tamanioDAO.actualizarTamanio(tamanio);
            cargarTamanios();
        } else {
            throw new SQLException("El tamaño no se puede cambiar a inactivo o no existe.");
        }
    }

    public void agregarTamanio(String unidadMedida, Tamanio.EstadoTamanio estado) throws SQLException {
        Validaciones.validarSoloLetras(unidadMedida);
        if (existeUnidadMedida(unidadMedida)) {
            throw new IllegalArgumentException("La unidad de medida ya está registrada en el sistema");
        }
        Tamanio tamanio = new Tamanio();
        tamanio.setUnidadMedida(unidadMedida.trim().toLowerCase());//ACA SE QUITA EL LOWER SI NO SE MANEJA MINUSCULAS
        tamanio.setEstado(estado);
        tamanioDAO.agregarTamanio(tamanio);
        cargarTamanios();
    }

    public Tamanio obtenerTamanioPorId(int id) {
        return tamanios.stream()
                .filter(t -> t.getIdTamanio() == id)
                .findFirst()
                .orElse(null);
    }

    public void actualizarTamanio(int id, String unidadMedida, String estado) throws SQLException {
        Validaciones.validarSoloLetras(unidadMedida);
        if (existeUnidadMedida(unidadMedida) && !unidadMedida.equals(obtenerTamanioPorId(id).getUnidadMedida())) {
            throw new IllegalArgumentException("La unidad de medida ya está registrada en el sistema");
        }
        Tamanio tamanio = obtenerTamanioPorId(id);
        if (tamanio != null) {
            tamanio.setUnidadMedida(unidadMedida.trim().toLowerCase());
            tamanio.setEstado(Tamanio.EstadoTamanio.valueOf(estado));

            tamanioDAO.actualizarTamanio(tamanio);
            cargarTamanios();
        } else {
            throw new SQLException("Tamaño no encontrado");
        }
    }

    public Tamanio.EstadoTamanio obtenerEstadoTamanio(String estadoStr) {
        if (estadoStr != null && !estadoStr.isEmpty()) {
            try {
                return Tamanio.EstadoTamanio.valueOf(estadoStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado de tamaño no válido: " + estadoStr);
            }
        }
        return null;
    }

    public TreeSet<Tamanio> buscarTamanios(String unidadMedida, Tamanio.EstadoTamanio estado) {
        TreeSet<Tamanio> tamaniosFiltrados = new TreeSet<>(Tamanio.TAMANIO_COMPARATOR_NATURAL_ORDER);
        for (Tamanio tamanio : tamanios) {
            if (verificarTamanio(tamanio, unidadMedida, estado)) {
                tamaniosFiltrados.add(tamanio);
            }
        }
        return tamaniosFiltrados;
    }

    private boolean verificarTamanio(Tamanio tamanio, String unidadMedida, Tamanio.EstadoTamanio estado) {
        boolean valido = true;
        if (unidadMedida != null && !unidadMedida.isEmpty() && !tamanio.getUnidadMedida().toLowerCase().contains(unidadMedida.toLowerCase())) {
            valido = false;
        }
        if (estado != null && !tamanio.getEstado().name().equals(estado.name())) {
            valido = false;
        }
        return valido;
    }

    public TreeSet<String> getNombresTamanios() {
        TreeSet<String> nombresTamanios = new TreeSet<>();
        for (Tamanio tamanio : tamanios) {
            nombresTamanios.add(tamanio.getUnidadMedida());
        }
        return nombresTamanios;
    }

    private boolean existeUnidadMedida(String unidadMedida) {
        return tamanios.stream()
                .anyMatch(t -> t.getUnidadMedida().equalsIgnoreCase(unidadMedida.trim()));
    }

    public TreeSet<Tamanio> cargarTamaniosActivos() {
        TreeSet<Tamanio> tamaniosActivos = new TreeSet<>(Tamanio.TAMANIO_COMPARATOR_NATURAL_ORDER);
        for (Tamanio tamanio : tamanios) {
            if (tamanio.getEstado() == Tamanio.EstadoTamanio.Activo) {
                tamaniosActivos.add(tamanio);
            }
        }
        return tamaniosActivos;
    }
}
