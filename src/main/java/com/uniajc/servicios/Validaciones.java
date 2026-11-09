package com.uniajc.servicios;

import java.util.regex.Pattern;

final class Validaciones {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private Validaciones() {
    }

    static void requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
    }

    static void email(String valor) {
        requerido(valor, "email");
        if (!EMAIL.matcher(valor.trim()).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }
    }

    static void idPositivo(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser mayor que cero.");
        }
    }
}
