package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;

/**
 * Vista de consola (Scanner) para inscripciones a cursos (capa <b>Vista</b>).
 *
 * <p>Hereda de {@link VistaMenuConsola} el menú y el manejo de errores, e implementa
 * las cinco operaciones CRUD. Solo habla con el usuario (a través de
 * {@link LectorConsola}) y con su controlador; nunca con el servicio ni con el DAO.</p>
 *
 * <p>También recibe los controladores de estudiantes y grupos, para mostrar el nombre
 * del estudiante en la tabla y listar las opciones al registrar o actualizar.</p>
 */
public class VistaInscripcionCurso extends VistaMenuConsola {

    /**
     * Formato de cada fila para {@code String.format}: {@code %-20s} significa "texto
     * alineado a la izquierda en 20 espacios", así las columnas quedan derechas.
     */
    private static final String FORMATO = "%-5s %-30s %-8s %-6s %-10s";

    private final ControladorInscripcionCurso controlador;
    private final ControladorEstudiante estudiantes;
    private final ControladorGrupo grupos;

    /**
     * Crea la vista. {@code super(...)} llama al constructor de {@link VistaMenuConsola}
     * con el lector y el título del menú.
     */
    public VistaInscripcionCurso(LectorConsola lector, ControladorInscripcionCurso controlador,
                                 ControladorEstudiante estudiantes, ControladorGrupo grupos) {
        super(lector, "Inscripciones");
        this.controlador = controlador;
        this.estudiantes = estudiantes;
        this.grupos = grupos;
    }

    /** Lista todas las inscripciones en forma de tabla, o avisa si no hay ninguna. */
    @Override
    protected void listar() {
        List<InscripcionCurso> inscripciones = controlador.listar();
        if (inscripciones.isEmpty()) {
            lector.mostrar("No hay inscripciones registradas.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "ESTUDIANTE", "GRUPO", "NOTA", "ESTADO"));
        inscripciones.forEach(this::imprimir);
    }

    /**
     * Pide los datos de una inscripción nueva, lo envía al controlador y muestra el ID asignado.
     * Si algún dato no cumple las reglas, el servicio lanza un error que muestra
     * {@link VistaMenuConsola}.
     */
    @Override
    protected void registrar() {
        InscripcionCurso inscripcion = new InscripcionCurso();
        mostrarOpciones();
        inscripcion.setIdEstudiante(lector.leerEntero("ID del estudiante"));
        inscripcion.setIdGrupo(lector.leerEntero("ID del grupo"));
        inscripcion.setNotaFinal(lector.leerNota("Nota final", null));
        inscripcion.setEstado(lector.leerTexto("Estado (ACTIVO/APROBADO/REPROBADO/CANCELADO)", InscripcionCurso.ACTIVO));
        controlador.registrar(inscripcion);
        lector.exito("Inscripción registrada con ID " + inscripcion.getIdInscripcion() + ".");
    }

    /**
     * Pide un ID y muestra ese registro. {@code orElseThrow} lanza el error
     * "No existe..." si el {@code Optional} viene vacío.
     */
    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID de la inscripción");
        InscripcionCurso inscripcion = controlador.buscar(id).orElseThrow(() -> noEncontrado("una inscripción", id));
        lector.mostrar(String.format(FORMATO, "ID", "ESTUDIANTE", "GRUPO", "NOTA", "ESTADO"));
        imprimir(inscripcion);
    }

    /**
     * Pide un ID, trae el registro actual y deja cambiar cada campo
     * (Enter conserva el valor que ya tenía).
     */
    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID de la inscripción a actualizar");
        InscripcionCurso inscripcion = controlador.buscar(id).orElseThrow(() -> noEncontrado("una inscripción", id));
        mostrarOpciones();
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        inscripcion.setIdEstudiante(lector.leerEntero("ID del estudiante", inscripcion.getIdEstudiante()));
        inscripcion.setIdGrupo(lector.leerEntero("ID del grupo", inscripcion.getIdGrupo()));
        inscripcion.setNotaFinal(lector.leerNota("Nota final", inscripcion.getNotaFinal()));
        inscripcion.setEstado(lector.leerTexto("Estado (ACTIVO/APROBADO/REPROBADO/CANCELADO)", inscripcion.getEstado()));
        controlador.actualizar(inscripcion);
        lector.exito("Inscripción actualizada.");
    }

    /** Pide un ID y elimina ese registro después de pedir confirmación. */
    @Override
    protected void eliminar() {
        int id = lector.leerEntero("ID de la inscripción a eliminar");
        if (lector.confirmar("¿Seguro que deseas eliminar la inscripción " + id + "?")) {
            controlador.eliminar(id);
            lector.exito("Inscripción eliminada.");
        } else {
            lector.mostrar("Operación cancelada.");
        }
    }

    /**
     * Muestra los ID disponibles de las entidades relacionadas, para que el usuario
     * sepa qué número escribir.
     */
    private void mostrarOpciones() {
        lector.mostrar("Estudiantes:");
        for (Estudiante e : estudiantes.listar()) {
            lector.mostrar("  " + e.getIdEstudiante() + " - " + e.getNombreCompleto());
        }
        lector.mostrar("Grupos:");
        for (Grupo g : grupos.listar()) {
            lector.mostrar("  " + g.getIdGrupo() + " - " + g.getAula() + ", " + g.getHorario());
        }
    }

    /** Escribe un registro como una fila de la tabla, con el mismo formato que los títulos. */
    private void imprimir(InscripcionCurso i) {
        String estudiante = estudiantes.buscar(i.getIdEstudiante())
                .map(Estudiante::getNombreCompleto)
                .orElse("#" + i.getIdEstudiante());
        String nota = i.getNotaFinal() == null ? "-" : String.format("%.2f", i.getNotaFinal());
        lector.mostrar(String.format(FORMATO, i.getIdInscripcion(), cortar(estudiante, 30), i.getIdGrupo(),
                nota, i.getEstado()));
    }
}
