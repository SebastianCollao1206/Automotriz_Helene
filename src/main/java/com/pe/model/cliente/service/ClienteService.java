package com.pe.model.cliente.service;

import com.pe.model.cliente.dao.ClienteDAO;
import com.pe.model.cliente.entidad.Cliente;
import com.pe.util.Validaciones;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.TreeSet;

public class ClienteService {
    private final ClienteDAO clienteDAO;
    private final TreeSet<Cliente> clientes;

    public ClienteService() throws SQLException {
        this.clienteDAO = new ClienteDAO();
        this.clientes = new TreeSet<>(Cliente.CLIENTE_COMPARATOR_NATURAL_ORDER);
        clienteDAO.cargarClientes(this.clientes);
    }

    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public void cargarClientes() throws SQLException {
        clienteDAO.cargarClientes(clientes);
    }

    public TreeSet<Cliente> getClientes() {
        return clientes;
    }

    // Encriptación SHA-256
    private byte[] encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(password.getBytes());
    }

    // Autenticación
    public Cliente autenticarCliente(String correo, String password) throws Exception {
        Validaciones.validarCorreo(correo);
        Validaciones.validarPassword(password);
        correo = Validaciones.sanitizarEntrada(correo);

        clienteDAO.cargarClientes(clientes);

        for (Cliente cliente : clientes) {
            if (cliente.getCorreo().equals(correo)) {
                byte[] contrasenaEncriptada = encryptPassword(password);
                if (Arrays.equals(cliente.getContrasena(), contrasenaEncriptada)) {
                    return cliente;
                }
            }
        }
        return null;
    }

//    public void agregarCliente(String correo, String contrasena, String confirmPassword, String dni, String nombre) throws Exception {
//
//        Validaciones.validarCorreo(correo);
//        Validaciones.validarPassword(contrasena);
//
//        if (!contrasena.equals(confirmPassword)) {
//            throw new IllegalArgumentException("Las contraseñas no coinciden");
//        }
//        if (existeCorreo(correo)) {
//            throw new IllegalArgumentException("El correo ya está registrado en el sistema");
//        }
//
//        correo = Validaciones.sanitizarEntrada(correo);
//        dni = dni != null ? Validaciones.sanitizarEntrada(dni) : null;
//
//        byte[] contrasenaEncriptada = encryptPassword(contrasena);
//
//        Cliente cliente = new Cliente();
//        cliente.setCorreo(correo);
//        cliente.setContrasena(contrasenaEncriptada);
//        cliente.setDni(dni);
//        cliente.setNombre(nombre);
//        cliente.setFechaRegistro(LocalDate.now());
//        agregarCliente(cliente);
//        clienteDAO.agregarCliente(cliente);
//    }

    public void agregarCliente(String correo, String contrasena, String confirmPassword, String dni, String nombre) throws Exception {
        Validaciones.validarCorreo(correo);
        Validaciones.validarPassword(contrasena);

        if (!contrasena.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        if (existeCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado en el sistema");
        }
        if (dni != null && existeDni(dni)) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema");
        }

        correo = Validaciones.sanitizarEntrada(correo);
        dni = dni != null ? Validaciones.sanitizarEntrada(dni) : null;
        nombre = nombre != null ? Validaciones.sanitizarEntrada(nombre) : null;

        byte[] contrasenaEncriptada = encryptPassword(contrasena);

        Cliente cliente = new Cliente();
        cliente.setCorreo(correo);
        cliente.setContrasena(contrasenaEncriptada);
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setFechaRegistro(LocalDate.now());
        agregarCliente(cliente);
        clienteDAO.agregarCliente(cliente);
    }

    public boolean existeDni(String dni) {
        return clientes.stream()
                .anyMatch(cliente -> dni != null && dni.equals(cliente.getDni()));
    }

    public boolean existeCorreo(String correo) {
        return clientes.stream()
                .anyMatch(cliente -> cliente.getCorreo().equalsIgnoreCase(correo));
    }

    public Cliente obtenerClientePorId(int idCliente) {
        return clientes.stream()
                .filter(cliente -> cliente.getIdCliente() == idCliente)
                .findFirst()
                .orElse(null);
    }

    //Nuevos metodos para la compra
    public Cliente obtenerClientePorCorreo(String correo) {
        if (correo == null || correo.isEmpty()) {
            return null;
        }

        return clientes.stream()
                .filter(cliente -> cliente.getCorreo().equalsIgnoreCase(correo))
                .findFirst()
                .orElse(null);
    }

    public Cliente agregarClienteSinContrasena(String correo, String dni, String nombre) throws Exception {
        Validaciones.validarCorreo(correo);

        if (existeCorreo(correo)) {
            Cliente existingCliente = obtenerClientePorCorreo(correo);

            if (existingCliente.getDni() != null && dni != null && !existingCliente.getDni().equals(dni)) {
                throw new IllegalArgumentException("El correo ya está registrado con un DNI diferente.");
            }
            return existingCliente;
        }

        if (dni != null) {
            final String finalDni = dni;
            boolean dniExists = clientes.stream()
                    .anyMatch(cliente -> finalDni.equals(cliente.getDni()));

            if (dniExists) {
                throw new IllegalArgumentException("El DNI ya está registrado en el sistema con otro correo.");
            }
        }

        correo = Validaciones.sanitizarEntrada(correo);
        dni = dni != null ? Validaciones.sanitizarEntrada(dni) : null;

        Cliente cliente = new Cliente();
        cliente.setCorreo(correo);
        cliente.setDni(dni);
        cliente.setNombre(nombre);
        cliente.setFechaRegistro(LocalDate.now());

        agregarCliente(cliente);
        clienteDAO.agregarCliente(cliente);

        return cliente;
    }
    public void actualizarCliente(String correo, String dni, String nombre) throws Exception {
        Cliente cliente = obtenerClientePorCorreo(correo);

        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe en el sistema.");
        }

        cliente.setDni(dni != null ? Validaciones.sanitizarEntrada(dni) : cliente.getDni());
        cliente.setNombre(nombre != null ? Validaciones.sanitizarEntrada(nombre) : cliente.getNombre());

        clienteDAO.actualizarCliente(cliente);
    }

//    public void validarCoherenciaClienteExistente(String correo, String dni, String nombre) throws Exception {
//        Cliente clienteExistente = obtenerClientePorCorreo(correo);
//
//        if (clienteExistente != null) {
//            if (dni != null && clienteExistente.getDni() != null && !dni.equals(clienteExistente.getDni())) {
//                throw new IllegalArgumentException("El DNI proporcionado no coincide con el registro existente.");
//            }
//            if (nombre != null && !nombre.equals(clienteExistente.getNombre())) {
//                throw new IllegalArgumentException("El nombre proporcionado no coincide con el registro existente.");
//            }
//        }
//    }
}
