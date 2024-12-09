package com.pe.controller.cliente;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/cliente/contacto")
public class ContactoServlet extends BaseClientServlet{
    @Override
    protected String getContentPage() {
        return "/contacto.html";
    }
}
