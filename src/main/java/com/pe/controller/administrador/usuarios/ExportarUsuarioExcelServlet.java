package com.pe.controller.administrador.usuarios;

import com.pe.model.entidad.Usuario;
import com.pe.model.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeSet;

@WebServlet("/usuario/exportar-excel")
public class ExportarUsuarioExcelServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ExportarUsuarioExcelServlet.class);
    private final UsuarioService usuarioService;

    public ExportarUsuarioExcelServlet() throws SQLException {
        this.usuarioService = new UsuarioService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            usuarioService.cargarUsuarios();
            TreeSet<Usuario> usuarios = usuarioService.buscarUsuarios(null, null, null, null);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Usuarios");

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre", "Correo", "Fecha de Registro", "DNI", "Tipo de Usuario", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Llenar datos
            int rowNum = 1;
            for (Usuario usuario : usuarios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(usuario.getNombre());
                row.createCell(1).setCellValue(usuario.getCorreo());
                row.createCell(2).setCellValue(usuario.getFechaRegistro().toString());
                row.createCell(3).setCellValue(usuario.getDni());
                row.createCell(4).setCellValue(usuario.getTipoUsuario().name());
                row.createCell(5).setCellValue(usuario.getEstado().name());
            }

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (SQLException e) {
            logger.error("Error al exportar usuarios a Excel: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error al exportar usuarios a Excel: " + e.getMessage());
        }
    }
}
