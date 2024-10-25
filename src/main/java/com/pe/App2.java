package com.pe;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class App2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Pedir la contraseña a encriptar
        System.out.print("Introduce la contraseña a encriptar: ");
        String password = scanner.nextLine();

        // Encriptar la contraseña
        byte[] encryptedPassword = encryptPassword(password);
        String encryptedPasswordHex = bytesToHex(encryptedPassword);

        // Mostrar la contraseña encriptada
        System.out.println("Contraseña encriptada (en formato hexadecimal): " + encryptedPasswordHex);

        // Pedir la contraseña para verificar
        System.out.print("Introduce la contraseña para verificar: ");
        String passwordToVerify = scanner.nextLine();

        // Verificar la contraseña
        if (verifyPassword(passwordToVerify, encryptedPasswordHex)) {
            System.out.println("La contraseña es correcta.");
        } else {
            System.out.println("La contraseña es incorrecta.");
        }

        scanner.close();
    }

    private static byte[] encryptPassword(String password) {
        try {
            // Crear un objeto MessageDigest para SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Encriptar la contraseña
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la contraseña: " + e.getMessage(), e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static boolean verifyPassword(String password, String storedHash) {
        // Encriptar la contraseña ingresada
        byte[] encryptedPassword = encryptPassword(password);
        String encryptedPasswordHex = bytesToHex(encryptedPassword);
        // Comparar el hash generado con el hash almacenado
        return encryptedPasswordHex.equals(storedHash);
    }
}
