package com.uniajc.servicios;

import java.util.regex.Pattern;

/** Reglas de validación compartidas por los servicios. */
final class Validaciones {

    /** Letras (con tildes y ñ), espacios, puntos, apóstrofes y guiones. */
    private static final Pattern NOMBRE = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validaciones() {
    }

    /** Quita espacios sobrantes; devuelve "" si el texto es null. */
    static String limpiar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    static void obligatorio(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
    }

    static void idValido(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser un número mayor que cero.");
        }
    }

    static void texto(String valor, String campo, int minimo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        if (valor.length() < minimo || valor.length() > maximo) {
            throw new IllegalArgumentException(campo + " debe tener entre " + minimo + " y " + maximo + " caracteres.");
        }
    }

    static void nombrePropio(String valor, String campo) {
        texto(valor, campo, 2, 100);
        if (!NOMBRE.matcher(valor).matches()) {
            throw new IllegalArgumentException(campo + " solo puede contener letras y espacios.");
        }
    }

    static void email(String valor) {
        texto(valor, "El email", 5, 100);
        if (!EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido (ejemplo: nombre@uniajc.edu.co).");
        }
    }
}
