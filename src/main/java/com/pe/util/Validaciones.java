package com.pe.util;

import com.google.common.base.Preconditions;
import com.pe.model.administrador.entidad.Variante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Validaciones {

    // Constantes para longitudes
    private static final int LENGTH_DNI = 8;
    private static final int MAX_LENGTH_NOMBRE = 60;
    private static final int MAX_LENGTH_CORREO = 50;
    private static final int MIN_LENGTH_PASSWORD = 8;
    private static final int MAX_LENGTH_PASSWORD = 20;
    private static final int MAX_LENGTH_TELEFONO = 9;

    // Patrones de validación
    private static final Pattern PATRON_SOLO_NUMEROS = Pattern.compile("^[0-9]+$");
    private static final Pattern PATRON_SOLO_LETRAS = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    private static final Pattern PATRON_CORREO = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PATRON_CARACTERES_ESPECIALES = Pattern.compile("[<>\"'%;()&+]");
    private static final Pattern PATRON_CARACTERES_ESPECIALES_CON_PORCENTAJE = Pattern.compile("[<>\"'();&+]");
    private static final Pattern PATRON_INYECCION_SQL = Pattern.compile("(?i).*(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER|EXEC|--).*");
    private static final Pattern PATRON_PASSWORD_SEGURO = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    // Validaciones de documentos
    public static void validarDNI(String dni) {
        Preconditions.checkNotNull(dni, "El DNI no puede ser nulo");
        Preconditions.checkArgument(dni.length() == LENGTH_DNI, "El DNI debe tener exactamente 8 dígitos");
        Preconditions.checkArgument(PATRON_SOLO_NUMEROS.matcher(dni).matches(), "El DNI solo debe contener números");
        Preconditions.checkArgument(!contieneEspacios(dni), "El DNI no debe contener espacios en blanco");
    }

    // Validaciones de datos personales
    public static void validarNombre(String nombre) {
        Preconditions.checkNotNull(nombre, "El nombre no puede ser nulo");
        Preconditions.checkArgument(!nombre.isEmpty(), "El nombre no puede estar vacío");
        Preconditions.checkArgument(nombre.length() <= MAX_LENGTH_NOMBRE,
                "El nombre no puede exceder los " + MAX_LENGTH_NOMBRE + " caracteres");
        Preconditions.checkArgument(PATRON_SOLO_LETRAS.matcher(nombre).matches(),
                "El nombre solo debe contener letras y espacios");
        Preconditions.checkArgument(!contieneEspaciosMultiples(nombre),
                "El nombre no debe contener espacios múltiples");
    }

    public static void validarTelefono(String telefono) {
        Preconditions.checkNotNull(telefono, "El teléfono no puede ser nulo");
        Preconditions.checkArgument(telefono.length() == MAX_LENGTH_TELEFONO,
                "El teléfono debe tener exactamente 9 dígitos");
        Preconditions.checkArgument(PATRON_SOLO_NUMEROS.matcher(telefono).matches(),
                "El teléfono solo debe contener números");
        Preconditions.checkArgument(!contieneEspacios(telefono),
                "El teléfono no debe contener espacios en blanco");
    }

    // Validaciones de seguridad
    public static void validarCorreo(String correo) {
        Preconditions.checkNotNull(correo, "El correo no puede ser nulo");
        Preconditions.checkArgument(!correo.isEmpty(), "El correo no puede estar vacío");
        Preconditions.checkArgument(correo.length() <= MAX_LENGTH_CORREO,
                "El correo no puede exceder los " + MAX_LENGTH_CORREO + " caracteres");
        Preconditions.checkArgument(PATRON_CORREO.matcher(correo).matches(),
                "El formato del correo no es válido");
        Preconditions.checkArgument(!PATRON_CARACTERES_ESPECIALES.matcher(correo).find(),
                "El correo contiene caracteres no permitidos");
        Preconditions.checkArgument(!PATRON_INYECCION_SQL.matcher(correo).matches(),
                "Entrada no válida detectada en el correo");
        Preconditions.checkArgument(!contieneEspacios(correo),
                "El correo no debe contener espacios en blanco");
    }

    public static void validarPassword(String password) {
        Preconditions.checkNotNull(password, "La contraseña no puede ser nula");
        Preconditions.checkArgument(!password.isEmpty(), "La contraseña no puede estar vacía");
        Preconditions.checkArgument(password.length() >= MIN_LENGTH_PASSWORD,
                "La contraseña debe tener al menos " + MIN_LENGTH_PASSWORD + " caracteres");
        Preconditions.checkArgument(password.length() <= MAX_LENGTH_PASSWORD,
                "La contraseña no puede exceder los " + MAX_LENGTH_PASSWORD + " caracteres");
        Preconditions.checkArgument(PATRON_PASSWORD_SEGURO.matcher(password).matches(),
                "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial");
        Preconditions.checkArgument(!PATRON_CARACTERES_ESPECIALES.matcher(password).find(),
                "La contraseña contiene caracteres no permitidos");
        Preconditions.checkArgument(!PATRON_INYECCION_SQL.matcher(password).matches(),
                "Entrada no válida detectada en la contraseña");
        Preconditions.checkArgument(!contieneEspacios(password),
                "La contraseña no debe contener espacios en blanco");
    }

    // Utilidades de validación
    public static String sanitizarEntrada(String input) {
        if (input == null) return null;
        return input.trim()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    //Solo letras
    public static void validarSoloLetras(String texto) {
        Preconditions.checkNotNull(texto, "El texto no puede ser nulo");
        String textoSanitizado = sanitizarEntrada(texto).replaceAll("\\s+", "").toLowerCase();
        Preconditions.checkArgument(PATRON_SOLO_LETRAS.matcher(textoSanitizado).matches(),
                "El texto solo debe contener letras");
    }

    public static void validarDescripcion(String descripcion) {
        Preconditions.checkNotNull(descripcion, "La descripción no puede ser nula");
        Preconditions.checkArgument(!descripcion.isEmpty(), "La descripción no puede estar vacía");
        Preconditions.checkArgument(!PATRON_INYECCION_SQL.matcher(descripcion).matches(), "Entrada no válida detectada en la descripción");
        Preconditions.checkArgument(!PATRON_CARACTERES_ESPECIALES_CON_PORCENTAJE.matcher(descripcion).find(), "La descripción contiene caracteres no permitidos");
    }

    public static void validarNombreCategoria(String nombre) {
        Preconditions.checkNotNull(nombre, "El nombre no puede ser nulo");
        Preconditions.checkArgument(!nombre.isEmpty(), "El nombre no puede estar vacío");
        Preconditions.checkArgument(!contieneEspaciosMultiples(nombre),
                "El nombre no debe contener espacios múltiples");
    }

    public static void validarVariantes(TreeSet<Variante> variantes) {
        for (Variante variante : variantes) {
            validarCodigo(variante.getCodigo());
            Preconditions.checkArgument(variante.getPrecio().compareTo(BigDecimal.ZERO) > 0, "El precio debe ser mayor a cero");
            Preconditions.checkArgument(variante.getStock() >= 0, "El stock no puede ser negativo");
            Preconditions.checkArgument(variante.getCantidad() > 0, "La cantidad debe ser mayor a cero");
            Preconditions.checkArgument(validarRutaImagen(variante.getImagen()), "La imagen debe ser una ruta válida y no puede contener código malicioso");
        }
    }

    public static void validarFechaRecojo(LocalDate fechaRecojo) {
        Preconditions.checkNotNull(fechaRecojo, "La fecha de recojo no puede ser nula");
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaLimite = fechaActual.plusDays(7);

        Preconditions.checkArgument(!fechaRecojo.isBefore(fechaActual), "La fecha de recojo debe ser igual o posterior a la fecha actual");
        Preconditions.checkArgument(!fechaRecojo.isAfter(fechaLimite), "La fecha de recojo no puede ser posterior a 7 días a partir de la fecha actual");
    }

    public static void validarFechaInicio(LocalDate fechaInicio) {
        Preconditions.checkNotNull(fechaInicio, "La fecha de inicio no puede ser nula");
        LocalDate fechaActual = LocalDate.now();
        Preconditions.checkArgument(!fechaInicio.isBefore(fechaActual), "La fecha de inicio debe ser igual o posterior a la fecha actual");
    }

    public static void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        Preconditions.checkNotNull(fechaInicio, "La fecha de inicio no puede ser nula");
        Preconditions.checkNotNull(fechaFin, "La fecha de fin no puede ser nula");
        Preconditions.checkArgument(!fechaInicio.isAfter(fechaFin), "La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    private static boolean validarRutaImagen(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return false;
        }
        if (!ruta.startsWith("http://") && !ruta.startsWith("https://")) {
            return false;
        }
        return !PATRON_INYECCION_SQL.matcher(ruta).find();
    }

    public static void validarCodigo(String codigo) {
        Preconditions.checkArgument(codigo != null && codigo.matches("^[a-zA-Z0-9]+$"), "El código solo puede contener letras y números");
    }

    private static boolean contieneEspacios(String texto) {
        return texto.contains(" ");
    }

    private static boolean contieneEspaciosMultiples(String texto) {
        return texto.contains("  ");
    }
}
