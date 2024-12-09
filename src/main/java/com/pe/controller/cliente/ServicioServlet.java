package com.pe.controller.cliente;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/cliente/servicio")
public class ServicioServlet extends BaseClientServlet{

    @Override
    protected String getContentPage() {
        return "/servicios.html";
    }
}
