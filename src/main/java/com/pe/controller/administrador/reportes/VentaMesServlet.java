package com.pe.controller.administrador.reportes;

import com.pe.model.administrador.service.CompraService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/ventas-por-mes")
public class VentaMesServlet extends HttpServlet {
    private final CompraService compraService;

    public VentaMesServlet() throws SQLException {
        this.compraService = new CompraService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Object[]> ventasPorMes = compraService.obtenerVentasPorMes();

            JSONObject json = new JSONObject();
            JSONArray labelsArray = new JSONArray();
            JSONArray dataArray = new JSONArray();

            for (Object[] venta : ventasPorMes) {
                String nombreMes = (String) venta[1];
                Double totalVentas = (Double) venta[2];

                labelsArray.put(nombreMes);
                dataArray.put(totalVentas);
            }

            JSONObject dataset = new JSONObject();
            dataset.put("label", "Ventas Mensuales");
            dataset.put("data", dataArray);
            dataset.put("backgroundColor", "rgba(54, 162, 235, 0.6)");
            dataset.put("borderColor", "rgba(54, 162, 235, 1)");
            dataset.put("borderWidth", 1);

            json.put("labels", labelsArray);
            json.put("datasets", new JSONArray().put(dataset));

            resp.getWriter().write(json.toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error al obtener ventas por mes: " + e.getMessage());
        }
    }
}
