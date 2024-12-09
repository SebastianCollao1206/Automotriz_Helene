package com.pe.controller.administrador.reportes;

import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.service.ProductoService;
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

@WebServlet("/productos-mas-vendidos")
public class TopProductosServlet extends HttpServlet {

    private final ProductoService productoService;

    public TopProductosServlet() throws SQLException {
        this.productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Producto> productosMasVendidos = productoService.obtenerProductosMasVendidosDeMes();

            JSONObject json = new JSONObject();
            JSONArray labelsArray = new JSONArray();
            JSONArray dataArray = new JSONArray();

            String[] backgroundColors = {
                    "rgba(54, 162, 235, 0.8)",
                    "rgba(255, 99, 132, 0.8)",
                    "rgba(75, 192, 192, 0.8)",
                    "rgba(255, 206, 86, 0.8)"
            };

            String[] borderColors = {
                    "rgba(54, 162, 235, 1)",
                    "rgba(255, 99, 132, 1)",
                    "rgba(75, 192, 192, 1)",
                    "rgba(255, 206, 86, 1)"
            };

            JSONArray backgroundColorsArray = new JSONArray();
            JSONArray borderColorsArray = new JSONArray();

            for (int i = 0; i < productosMasVendidos.size(); i++) {
                Producto producto = productosMasVendidos.get(i);
                labelsArray.put(producto.getNombre());
                dataArray.put(generateRandomQuantity(100, 500));

                backgroundColorsArray.put(backgroundColors[i % backgroundColors.length]);
                borderColorsArray.put(borderColors[i % borderColors.length]);
            }

            JSONObject dataset = new JSONObject();
            dataset.put("data", dataArray);
            dataset.put("backgroundColor", backgroundColorsArray);
            dataset.put("borderColor", borderColorsArray);
            dataset.put("borderWidth", 1);

            json.put("labels", labelsArray);
            json.put("datasets", new JSONArray().put(dataset));

            resp.getWriter().write(json.toString());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error al obtener productos más vendidos: " + e.getMessage());
        }
    }

    private int generateRandomQuantity(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
