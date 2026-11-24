package com.uniajc.vista;

import com.uniajc.dao.ErrorBaseDatosException;

/**
 * Menú CRUD común para todas las vistas de consola (capa <b>Vista</b>).
 *
 * <p>Es una clase <b>abstracta</b>: no se puede usar sola, sirve de base para
 * {@code VistaEstudiante}, {@code VistaDocente}, etc., que la heredan con
 * {@code extends}. Aquí está lo que todas comparten (mostrar el menú, leer la
 * opción y atrapar los errores); cada vista hija solo implementa las cinco
 * operaciones marcadas como {@code abstract}. Esto es el patrón <i>Template Method</i>
 * y evita repetir el mismo menú cinco veces.</p>
 */
public abstract class VistaMenuConsola {

    /**
     * Lector para pedir y mostrar datos. Es {@code protected} para que las vistas
     * hijas puedan usarlo directamente.
     */
    protected final LectorConsola lector;
    /** Título del menú, por ejemplo "Estudiantes". */
    private final String titulo;

    /**
     * Constructor para las vistas hijas (por eso es {@code protected}).
     *
     * @param lector lector de consola compartido
     * @param titulo título que se muestra arriba del menú
     */
    protected VistaMenuConsola(LectorConsola lector, String titulo) {
        this.lector = lector;
        this.titulo = titulo;
    }

    /**
     * Muestra el menú en un ciclo hasta que el usuario elige "0. Volver".
     * {@code while (true)} repite para siempre; el {@code return} es la única salida.
     */
    public void mostrarMenu() {
        while (true) {
            lector.mostrar("");
            lector.mostrar("===== " + titulo.toUpperCase() + " =====");
            lector.mostrar("1. Listar");
            lector.mostrar("2. Registrar");
            lector.mostrar("3. Buscar por ID");
            lector.mostrar("4. Actualizar");
            lector.mostrar("5. Eliminar");
            lector.mostrar("0. Volver");
            String opcion = lector.leerTexto("Opción");
            if (opcion.equals("0")) {
                return;
            }
            ejecutar(opcion);
        }
    }

    /**
     * Ejecuta la operación elegida y muestra los errores sin cerrar el programa.
     *
     * <p>Aquí termina el "viaje de vuelta" de los errores: las reglas del servicio
     * ({@code IllegalArgumentException}) y los fallos de la base
     * ({@link ErrorBaseDatosException}) se atrapan y se le muestran al usuario. Después
     * el ciclo de {@link #mostrarMenu()} vuelve a mostrar el menú.</p>
     *
     * <p>El {@code switch} con flechas ({@code case "1" -> ...}) es la sintaxis moderna
     * de Java: cada caso ejecuta solo su línea, sin necesidad de {@code break}.</p>
     */
    private void ejecutar(String opcion) {
        try {
            switch (opcion) {
                case "1" -> listar();
                case "2" -> registrar();
                case "3" -> buscar();
                case "4" -> actualizar();
                case "5" -> eliminar();
                default -> lector.error("Opción no válida.");
            }
        } catch (IllegalArgumentException e) {
            lector.error(e.getMessage());
        } catch (ErrorBaseDatosException e) {
            lector.error("Error de base de datos. " + e.getMessage());
        }
    }

    // Operaciones que cada vista hija debe implementar. "abstract" significa que aquí
    // no tienen código: solo se declara que existen.

    /** Muestra todos los registros en forma de tabla. */
    protected abstract void listar();

    /** Pide los datos de un registro nuevo y lo guarda. */
    protected abstract void registrar();

    /** Pide un ID y muestra ese registro. */
    protected abstract void buscar();

    /** Pide un ID, muestra los datos actuales y permite cambiarlos. */
    protected abstract void actualizar();

    /** Pide un ID y elimina ese registro tras confirmar. */
    protected abstract void eliminar();

    /**
     * Pide un ID, pregunta "¿Seguro…?" y elimina solo si el usuario confirma.
     *
     * <p>Recibe la acción de eliminar como {@link java.util.function.IntConsumer}: una
     * función que recibe un {@code int}. Así cada vista le pasa su propia forma de
     * eliminar (por ejemplo {@code controlador::eliminar}) y este método no repite
     * la lógica de confirmación cinco veces.</p>
     *
     * @param entidad  nombre de la entidad para los mensajes, por ejemplo "estudiante"
     * @param eliminar función que elimina el registro con el ID dado
     */
    protected void eliminarConConfirmacion(String entidad, java.util.function.IntConsumer eliminar) {
        int id = lector.leerEntero("ID del " + entidad + " a eliminar");
        if (lector.confirmar("¿Seguro que deseas eliminar el " + entidad + " " + id + "?")) {
            eliminar.accept(id);
            lector.exito("Registro eliminado.");
        } else {
            lector.mostrar("Operación cancelada.");
        }
    }

    /**
     * Recorta textos largos para que no descuadren las columnas de la tabla en consola.
     *
     * @param texto  texto a recortar
     * @param maximo largo máximo permitido
     * @return el texto igual, o recortado y terminado en "…"
     */
    protected static String cortar(String texto, int maximo) {
        return texto.length() <= maximo ? texto : texto.substring(0, maximo - 1) + "…";
    }

    /**
     * Crea (sin lanzarlo) el error "No existe … con ID …". Se usa con
     * {@code Optional.orElseThrow(() -> noEncontrado(...))} cuando una búsqueda viene vacía.
     *
     * @param entidad texto de la entidad, por ejemplo "un estudiante"
     * @param id      ID que no se encontró
     * @return la excepción lista para lanzar
     */
    protected IllegalArgumentException noEncontrado(String entidad, int id) {
        return new IllegalArgumentException("No existe " + entidad + " con ID " + id + ".");
    }
}
