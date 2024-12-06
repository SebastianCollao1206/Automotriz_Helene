package com.pe.model.administrador.service;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.model.administrador.entidad.Producto;
import com.pe.model.administrador.entidad.Variante;

import java.sql.SQLException;
import java.util.*;

public class ProductosRelacionadosService {
    private static final int MAX_PRODUCTOS_RELACIONADOS = 6;
    private static final int MAX_PRODUCTOS_OTRAS_CATEGORIAS = 4;

    private final MutableGraph<Integer> grafoCategoriasRelacionadas;
    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final VarianteService varianteService;

    public ProductosRelacionadosService(CategoriaService categoriaService,
                                        ProductoService productoService,
                                        VarianteService varianteService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
        this.varianteService = varianteService;
        this.grafoCategoriasRelacionadas = GraphBuilder.directed().build();
        inicializarGrafo();
    }

    private void inicializarGrafo() {
        try {
            List<Categoria> categoriasDisponibles = obtenerCategoriasDisponibles();
            construirGrafoCircular(categoriasDisponibles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Categoria> obtenerCategoriasDisponibles() throws SQLException {
        List<Categoria> categoriasDisponibles = new ArrayList<>();
        TreeSet<Categoria> categorias = categoriaService.getCategorias();

        for (Categoria categoria : categorias) {
            if (productoService.tieneCategoriaProductosDisponibles(categoria, productoService, varianteService)) {
                categoriasDisponibles.add(categoria);
            }
        }
        return categoriasDisponibles;
    }

    private void construirGrafoCircular(List<Categoria> categorias) {
        if (categorias.isEmpty()) {
            return;
        }

        for (int i = 0; i < categorias.size(); i++) {
            Categoria actual = categorias.get(i);
            grafoCategoriasRelacionadas.addNode(actual.getIdCategoria());

            //conexion en bucle
            int siguienteIndex = (i + 1) % categorias.size();
            Categoria siguiente = categorias.get(siguienteIndex);
            grafoCategoriasRelacionadas.addNode(siguiente.getIdCategoria());
            grafoCategoriasRelacionadas.putEdge(actual.getIdCategoria(), siguiente.getIdCategoria());
        }
    }

    public List<Producto> obtenerProductosRelacionados(int categoriaId, int productoActualId) throws SQLException {
        List<Producto> productosRelacionados = new ArrayList<>();

        agregarProductosOtrasCategorias(categoriaId, productosRelacionados);
        agregarProductosMismaCategoria(categoriaId, productoActualId, productosRelacionados);

        return productosRelacionados;
    }

    private void agregarProductosOtrasCategorias(int categoriaId, List<Producto> productosRelacionados) throws SQLException {
        Set<Integer> categoriasRelacionadas = obtenerCategoriasRelacionadas(categoriaId);

        for (Integer idCategoria : categoriasRelacionadas) {
            if (idCategoria == categoriaId) {
                continue;
            }

            List<Producto> productos = new ArrayList<>(productoService.obtenerProductosPorCategoria(idCategoria));
            for (Producto producto : productos) {
                if (productoService.tieneVariantesDisponibles(producto, varianteService)) {
                    productosRelacionados.add(producto);
                    if (productosRelacionados.size() >= MAX_PRODUCTOS_OTRAS_CATEGORIAS) {
                        return;
                    }
                }
            }
        }
    }

    private Set<Integer> obtenerCategoriasRelacionadas(int categoriaId) {
        Set<Integer> categoriasRelacionadas = new HashSet<>();
        categoriasRelacionadas.add(categoriaId);

        if (grafoCategoriasRelacionadas.nodes().contains(categoriaId)) {
            Set<Integer> siguientesCategorias = grafoCategoriasRelacionadas.successors(categoriaId);
            categoriasRelacionadas.addAll(siguientesCategorias);
        }

        return categoriasRelacionadas;
    }

    private void agregarProductosMismaCategoria(int categoriaId, int productoActualId,
                                                List<Producto> productosRelacionados) throws SQLException {
        List<Producto> productosMismaCategoria = new ArrayList<>(productoService.obtenerProductosPorCategoria(categoriaId));

        for (Producto producto : productosMismaCategoria) {
            if (producto.getIdProducto() != productoActualId &&
                    productoService.tieneVariantesDisponibles(producto, varianteService)) {
                productosRelacionados.add(producto);
                if (productosRelacionados.size() >= MAX_PRODUCTOS_RELACIONADOS) {
                    return;
                }
            }
        }
    }

    public Variante obtenerPrimeraVarianteDisponible(Producto producto) throws SQLException {
        List<Variante> variantes = new ArrayList<>(varianteService.obtenerVariantesPorProducto(producto.getIdProducto()));
        for (Variante variante : variantes) {
            if (variante.getStock() > 0) {
                return variante;
            }
        }
        return null;
    }
}
