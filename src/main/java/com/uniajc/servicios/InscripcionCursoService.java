package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.InscripcionCurso;

/**
 * Lógica de negocio de inscripciones. Escala de notas de 0.0 a 5.0;
 * se aprueba con 3.0 o más.
 */
public class InscripcionCursoService {

    public static final float NOTA_MINIMA = 0.0f;
    public static final float NOTA_MAXIMA = 5.0f;
    public static final float NOTA_APROBATORIA = 3.0f;
    public static final Set<String> ESTADOS = Set.of(
            InscripcionCurso.ACTIVO, InscripcionCurso.APROBADO,
            InscripcionCurso.REPROBADO, InscripcionCurso.CANCELADO);

    private final InscripcionCursoDao inscripcionDao;
    private final EstudianteDao estudianteDao;
    private final GrupoDao grupoDao;

    public InscripcionCursoService(InscripcionCursoDao inscripcionDao, EstudianteDao estudianteDao, GrupoDao grupoDao) {
        this.inscripcionDao = inscripcionDao;
        this.estudianteDao = estudianteDao;
        this.grupoDao = grupoDao;
    }

    public void registrarInscripcion(InscripcionCurso inscripcion) {
        validarInscripcion(inscripcion);
        inscripcionDao.guardar(inscripcion);
    }

    public List<InscripcionCurso> obtenerTodasLasInscripciones() {
        return inscripcionDao.obtenerTodos();
    }

    public Optional<InscripcionCurso> buscarInscripcionPorId(int id) {
        Validaciones.idValido(id, "inscripción");
        return inscripcionDao.buscarPorId(id);
    }

    public void actualizarInscripcion(InscripcionCurso inscripcion) {
        Validaciones.obligatorio(inscripcion, "La inscripción");
        exigirExistente(inscripcion.getIdInscripcion());
        validarInscripcion(inscripcion);
        inscripcionDao.actualizar(inscripcion);
    }

    public void eliminarInscripcion(int id) {
        exigirExistente(id);
        inscripcionDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        Validaciones.idValido(id, "inscripción");
        if (inscripcionDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una inscripción con ID " + id + ".");
        }
    }

    private void validarInscripcion(InscripcionCurso inscripcion) {
        Validaciones.obligatorio(inscripcion, "La inscripción");
        Validaciones.idValido(inscripcion.getIdEstudiante(), "estudiante");
        Validaciones.idValido(inscripcion.getIdGrupo(), "grupo");

        if (estudianteDao.buscarPorId(inscripcion.getIdEstudiante()).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID " + inscripcion.getIdEstudiante() + ".");
        }
        if (grupoDao.buscarPorId(inscripcion.getIdGrupo()).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID " + inscripcion.getIdGrupo() + ".");
        }
        inscripcionDao.buscarPorEstudianteYGrupo(inscripcion.getIdEstudiante(), inscripcion.getIdGrupo())
                .filter(otra -> otra.getIdInscripcion() != inscripcion.getIdInscripcion())
                .ifPresent(otra -> {
                    throw new IllegalArgumentException("El estudiante ya está inscrito en ese grupo (inscripción "
                            + otra.getIdInscripcion() + ").");
                });

        validarNotaYEstado(inscripcion);
    }

    private void validarNotaYEstado(InscripcionCurso inscripcion) {
        String estado = Validaciones.limpiar(inscripcion.getEstado()).toUpperCase();
        if (estado.isEmpty()) {
            estado = InscripcionCurso.ACTIVO;
        }
        if (!ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("El estado debe ser uno de: ACTIVO, APROBADO, REPROBADO o CANCELADO.");
        }
        inscripcion.setEstado(estado);

        Float nota = inscripcion.getNotaFinal();
        if (nota != null && (nota.isNaN() || nota < NOTA_MINIMA || nota > NOTA_MAXIMA)) {
            throw new IllegalArgumentException("La nota final debe estar entre 0.0 y 5.0.");
        }
        if (InscripcionCurso.APROBADO.equals(estado) && (nota == null || nota < NOTA_APROBATORIA)) {
            throw new IllegalArgumentException("Para marcar APROBADO la nota debe ser 3.0 o mayor.");
        }
        if (InscripcionCurso.REPROBADO.equals(estado) && (nota == null || nota >= NOTA_APROBATORIA)) {
            throw new IllegalArgumentException("Para marcar REPROBADO la nota debe ser menor que 3.0.");
        }
    }
}
