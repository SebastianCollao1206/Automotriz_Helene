package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.ComentarioDAO;
import com.pe.model.administrador.entidad.Comentario;
import com.pe.model.administrador.entidad.Usuario;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.model.cliente.service.ClienteService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.TreeSet;

public class ComentarioService {
    private final ComentarioDAO comentarioDAO;
    private final TreeSet<Comentario> comentarios;

    public ComentarioService() throws SQLException {
        this.comentarioDAO = new ComentarioDAO();
        this.comentarios = new TreeSet<>(Comentario.COMENTARIO_COMPARATOR_NATURAL_ORDER);
        comentarioDAO.cargarComentarios(this.comentarios);
    }

    public TreeSet<Comentario> getComentarios() {
        return comentarios;
    }

    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public void cargarComentarios() throws SQLException {
        comentarioDAO.cargarComentarios(comentarios);
    }

    public void cambiarEstadoComentario(int id, boolean activar) throws SQLException {
        Comentario comentario = obtenerComentarioPorId(id);
        if (comentario != null) {
            if (activar) {
                if (comentario.getEstado() == Comentario.EstadoComentario.Inactivo) {
                    comentario.setEstado(Comentario.EstadoComentario.Activo);
                    comentarioDAO.actualizarComentario(comentario);
                }
            } else {
                if (comentario.getEstado() == Comentario.EstadoComentario.Activo) {
                    comentario.setEstado(Comentario.EstadoComentario.Inactivo);
                    comentarioDAO.actualizarComentario(comentario);
                }
            }
        } else {
            throw new SQLException("El comentario no existe.");
        }
    }

    public Comentario obtenerComentarioPorId(int id) {
        return comentarios.stream()
                .filter(comentario -> comentario.getIdComentario() == id)
                .findFirst()
                .orElse(null);
    }

    public TreeSet<Comentario> buscarComentarios(String estadoParam, String fechaInicioParam, String fechaFinParam) {
        LocalDate fechaInicio = (fechaInicioParam != null && !fechaInicioParam.isEmpty()) ? LocalDate.parse(fechaInicioParam) : null;
        LocalDate fechaFin = (fechaFinParam != null && !fechaFinParam.isEmpty()) ? LocalDate.parse(fechaFinParam) : null;
        Comentario.EstadoComentario estado = (estadoParam != null && !estadoParam.isEmpty()) ? Comentario.EstadoComentario.valueOf(estadoParam) : null;

        TreeSet<Comentario> comentariosFiltrados = new TreeSet<>(Comentario.COMENTARIO_COMPARATOR_NATURAL_ORDER);
        for (Comentario comentario : comentarios) {
            if (verificarComentario(comentario, estado, fechaInicio, fechaFin)) {
                comentariosFiltrados.add(comentario);
            }
        }
        return comentariosFiltrados;
    }

    private boolean verificarComentario(Comentario comentario, Comentario.EstadoComentario estado, LocalDate fechaInicio, LocalDate fechaFin) {
        boolean valido = true;

        if (estado != null && !comentario.getEstado().name().equals(estado.name())) {
            valido = false;
        }
        if (fechaInicio != null && fechaFin != null) {
            if (comentario.getFecha().isBefore(fechaInicio) || comentario.getFecha().isAfter(fechaFin)) {
                valido = false;
            }
        } else if (fechaInicio != null) {
            if (!comentario.getFecha().isEqual(fechaInicio)) {
                valido = false;
            }
        } else if (fechaFin != null) {
            if (!comentario.getFecha().isEqual(fechaFin)) {
                valido = false;
            }
        }

        return valido;
    }

    public TreeSet<Comentario> cargarComentariosActivos() {
        TreeSet<Comentario> comentariosActivos = new TreeSet<>(Comentario.COMENTARIO_COMPARATOR_NATURAL_ORDER);
        for (Comentario comentario : comentarios) {
            if (comentario.getEstado() == Comentario.EstadoComentario.Activo) {
                comentariosActivos.add(comentario);
            }
        }
        return comentariosActivos;
    }

    public TreeSet<String> getEstadosComentarioSet() {
        TreeSet<String> estadosComentario = new TreeSet<>();
        for (Comentario.EstadoComentario estado : Comentario.EstadoComentario.values()) {
            estadosComentario.add(estado.name());
        }
        return estadosComentario;
    }

    public Cliente obtenerClientePorComentario(Comentario comentario, ClienteService clienteService) {
        if (comentario == null) {
            return null;
        }
        int idCliente = comentario.getIdCliente();

        return clienteService.getClientes().stream()
                .filter(cliente -> cliente.getIdCliente() == idCliente)
                .findFirst()
                .orElse(null);
    }

}
