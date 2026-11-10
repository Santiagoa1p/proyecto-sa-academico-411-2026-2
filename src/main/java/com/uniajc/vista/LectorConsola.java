package com.uniajc.vista;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Envuelve Scanner y PrintStream para leer datos de forma segura en consola:
 * repite la pregunta hasta recibir un valor válido y permite conservar el
 * valor actual al editar (pulsando Enter).
 */
public class LectorConsola {

    /** Se lanza cuando la entrada se termina (por ejemplo, Ctrl+Z / Ctrl+D). */
    public static class FinDeEntrada extends RuntimeException {
        public FinDeEntrada() {
            super("La entrada terminó.");
        }
    }

    private final Scanner scanner;
    private final PrintStream salida;

    public LectorConsola(Scanner scanner, PrintStream salida) {
        this.scanner = scanner;
        this.salida = salida;
    }

    public void mostrar(String mensaje) {
        salida.println(mensaje);
    }

    public void error(String mensaje) {
        salida.println("  [!] " + mensaje);
    }

    public void exito(String mensaje) {
        salida.println("  [OK] " + mensaje);
    }

    public String leerTexto(String etiqueta) {
        salida.print(etiqueta + ": ");
        salida.flush();
        if (!scanner.hasNextLine()) {
            throw new FinDeEntrada();
        }
        return scanner.nextLine().trim();
    }

    /** Muestra el valor actual; si el usuario pulsa Enter se conserva. */
    public String leerTexto(String etiqueta, String actual) {
        String texto = leerTexto(etiqueta + " [" + (actual == null ? "" : actual) + "]");
        return texto.isEmpty() ? actual : texto;
    }

    public int leerEntero(String etiqueta) {
        while (true) {
            String texto = leerTexto(etiqueta);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                error("Escribe un número entero.");
            }
        }
    }

    public int leerEntero(String etiqueta, int actual) {
        while (true) {
            String texto = leerTexto(etiqueta + " [" + actual + "]");
            if (texto.isEmpty()) {
                return actual;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                error("Escribe un número entero.");
            }
        }
    }

    /**
     * Lee una nota decimal (acepta coma o punto). Vacío conserva el valor
     * actual y "-" la deja sin nota.
     */
    public Float leerNota(String etiqueta, Float actual) {
        String sufijo = actual == null ? "sin nota" : String.valueOf(actual);
        while (true) {
            String texto = leerTexto(etiqueta + " (Enter conserva, - sin nota) [" + sufijo + "]");
            if (texto.isEmpty()) {
                return actual;
            }
            if (texto.equals("-")) {
                return null;
            }
            try {
                return Float.parseFloat(texto.replace(',', '.'));
            } catch (NumberFormatException e) {
                error("Escribe una nota como 4.5 o deja '-' para no asignarla.");
            }
        }
    }

    public boolean confirmar(String pregunta) {
        String respuesta = leerTexto(pregunta + " (s/n)");
        return respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("sí");
    }
}
