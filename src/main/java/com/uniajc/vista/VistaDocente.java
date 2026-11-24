package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

/**
 * Vista de consola (Scanner) para docentes (capa <b>Vista</b>).
 *
 * <p>Hereda de {@link VistaMenuConsola} el menú y el manejo de errores, e implementa
 * las cinco operaciones CRUD. Solo habla con el usuario (a través de
 * {@link LectorConsola}) y con su controlador; nunca con el servicio ni con el DAO.</p>
 */
public class VistaDocente extends VistaMenuConsola {

    /**
     * Formato de cada fila para {@code String.format}: {@code %-20s} significa "texto
     * alineado a la izquierda en 20 espacios", así las columnas quedan derechas.
     */
    private static final String FORMATO = "%-5s %-30s %-30s";

    private final ControladorDocente controlador;

    /**
     * Crea la vista. {@code super(...)} llama al constructor de {@link VistaMenuConsola}
     * con el lector y el título del menú.
     */
    public VistaDocente(LectorConsola lector, ControladorDocente controlador) {
        super(lector, "Docentes");
        this.controlador = controlador;
    }

    /** Lista todos los docentes en forma de tabla, o avisa si no hay ninguno. */
    @Override
    protected void listar() {
        List<Docente> docentes = controlador.listar();
        if (docentes.isEmpty()) {
            lector.mostrar("No hay docentes registrados.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "ESPECIALIDAD"));
        docentes.forEach(this::imprimir);
    }

    /**
     * Pide los datos de un docente nuevo, lo envía al controlador y muestra el ID asignado.
     * Si algún dato no cumple las reglas, el servicio lanza un error que muestra
     * {@link VistaMenuConsola}.
     */
    @Override
    protected void registrar() {
        Docente docente = new Docente();
        docente.setNombre(lector.leerTexto("Nombre completo"));
        docente.setEspecialidad(lector.leerTexto("Especialidad"));
        controlador.registrar(docente);
        lector.exito("Docente registrado con ID " + docente.getIdDocente() + ".");
    }

    /**
     * Pide un ID y muestra ese registro. {@code orElseThrow} lanza el error
     * "No existe..." si el {@code Optional} viene vacío.
     */
    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del docente");
        Docente docente = controlador.buscar(id).orElseThrow(() -> noEncontrado("un docente", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "ESPECIALIDAD"));
        imprimir(docente);
    }

    /**
     * Pide un ID, trae el registro actual y deja cambiar cada campo
     * (Enter conserva el valor que ya tenía).
     */
    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID del docente a actualizar");
        Docente docente = controlador.buscar(id).orElseThrow(() -> noEncontrado("un docente", id));
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        docente.setNombre(lector.leerTexto("Nombre completo", docente.getNombre()));
        docente.setEspecialidad(lector.leerTexto("Especialidad", docente.getEspecialidad()));
        controlador.actualizar(docente);
        lector.exito("Docente actualizado.");
    }

    /** Pide un ID y elimina ese registro después de pedir confirmación. */
    @Override
    protected void eliminar() {
        eliminarConConfirmacion("docente", controlador::eliminar);
    }

    /** Escribe un registro como una fila de la tabla, con el mismo formato que los títulos. */
    private void imprimir(Docente d) {
        lector.mostrar(String.format(FORMATO, d.getIdDocente(), d.getNombre(), d.getEspecialidad()));
    }
}
