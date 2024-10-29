package com.pe.model.service;

import com.pe.model.dao.CategoriaDAO;
import com.pe.model.entidad.Categoria;
import com.pe.model.entidad.Usuario;

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

    public void agregarCategoria(Categoria categoria) {
        categorias.add(categoria);
    }

    public void cargarCategorias() throws SQLException {
        categoriaDAO.cargarCategorias(categorias);
    }

    public void eliminarCategoria(int id) throws SQLException {
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
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setEstado(estado);

        categoriaDAO.agregarCategoria(categoria);
        cargarCategorias();
    }

    // Metodo para obtener una categoría por ID
    public Categoria obtenerCategoriaPorId(int id) throws SQLException {
        return categoriaDAO.obtenerCategoriaPorId(id);
    }

    public void actualizarCategoria(int id, String nombre, String estado) throws SQLException {
        Categoria categoria = obtenerCategoriaPorId(id);
        if (categoria != null) {
            categoria.setNombre(nombre);
            categoria.setEstado(Categoria.EstadoCategoria.valueOf(estado));

            categoriaDAO.actualizarCategoria(categoria);
            cargarCategorias();
        } else {
            throw new SQLException("Categoría no encontrada");
        }
    }

    // Metodos para buscar categorías
    public TreeSet<Categoria> buscarCategorias(String nombre, Categoria.EstadoCategoria estado) {
        TreeSet<Categoria> categoriasFiltradas = new TreeSet<>(Categoria.CATEGORIA_COMPARATOR_NATURAL_ORDER);
        for (Categoria categoria : categorias) {
            if (verificarCategoria(categoria, nombre, estado)) {
                categoriasFiltradas.add(categoria);
            }
        }
        return categoriasFiltradas;
    }

    // Verificar si la categoría cumple con los filtros
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

    public TreeSet<Categoria> getCategorias() {
        return categorias;
    }
}
