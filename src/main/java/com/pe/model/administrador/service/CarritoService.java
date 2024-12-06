package com.pe.model.administrador.service;

import com.pe.model.administrador.entidad.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.TreeSet;

public class CarritoService {

    public static void agregarAlCarrito(HttpServletRequest request, Variante variante, int cantidad) {
        HttpSession session = request.getSession(true);

        TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new TreeSet<>(ItemCarrito.ITEM_CARRITO_COMPARATOR_NATURAL_ORDER);
        }

        ItemCarrito nuevoItem = new ItemCarrito(
                variante.getImagen(),
                variante.getCodigo(),
                variante.getPrecio().doubleValue(),
                cantidad,
                variante.getIdVariante()
        );

        Optional<ItemCarrito> itemExistente = carrito.stream()
                .filter(item -> item.getIdVariante() == nuevoItem.getIdVariante())
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrito existente = itemExistente.get();
            carrito.remove(existente);
            nuevoItem.setCantidad(existente.getCantidad() + cantidad);
        }

        carrito.add(nuevoItem);

        int totalCantidad = carrito.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();

        session.setAttribute("carrito", carrito);
        session.setAttribute("cartCounter", totalCantidad);

    }

    public static void eliminarDelCarrito(HttpServletRequest request, int idVariante) {
        HttpSession session = request.getSession(true);
        TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) session.getAttribute("carrito");

        if (carrito != null) {
            carrito.removeIf(item -> item.getIdVariante() == idVariante);

            int totalCantidad = carrito.stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum();

            double subtotalCarrito = carrito.stream()
                    .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                    .sum();

            double igv = subtotalCarrito * 0.18;
            double totalCarrito = subtotalCarrito + igv;

            session.setAttribute("carrito", carrito);
            session.setAttribute("cartCounter", totalCantidad);
            session.setAttribute("totalCarrito", totalCarrito);
        } else {
            session.setAttribute("cartCounter", 0);
            session.setAttribute("totalCarrito", 0.0);
        }
    }

    public static boolean validarStock(VarianteService varianteService, int idVariante, int cantidad) {
        try {
            Variante variante = varianteService.obtenerVariantePorId(idVariante);
            return variante.getStock() >= cantidad;
        } catch (Exception e) {
            return false;
        }
    }

    public static Mensaje actualizarCantidadEnCarrito(HttpServletRequest request, VarianteService varianteService, int idVariante, int nuevaCantidad) {
        try {
            HttpSession session = request.getSession(true);
            TreeSet<ItemCarrito> carrito = (TreeSet<ItemCarrito>) session.getAttribute("carrito");

            if (carrito == null) {
                return new Mensaje("error", "Carrito no encontrado", null);
            }

            if (!validarStock(varianteService, idVariante, nuevaCantidad)) {
                return new Mensaje("error", "No hay suficiente stock", null);
            }

            carrito.stream()
                    .filter(item -> item.getIdVariante() == idVariante)
                    .findFirst()
                    .ifPresent(item -> {
                        carrito.remove(item);
                        item.setCantidad(nuevaCantidad);
                        carrito.add(item);
                    });

            double subtotalCarrito = carrito.stream()
                    .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                    .sum();

            double igv = subtotalCarrito * 0.18;
            double totalCarrito = subtotalCarrito + igv;

            int totalCantidad = carrito.stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum();

            session.setAttribute("carrito", carrito);
            session.setAttribute("cartCounter", totalCantidad);
            session.setAttribute("totalCarrito", totalCarrito);

            return new Mensaje("success", String.format("Cantidad actualizada a %d", nuevaCantidad), null);
        } catch (Exception e) {
            return new Mensaje("error", "Error al procesar la solicitud", null);
        }
    }
}
