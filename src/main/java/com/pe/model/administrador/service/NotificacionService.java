package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.DetalleNotificacionDAO;
import com.pe.model.administrador.dao.NotificacionDAO;
import com.pe.model.administrador.entidad.notificaciones.DetalleNotificacion;
import com.pe.model.administrador.entidad.notificaciones.Notificacion;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionService {

    private NotificacionDAO notificacionDAO;
    private DetalleNotificacionDAO detalleNotificacionDAO;
    private final List<Notificacion> notificaciones;
    private final List<DetalleNotificacion> detallesNotificacion;

    public NotificacionService() throws SQLException {
        this.notificacionDAO = new NotificacionDAO();
        this.detalleNotificacionDAO = new DetalleNotificacionDAO();
        this.notificaciones = new ArrayList<>();
        this.detallesNotificacion = new ArrayList<>();
        cargarNotificaciones();
    }

    public void cargarNotificaciones() throws SQLException {
        try {
            notificacionDAO.cargarNotificaciones(notificaciones);
            detalleNotificacionDAO.cargarDetallesNotificacion(detallesNotificacion);
        } finally {

        }
    }

    public List<Notificacion> obtenerTodasLasNotificaciones(){
        return new ArrayList<>(notificaciones);
    }

    public void marcarTodasNotificacionesComoLeidas(int usuarioId) throws SQLException {
        detalleNotificacionDAO.marcarTodasNotificacionesComoLeidas(usuarioId);
    }

    public int contarNotificacionesNoLeidas(int usuarioId) throws SQLException {
        cargarNotificaciones();
        return notificacionDAO.contarNotificacionesNoLeidasPorUsuario(usuarioId);
    }

    public void generarNotificacionStock(Notificacion notificacion) throws SQLException {
        try {
            int idNotificacion = notificacionDAO.insertarNotificacion(notificacion);

            UsuarioService usuarioService = new UsuarioService();
            List<Integer> usuarioIds = usuarioService.obtenerTodosLosUsuarioIds();

            detalleNotificacionDAO.insertarDetallesNotificacion(idNotificacion, usuarioIds);
            cargarNotificaciones();
        } finally {

        }
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public List<DetalleNotificacion> getDetallesNotificacion() {
        return detallesNotificacion;
    }
}
