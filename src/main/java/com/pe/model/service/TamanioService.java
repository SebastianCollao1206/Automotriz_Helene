package com.pe.model.service;

import com.pe.model.dao.TamanioDAO;
import com.pe.model.entidad.Tamanio;
import com.pe.model.entidad.Usuario;

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

    public void agregarTamanio(Tamanio tamanio) throws SQLException {
        tamanioDAO.agregarTamanio(tamanio);
        cargarTamanios();
    }

    public void cargarTamanios() throws SQLException {
        tamanioDAO.cargarTamanios(tamanios);
    }

    public void eliminarTamanio(int id) throws SQLException {
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
        Tamanio tamanio = new Tamanio();
        tamanio.setUnidadMedida(unidadMedida);
        tamanio.setEstado(estado);

        tamanioDAO.agregarTamanio(tamanio);
        cargarTamanios();
    }

    // Método para obtener un tamaño por ID
    public Tamanio obtenerTamanioPorId(int id) throws SQLException {
        return tamanioDAO.obtenerTamanioPorId(id);
    }

    public void actualizarTamanio(int id, String unidadMedida, String estado) throws SQLException {
        Tamanio tamanio = obtenerTamanioPorId(id);
        if (tamanio != null) {
            tamanio.setUnidadMedida(unidadMedida);
            tamanio.setEstado(Tamanio.EstadoTamanio.valueOf(estado));

            tamanioDAO.actualizarTamanio(tamanio);
            cargarTamanios();
        } else {
            throw new SQLException("Tamaño no encontrado");
        }
    }

    // Métodos para buscar tamaños
    public TreeSet<Tamanio> buscarTamanios(String unidadMedida, Tamanio.EstadoTamanio estado) {
        TreeSet<Tamanio> tamaniosFiltrados = new TreeSet<>(Tamanio.TAMANIO_COMPARATOR_NATURAL_ORDER);
        for (Tamanio tamanio : tamanios) {
            if (verificarTamanio(tamanio, unidadMedida, estado)) {
                tamaniosFiltrados.add(tamanio);
            }
        }
        return tamaniosFiltrados;
    }

    // Verificar si el tamaño cumple con los filtros
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

    // Metodo para obtener nombres de tamaños
    public TreeSet<String> getNombresTamanios() {
        TreeSet<String> nombresTamanios = new TreeSet<>();
        for (Tamanio tamanio : tamanios) {
            nombresTamanios.add(tamanio.getUnidadMedida());
        }
        return nombresTamanios;
    }

    public TreeSet<Tamanio> getTamanios() {
        return tamanios;
    }
}
