package com.pe.model.administrador.service;

import com.pe.model.administrador.dao.CategoriaDAO;
import com.pe.model.administrador.entidad.Categoria;
import com.pe.util.Validaciones;

import java.sql.SQLException;
import java.util.TreeSet;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;
    private final TreeSet<Categoria> categorias;

    public CategoriaService() throws SQLException {
        this.categoriaDAO = new CategoriaDAO();
        this.categorias = new TreeSet<>(Categoria.CATEGORIA_COMPARATOR_NATURAL_ORDER);
        categoriaDAO.cargarCategorias(this.categorias);
    }

    public TreeSet<Categoria> getCategorias() {
        return categorias;
    }

    public void agregarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    public void cargarCategorias() throws SQLException {
        categoriaDAO.cargarCategorias(categorias);
    }

    public void eliminarCategoria(int id) throws SQLException {
        cargarCategorias();
        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria != null && categoria.getEstado() == Categoria.EstadoCategoria.Activo) {
            categoria.setEstado(Categoria.EstadoCategoria.Inactivo);
            categoriaDAO.actualizarCategoria(categoria);
            cargarCategorias();
        } else {
            throw new SQLException("La categoría no se puede cambiar a inactiva o no existe.");
        }
    }

    public void agregarCategoria(String nombre, Categoria.EstadoCategoria estado) throws Exception {
        Validaciones.validarSoloLetras(nombre);
        if (existeCategoria(nombre)) {
            throw new IllegalArgumentException("La categoria ya está registrada en el sistema");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setEstado(estado);

        categoriaDAO.agregarCategoria(categoria);
        cargarCategorias();
    }

    public Categoria obtenerCategoriaPorId(int id) {
        return categorias.stream()
                .filter(categoria -> categoria.getIdCategoria() == id)
                .findFirst()
                .orElse(null);
    }

    public void actualizarCategoria(int id, String nombre, String estado) throws SQLException {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria != null) {
            if (!categoria.getNombre().equals(nombre)) {
                Validaciones.validarSoloLetras(nombre);
                if (existeCategoria(nombre)) {
                    throw new IllegalArgumentException("La categoria ya está registrada en el sistema");
                }
                categoria.setNombre(nombre);
            }
            categoria.setEstado(Categoria.EstadoCategoria.valueOf(estado));
            categoriaDAO.actualizarCategoria(categoria);
            cargarCategorias();
        } else {
            throw new SQLException("Categoría no encontrada");
        }
    }

    public TreeSet<Categoria> buscarCategorias(String nombre, Categoria.EstadoCategoria estado) {
        TreeSet<Categoria> categoriasFiltradas = new TreeSet<>(Categoria.CATEGORIA_COMPARATOR_NATURAL_ORDER);
        for (Categoria categoria : categorias) {
            if (verificarCategoria(categoria, nombre, estado)) {
                categoriasFiltradas.add(categoria);
            }
        }
        return categoriasFiltradas;
    }

    private boolean verificarCategoria(Categoria categoria, String nombre, Categoria.EstadoCategoria estado) {
        boolean valido = true;
        if (nombre != null && !nombre.isEmpty() && !categoria.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
            valido = false;
        }
        if (estado != null && !categoria.getEstado().name().equals(estado.name())) {
            valido = false;
        }
        return valido;
    }

    public TreeSet<Categoria> cargarCategoriasActivas() {
        TreeSet<Categoria> categoriasActivas = new TreeSet<>(Categoria.CATEGORIA_COMPARATOR_NATURAL_ORDER);
        for (Categoria categoria : categorias) {
            if (categoria.getEstado() == Categoria.EstadoCategoria.Activo) {
                categoriasActivas.add(categoria);
            }
        }
        return categoriasActivas;
    }

    public TreeSet<String> getNombresCategorias() {
        TreeSet<String> nombresCategorias = new TreeSet<>();
        for (Categoria categoria : categorias) {
            nombresCategorias.add(categoria.getNombre());
        }
        return nombresCategorias;
    }

    private boolean existeCategoria(String nombre) {
        return categorias.stream()
                .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre.trim()));
    }
}
