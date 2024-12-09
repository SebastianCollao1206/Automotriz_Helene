package com.pe.controller.administrador.reportes;

import com.pe.controller.administrador.BaseServlet;
import com.pe.model.administrador.entidad.PermisoUsuario;
import jakarta.servlet.annotation.WebServlet;

import java.sql.SQLException;

@WebServlet("/reportes/pagina")
public class ReportesServlet extends BaseServlet {

    public ReportesServlet() throws SQLException{
    }

    @Override
    protected String getContentPage() {
        return "/reportes.html";
    }

    @Override
    protected PermisoUsuario getPermiso() {
        return PermisoUsuario.TODOS;
    }
}
