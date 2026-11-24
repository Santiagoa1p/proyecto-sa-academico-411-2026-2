package com.uniajc.servicios;

import java.util.regex.Pattern;

/**
 * Validaciones comunes que reutilizan todos los servicios.
 *
 * <p>Se juntaron aquí para no repetir el mismo {@code if} en cada servicio. Cada
 * método revisa una condición y, si no se cumple, lanza
 * {@link IllegalArgumentException} con un mensaje en español listo para mostrarse
 * al usuario. Si se cumple, no hace nada y el código sigue.</p>
 *
 * <p>La clase es {@code final} (no se puede heredar), tiene constructor privado (no
 * se pueden crear objetos) y no es {@code public}: solo la usan los servicios de
 * este mismo paquete.</p>
 */
final class Validaciones {

    /**
     * Expresión regular para nombres: letras de cualquier idioma ({@code \p{L}} incluye
     * tildes y ñ), espacios, puntos, apóstrofes y guiones. Rechaza números y símbolos.
     * {@code ^} y {@code $} obligan a que todo el texto cumpla, no solo una parte.
     */
    private static final Pattern NOMBRE = Pattern.compile("^[\\p{L} .'-]+$");
    /**
     * Expresión regular para emails: algo antes de la {@code @}, un dominio, un punto y
     * una terminación de al menos 2 letras (por ejemplo {@code nombre@uniajc.edu.co}).
     */
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /** Constructor privado: es una clase de utilidades, todo se usa de forma estática. */
    private Validaciones() {
    }

    /**
     * Normaliza un texto escrito por el usuario: quita los espacios del inicio y del
     * final, y reemplaza varios espacios seguidos por uno solo
     * ({@code "  Ana   María "} → {@code "Ana María"}).
     *
     * @param texto el texto a limpiar (puede ser {@code null})
     * @return el texto limpio, o {@code ""} si era {@code null}
     */
    static String limpiar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    /**
     * Exige que un valor no sea {@code null}.
     *
     * @param valor el objeto a revisar
     * @param campo nombre del campo para el mensaje, por ejemplo "El estudiante"
     * @throws IllegalArgumentException si el valor es {@code null}
     */
    static void obligatorio(Object valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
    }

    /**
     * Exige que un ID sea positivo. Los ID de la base empiezan en 1, así que 0 o un
     * número negativo nunca pueden existir.
     *
     * @param id      el ID a revisar
     * @param entidad nombre de la entidad para el mensaje, por ejemplo "estudiante"
     * @throws IllegalArgumentException si el ID es menor o igual a 0
     */
    static void idValido(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser un número mayor que cero.");
        }
    }

    /**
     * Exige que un texto no esté vacío y que su longitud esté dentro de un rango.
     *
     * @param valor  el texto a revisar
     * @param campo  nombre del campo para el mensaje
     * @param minimo cantidad mínima de caracteres
     * @param maximo cantidad máxima de caracteres (coincide con el tamaño de la columna)
     * @throws IllegalArgumentException si está vacío o fuera del rango
     */
    static void texto(String valor, String campo, int minimo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        if (valor.length() < minimo || valor.length() > maximo) {
            throw new IllegalArgumentException(campo + " debe tener entre " + minimo + " y " + maximo + " caracteres.");
        }
    }

    /**
     * Exige un nombre de persona válido: entre 2 y 100 caracteres y solo letras
     * (ver {@link #NOMBRE}). Así se rechazan valores como "Ana2" o "@@@".
     *
     * @param valor el nombre a revisar
     * @param campo nombre del campo para el mensaje, por ejemplo "El apellido"
     * @throws IllegalArgumentException si no cumple
     */
    static void nombrePropio(String valor, String campo) {
        texto(valor, campo, 2, 100);
        if (!NOMBRE.matcher(valor).matches()) {
            throw new IllegalArgumentException(campo + " solo puede contener letras y espacios.");
        }
    }

    /**
     * Exige un email con formato válido (ver {@link #EMAIL}) y entre 5 y 100 caracteres.
     *
     * @param valor el email a revisar
     * @throws IllegalArgumentException si no cumple
     */
    static void email(String valor) {
        texto(valor, "El email", 5, 100);
        if (!EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido (ejemplo: nombre@uniajc.edu.co).");
        }
    }
}
