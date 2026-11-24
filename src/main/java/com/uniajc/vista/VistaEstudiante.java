package com.uniajc.vista;

import java.util.List;
import java.util.Objects;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/**
 * Vista de consola (Scanner) para estudiantes (capa <b>Vista</b>).
 *
 * <p>Hereda de {@link VistaMenuConsola} el menú y el manejo de errores, e implementa
 * las cinco operaciones CRUD. Solo habla con el usuario (a través de
 * {@link LectorConsola}) y con su controlador; nunca con el servicio ni con el DAO.</p>
 */
public class VistaEstudiante extends VistaMenuConsola {

    /**
     * Formato de cada fila para {@code String.format}: {@code %-20s} significa "texto
     * alineado a la izquierda en 20 espacios", así las columnas quedan derechas.
     */
    private static final String FORMATO = "%-5s %-20s %-20s %-35s";

    private final ControladorEstudiante controlador;

    /**
     * Crea la vista. {@code super(...)} llama al constructor de {@link VistaMenuConsola}
     * con el lector y el título del menú.
     */
    public VistaEstudiante(LectorConsola lector, ControladorEstudiante controlador) {
        super(lector, "Estudiantes");
        this.controlador = controlador;
    }

    /** Lista todos los estudiantes en forma de tabla, o avisa si no hay ninguno. */
    @Override
    protected void listar() {
        List<Estudiante> estudiantes = controlador.listar();
        if (estudiantes.isEmpty()) {
            lector.mostrar("No hay estudiantes registrados.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "APELLIDO", "EMAIL"));
        estudiantes.forEach(this::imprimir);
    }

    /**
     * Pide los datos de un estudiante nuevo, lo envía al controlador y muestra el ID asignado.
     * Si algún dato no cumple las reglas, el servicio lanza un error que muestra
     * {@link VistaMenuConsola}.
     */
    @Override
    protected void registrar() {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(lector.leerTexto("Nombre"));
        estudiante.setApellido(lector.leerTexto("Apellido"));
        estudiante.setEmail(lector.leerTexto("Email"));
        controlador.registrar(estudiante);
        lector.exito("Estudiante registrado con ID " + estudiante.getIdEstudiante() + ".");
    }

    /**
     * Pide un ID y muestra ese registro. {@code orElseThrow} lanza el error
     * "No existe..." si el {@code Optional} viene vacío.
     */
    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del estudiante");
        Estudiante estudiante = controlador.buscar(id).orElseThrow(() -> noEncontrado("un estudiante", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "APELLIDO", "EMAIL"));
        imprimir(estudiante);
    }

    /**
     * Pide un ID, trae el registro actual y deja cambiar cada campo
     * (Enter conserva el valor que ya tenía).
     */
    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID del estudiante a actualizar");
        Estudiante estudiante = controlador.buscar(id).orElseThrow(() -> noEncontrado("un estudiante", id));
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        estudiante.setNombre(lector.leerTexto("Nombre", estudiante.getNombre()));
        estudiante.setApellido(lector.leerTexto("Apellido", estudiante.getApellido()));
        estudiante.setEmail(lector.leerTexto("Email", estudiante.getEmail()));
        controlador.actualizar(estudiante);
        lector.exito("Estudiante actualizado.");
    }

    /** Pide un ID y elimina ese registro después de pedir confirmación. */
    @Override
    protected void eliminar() {
        eliminarConConfirmacion("estudiante", controlador::eliminar);
    }

    /** Escribe un registro como una fila de la tabla, con el mismo formato que los títulos. */
    private void imprimir(Estudiante e) {
        lector.mostrar(String.format(FORMATO, e.getIdEstudiante(), e.getNombre(),
                Objects.toString(e.getApellido(), ""), Objects.toString(e.getEmail(), "")));
    }
}
