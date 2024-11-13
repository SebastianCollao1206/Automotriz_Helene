package com.pe.controller.cliente;

import com.pe.controller.administrador.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/cliente/")
public class IndexClienteServlet extends BaseClientServlet {
    private static final Logger logger = LoggerFactory.getLogger(IndexClienteServlet.class);

    @Override
    protected String getContentPage() {
        return "/index.html";
    }

//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        logger.info("Accediendo a la página de inicio del cliente.");
//
//        super.doGet(request, response);
//    }
}
