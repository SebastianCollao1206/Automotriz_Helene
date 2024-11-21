package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.SliderDAO;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Slider;
import com.pe.model.administrador.entidad.Variante;
import com.pe.util.Validaciones;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeSet;

public class SliderService {
    private final SliderDAO sliderDAO;
    private final TreeSet<Slider> sliders;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/html/uploads";
    private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(UPLOAD_DIRECTORY);

    public SliderService() throws SQLException {
        this.sliderDAO = new SliderDAO();
        this.sliders = new TreeSet<>(Slider.SLIDER_COMPARATOR_NATURAL_ORDER);
        sliderDAO.cargarSliders(this.sliders);
    }

    public static String getUploadDirectory() {
        return UPLOAD_DIRECTORY;
    }

    public static MultipartConfigElement getMultipartConfig() {
        return MULTI_PART_CONFIG;
    }

    public void agregarSlider(Slider slider) throws SQLException {
        sliderDAO.agregarSlider(slider);
        cargarSliders();
    }

    public void cargarSliders() throws SQLException {
        sliderDAO.cargarSliders(sliders);
        verificarYActualizarEstadosSliders();
    }

    public void agregarSlider(String titulo, String eslogan, LocalDate fechaInicio, String imagen, LocalDate fechaFin, Slider.EstadoSlider estado, int idVariante, String relacionarCon) throws SQLException {
        validarDatosSlider(titulo, eslogan, fechaInicio, fechaFin);
        Slider.EstadoSlider estadoInicial = calcularEstadoSegunFechas(fechaInicio, fechaFin);

        Slider slider = new Slider();
        slider.setTitulo(titulo);
        slider.setEslogan(eslogan);
        slider.setFechaInicio(fechaInicio);
        slider.setImagen(imagen);
        slider.setFechaFin(fechaFin);
        slider.setEstado(estadoInicial);
        slider.setIdVariante(idVariante);
        slider.setRelacionarCon(relacionarCon);

        sliderDAO.agregarSlider(slider);
        cargarSliders();
    }

    public Slider obtenerSliderPorId(int id) {
        return sliders.stream()
                .filter(slider -> slider.getIdSlider() == id)
                .findFirst()
                .orElse(null);
    }

    public void actualizarSlider(int id, String titulo, String eslogan, String imagen, LocalDate fechaInicio, LocalDate fechaFin, Slider.EstadoSlider nuevoEstado) throws SQLException {
        Slider slider = obtenerSliderPorId(id);
        if (slider != null) {
            validarDatosSlider(titulo, eslogan, fechaInicio, fechaFin);

            actualizarDatosBasicos(slider, titulo, eslogan, imagen, fechaInicio, fechaFin);

            Slider.EstadoSlider estadoCalculado = calcularEstadoSegunFechas(fechaInicio, fechaFin);

            if (estadoCalculado == Slider.EstadoSlider.Inactivo) {
                slider.setEstado(Slider.EstadoSlider.Inactivo);
            } else {
                slider.setEstado(nuevoEstado);
            }
            sliderDAO.actualizarSlider(slider);
            cargarSliders();
        } else {
            throw new SQLException("Slider no encontrado");
        }
    }

    private void validarDatosSlider(String titulo, String eslogan, LocalDate fechaInicio, LocalDate fechaFin) {
        Validaciones.validarDescripcion(titulo);
        Validaciones.validarDescripcion(eslogan);
        Validaciones.validarFechaInicio(fechaInicio);
        Validaciones.validarFechas(fechaInicio, fechaFin);
    }

    private void actualizarDatosBasicos(Slider slider, String titulo, String eslogan, String imagen, LocalDate fechaInicio, LocalDate fechaFin) {
        if (!slider.getTitulo().equals(titulo)) {
            slider.setTitulo(titulo);
        }
        if (!slider.getEslogan().equals(eslogan)) {
            slider.setEslogan(eslogan);
        }
        if (imagen != null && !imagen.isEmpty() && !imagen.equals(slider.getImagen())) {
            slider.setImagen(imagen);
        }
        slider.setFechaInicio(fechaInicio);
        slider.setFechaFin(fechaFin);
    }

    private Slider.EstadoSlider calcularEstadoSegunFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDate hoy = LocalDate.now();

        if (fechaInicio.isAfter(hoy) || fechaFin.isBefore(hoy)) {
            return Slider.EstadoSlider.Inactivo;
        }

        return Slider.EstadoSlider.Activo;
    }

    public void eliminarSlider(int idSlider) throws SQLException {
        Slider slider = obtenerSliderPorId(idSlider);
        if (slider != null) {
            sliderDAO.eliminarSlider(idSlider);
            cargarSliders();
        } else {
            throw new SQLException("Slider no encontrado");
        }
    }

    public String agregarSliderConRelacion(String titulo, String eslogan, LocalDate fechaInicio, String imagen, LocalDate fechaFin, String estadoStr, String relacionarCon, String idOpcionSeleccionada) throws SQLException {
        int idVariante = -1;
        Slider.EstadoSlider estado = Slider.EstadoSlider.valueOf(estadoStr != null && !estadoStr.isEmpty() ? estadoStr : "Activo");

        VarianteService varianteService = new VarianteService();

        if ("variante".equals(relacionarCon)) {
            idVariante = Integer.parseInt(idOpcionSeleccionada);
            if (existeSliderParaVariante(idVariante)) {
                throw new IllegalArgumentException("Ya existe un slider para esta variante.");
            }
            Variante variante = varianteService.obtenerVariantePorId(idVariante);
            if (imagen == null || imagen.isEmpty()) {
                imagen = variante.getImagen();
            }

        }else if ("producto".equals(relacionarCon)) {
            int idProducto = Integer.parseInt(idOpcionSeleccionada);
            if (existeSliderParaProducto(idProducto)) {
                throw new IllegalArgumentException("Ya existe un slider para este producto.");
            }

            boolean tieneVariantes = false;
            try {
                TreeSet<Variante> variantesDeProducto = varianteService.obtenerVariantesPorProducto(idProducto);
                tieneVariantes = !variantesDeProducto.isEmpty();
            } finally {
            }

            if (!tieneVariantes) {
                throw new IllegalArgumentException("El producto seleccionado no tiene variantes, por favor seleccione otro producto.");
            }

            Variante variante = varianteService.obtenerVarianteAleatoriaPorProducto(idProducto);
            if (variante != null) {
                idVariante = variante.getIdVariante();
                if (imagen == null || imagen.isEmpty()) {
                    imagen = variante.getImagen();
                }
            } else {
                throw new IllegalArgumentException("No se encontró una variante para el producto seleccionado.");
            }
        } else if ("categoria".equals(relacionarCon)) {
            int idCategoria = Integer.parseInt(idOpcionSeleccionada);

            CategoriaService categoriaService = new CategoriaService();
            TreeSet<Categoria> categoriasActivas = categoriaService.cargarCategoriasActivas();

            if (categoriasActivas.stream().noneMatch(categoria -> categoria.getIdCategoria() == idCategoria)) {
                throw new IllegalArgumentException("La categoría seleccionada está inactiva.");
            }

            if (existeSliderParaCategoria(idCategoria)) {
                throw new IllegalArgumentException("Ya existe un slider para esta categoría.");
            }

            ProductoService productoService = new ProductoService();
            TreeSet<Producto> productosDeCategoria = productoService.obtenerProductosPorCategoria(idCategoria);

            if (productosDeCategoria.isEmpty()) {
                throw new IllegalArgumentException("La categoría no tiene productos, por favor seleccione otra.");
            }

            boolean tieneVariantes = productosDeCategoria.stream().anyMatch(producto -> {
                try {
                    TreeSet<Variante> variantesDeProducto = varianteService.obtenerVariantesPorProducto(producto.getIdProducto());
                    return !variantesDeProducto.isEmpty();
                } finally {

                }
            });

            if (!tieneVariantes) {
                throw new IllegalArgumentException("La categoría no tiene productos con variantes, por favor seleccione otra.");
            }

            Variante variante = varianteService.obtenerVariantePorCategoria(idCategoria);
            if (variante != null) {
                idVariante = variante.getIdVariante();
                if (imagen == null || imagen.isEmpty()) {
                    imagen = variante.getImagen();
                }
            } else {
                throw new IllegalArgumentException("No se encontró una variante para la categoría seleccionada.");
            }
        } else {
            throw new IllegalArgumentException("Tipo de relación no válido.");
        }
        agregarSlider(titulo, eslogan, fechaInicio, imagen, fechaFin, estado, idVariante, relacionarCon);
        return "Slider agregado exitosamente!";
    }


    public boolean existeSliderParaVariante(int idVariante) {
        return sliders.stream().anyMatch(slider -> slider.getIdVariante() == idVariante);
    }

    public boolean existeSliderParaProducto(int idProducto) throws SQLException {
        VarianteService varianteService = new VarianteService();
        TreeSet<Variante> variantes = varianteService.obtenerVariantesPorProducto(idProducto);
        return variantes.stream().anyMatch(variante -> existeSliderParaVariante(variante.getIdVariante()));
    }

    public boolean existeSliderParaCategoria(int idCategoria) throws SQLException{
        ProductoService productoService = new ProductoService();
        Producto producto = productoService.obtenerProductoPorCategoria(idCategoria);
        if (producto != null) {
            return existeSliderParaProducto(producto.getIdProducto());
        }
        return false;
    }

    public String manejarCargaDeImagen(HttpServletRequest request) throws IOException, ServletException {
        String imagen = null;
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            if ("imagen".equals(part.getName()) && part.getSize() > 0) {
                String fileName = part.getSubmittedFileName();
                File uploadDir = new File(UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                File file = new File(uploadDir, fileName);
                try (InputStream fileContent = part.getInputStream()) {
                    Files.copy(fileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                imagen = "uploads/" + fileName;
            }
        }
        return imagen;
    }

    public TreeSet<Slider> buscarSliders(String relacionarCon, Slider.EstadoSlider estado) {
        TreeSet<Slider> slidersFiltrados = new TreeSet<>(Slider.SLIDER_COMPARATOR_NATURAL_ORDER);
        for (Slider slider : sliders) {
            if (verificarSlider(slider, relacionarCon, estado)) {
                slidersFiltrados.add(slider);
            }
        }
        return slidersFiltrados;
    }

    private boolean verificarSlider(Slider slider, String relacionarCon, Slider.EstadoSlider estado) {
        boolean valido = true;

        if (relacionarCon != null && !relacionarCon.isEmpty() && !slider.getRelacionarCon().equalsIgnoreCase(relacionarCon)) {
            valido = false;
        }
        if (estado != null && !slider.getEstado().equals(estado)) {
            valido = false;
        }
        return valido;
    }

    public void verificarYActualizarEstadosSliders() throws SQLException {
        LocalDate fechaActual = LocalDate.now();
        boolean huboActualizaciones = false;

        for (Slider slider : sliders) {
            Slider.EstadoSlider nuevoEstado = calcularEstadoSegunFechas(
                    slider.getFechaInicio(),
                    slider.getFechaFin()
            );

            if (slider.getEstado() != nuevoEstado) {
                slider.setEstado(nuevoEstado);
                sliderDAO.actualizarSlider(slider);
                huboActualizaciones = true;
            }
        }
        if (huboActualizaciones) {
            cargarSliders();
        }
    }

    public TreeSet<String> getEstadosSliderSet() {
        TreeSet<String> estadosSlider = new TreeSet<>();
        for (Slider.EstadoSlider estado : Slider.EstadoSlider.values()) {
            estadosSlider.add(estado.name());
        }
        return estadosSlider;
    }

    public TreeSet<Slider> cargarSlidersActivos() {
        TreeSet<Slider> slidersActivos = new TreeSet<>(Slider.SLIDER_COMPARATOR_NATURAL_ORDER);
        for (Slider slider : sliders) {
            if (slider.getEstado() == Slider.EstadoSlider.Activo) {
                slidersActivos.add(slider);
            }
        }
        return slidersActivos;
    }

    public static String generarEnlaceVerMas(Slider slider, VarianteService varianteService, ProductoService productoService, CategoriaService categoriaService) throws SQLException {
        int idDestino = -1;
        switch (slider.getRelacionarCon()) {
            case "variante":
                idDestino = slider.getIdVariante();
                return "/cliente/variante?id=" + idDestino;
            case "producto":
                idDestino = varianteService.obtenerIdProductoPorVariante(slider.getIdVariante());
                return "/cliente/producto?id=" + idDestino;
            case "categoria":
                Producto producto = productoService.obtenerProductoPorCategoria(
                        varianteService.obtenerVariantePorId(slider.getIdVariante()).getIdProducto()
                );

                if (producto != null) {
                    idDestino = producto.getIdCategoria();
                    return "/cliente/categoria?id=" + idDestino;
                }
                return "#";
            default:
                return "#";
        }
    }
}
