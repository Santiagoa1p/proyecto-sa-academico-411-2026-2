package com.uniajc.vista;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Ayudante para leer y escribir en la consola de forma segura (capa <b>Vista</b>).
 *
 * <p>Envuelve un {@link Scanner} (para leer lo que escribe el usuario) y un
 * {@link PrintStream} (para escribir, normalmente {@code System.out}). Ventajas:</p>
 * <ul>
 *   <li>Repite la pregunta hasta recibir un valor válido (por ejemplo, un número).</li>
 *   <li>Al editar, muestra el valor actual entre corchetes y lo conserva si el
 *       usuario solo presiona Enter.</li>
 *   <li>Como recibe el Scanner y la salida por el constructor, las pruebas pueden
 *       simular lo que escribe el usuario sin teclado (ver {@code VistaConsolaTest}).</li>
 * </ul>
 */
public class LectorConsola {

    /**
     * Error que se lanza cuando ya no hay más entrada (por ejemplo, si el usuario
     * presiona Ctrl+Z en Windows o Ctrl+D en Linux). Permite cerrar el programa
     * limpiamente en vez de quedarse en un ciclo infinito.
     *
     * <p>Es una clase <i>anidada</i> ({@code static class} dentro de otra clase)
     * porque solo tiene sentido junto a {@code LectorConsola}.</p>
     */
    public static class FinDeEntrada extends RuntimeException {
        /** Crea el error con un mensaje fijo. */
        public FinDeEntrada() {
            super("La entrada terminó.");
        }
    }

    /** De dónde se lee lo que escribe el usuario. */
    private final Scanner scanner;
    /** Dónde se escriben los mensajes. */
    private final PrintStream salida;

    /**
     * Crea el lector.
     *
     * @param scanner lector de la entrada (normalmente {@code new Scanner(System.in)})
     * @param salida  destino de los mensajes (normalmente {@code System.out})
     */
    public LectorConsola(Scanner scanner, PrintStream salida) {
        this.scanner = scanner;
        this.salida = salida;
    }

    /** Escribe un mensaje normal en una línea. */
    public void mostrar(String mensaje) {
        salida.println(mensaje);
    }

    /** Escribe un mensaje de error, marcado con {@code [!]}. */
    public void error(String mensaje) {
        salida.println("  [!] " + mensaje);
    }

    /** Escribe un mensaje de éxito, marcado con {@code [OK]}. */
    public void exito(String mensaje) {
        salida.println("  [OK] " + mensaje);
    }

    /**
     * Muestra una etiqueta y lee una línea de texto.
     *
     * @param etiqueta lo que se le pregunta al usuario, por ejemplo "Nombre"
     * @return el texto escrito, sin espacios al inicio ni al final
     * @throws FinDeEntrada si ya no hay más entrada
     */
    public String leerTexto(String etiqueta) {
        salida.print(etiqueta + ": ");
        salida.flush();
        if (!scanner.hasNextLine()) {
            throw new FinDeEntrada();
        }
        return scanner.nextLine().trim();
    }

    /**
     * Lee un texto mostrando el valor actual; si el usuario solo presiona Enter, se
     * conserva. Se usa al actualizar. Es una <i>sobrecarga</i> de
     * {@link #leerTexto(String)}: mismo nombre, distintos parámetros.
     *
     * @param etiqueta lo que se pregunta
     * @param actual   valor que ya tiene el registro (puede ser {@code null})
     * @return el texto nuevo, o {@code actual} si el usuario no escribió nada
     */
    public String leerTexto(String etiqueta, String actual) {
        String texto = leerTexto(etiqueta + " [" + (actual == null ? "" : actual) + "]");
        return texto.isEmpty() ? actual : texto;
    }

    /**
     * Lee un número entero; si el usuario escribe letras, muestra un error y vuelve a preguntar.
     *
     * @param etiqueta lo que se pregunta
     * @return el número escrito
     */
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

    /**
     * Lee un número entero mostrando el valor actual; Enter lo conserva.
     *
     * @param etiqueta lo que se pregunta
     * @param actual   valor que ya tiene el registro
     * @return el número nuevo, o {@code actual} si el usuario no escribió nada
     */
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
     * Lee una nota decimal. Acepta coma o punto ({@code 4,5} o {@code 4.5}), porque en
     * Colombia se suele escribir con coma y Java solo entiende el punto.
     *
     * @param etiqueta lo que se pregunta
     * @param actual   nota actual, o {@code null} si no tiene
     * @return la nota escrita; {@code actual} si se presiona Enter; {@code null} si se escribe "-"
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

    /**
     * Hace una pregunta de sí o no.
     *
     * @param pregunta la pregunta, por ejemplo "¿Seguro que deseas eliminar…?"
     * @return {@code true} si responde "s", "si" o "sí" (sin importar mayúsculas); {@code false} en cualquier otro caso
     */
    public boolean confirmar(String pregunta) {
        String respuesta = leerTexto(pregunta + " (s/n)");
        return respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si") || respuesta.equalsIgnoreCase("sí");
    }
}
