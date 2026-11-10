package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.InscripcionCurso;

/** Vista de consola (Scanner) para inscripciones a cursos. */
public class VistaInscripcionCurso extends VistaMenuConsola {

    private static final String FORMATO = "%-5s %-30s %-8s %-6s %-10s";

    private final ControladorInscripcionCurso controlador;
    private final ControladorEstudiante estudiantes;
    private final ControladorGrupo grupos;

    public VistaInscripcionCurso(LectorConsola lector, ControladorInscripcionCurso controlador,
                                 ControladorEstudiante estudiantes, ControladorGrupo grupos) {
        super(lector, "Inscripciones");
        this.controlador = controlador;
        this.estudiantes = estudiantes;
        this.grupos = grupos;
    }

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

    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID de la inscripción");
        InscripcionCurso inscripcion = controlador.buscar(id).orElseThrow(() -> noEncontrado("una inscripción", id));
        lector.mostrar(String.format(FORMATO, "ID", "ESTUDIANTE", "GRUPO", "NOTA", "ESTADO"));
        imprimir(inscripcion);
    }

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

    private void mostrarOpciones() {
        lector.mostrar("Estudiantes:");
        for (Estudiante e : estudiantes.listar()) {
            lector.mostrar("  " + e.getIdEstudiante() + " - " + e.getNombre() + " " + e.getApellido());
        }
        lector.mostrar("Grupos:");
        for (Grupo g : grupos.listar()) {
            lector.mostrar("  " + g.getIdGrupo() + " - " + g.getAula() + ", " + g.getHorario());
        }
    }

    private void imprimir(InscripcionCurso i) {
        String estudiante = estudiantes.buscar(i.getIdEstudiante())
                .map(e -> e.getNombre() + " " + e.getApellido())
                .orElse("#" + i.getIdEstudiante());
        String nota = i.getNotaFinal() == null ? "-" : String.format("%.2f", i.getNotaFinal());
        lector.mostrar(String.format(FORMATO, i.getIdInscripcion(), estudiante, i.getIdGrupo(), nota, i.getEstado()));
    }
}
