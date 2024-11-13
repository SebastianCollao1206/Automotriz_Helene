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

    public void agregarCliente(String correo, String contrasena, String confirmPassword, String dni, String nombre) throws Exception {

        Validaciones.validarCorreo(correo);
        Validaciones.validarPassword(contrasena);

        if (!contrasena.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        if (existeCorreo(correo)) {
            throw new IllegalArgumentException("El correo ya está registrado en el sistema");
        }

        correo = Validaciones.sanitizarEntrada(correo);
        dni = dni != null ? Validaciones.sanitizarEntrada(dni) : null;

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

    public boolean existeCorreo(String correo) {
        return clientes.stream()
                .anyMatch(cliente -> cliente.getCorreo().equalsIgnoreCase(correo));
    }
}
