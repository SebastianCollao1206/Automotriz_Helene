package com.pe.controller.administrador.reportes;

import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.service.CategoriaService;
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

@WebServlet("/top-categorias-vendidas")
public class TopCategoriasServlet extends HttpServlet {

    private final CategoriaService categoriaService;

    public TopCategoriasServlet() throws SQLException {
        this.categoriaService = new CategoriaService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<Categoria> categoriasMasVendidas = categoriaService.obtenerTopCategoriasMasVendidasDelMes();

            JSONObject json = new JSONObject();
            JSONArray labelsArray = new JSONArray();
            JSONArray dataArray = new JSONArray();

            String[] backgroundColors = {
                    "rgba(76, 175, 80, 0.6)",
                    "rgba(33, 150, 243, 0.6)",
                    "rgba(255, 152, 0, 0.6)",
                    "rgba(156, 39, 176, 0.6)",
                    "rgba(0, 188, 212, 0.6)"
            };

            String[] borderColors = {
                    "rgba(76, 175, 80, 1)",
                    "rgba(33, 150, 243, 1)",
                    "rgba(255, 152, 0, 1)",
                    "rgba(156, 39, 176, 1)",
                    "rgba(0, 188, 212, 1)"
            };

            JSONArray backgroundColorsArray = new JSONArray();
            JSONArray borderColorsArray = new JSONArray();

            for (int i = 0; i < categoriasMasVendidas.size(); i++) {
                Categoria categoria = categoriasMasVendidas.get(i);
                labelsArray.put(categoria.getNombre());
                dataArray.put(generateRandomQuantity(50, 300));

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
            resp.getWriter().write("Error al obtener categorías más vendidas: " + e.getMessage());
        }
    }

    private int generateRandomQuantity(int min, int max) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
