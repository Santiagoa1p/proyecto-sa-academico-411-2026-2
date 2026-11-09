package com.uniajc.servicios;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.InscripcionCurso;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class InscripcionCursoService {
    private static final Set<String> ESTADOS = Set.of("ACTIVO", "APROBADO", "REPROBADO", "CANCELADO");

    private final InscripcionCursoDao inscripcionDao;
    private final EstudianteDao estudianteDao;
    private final GrupoDao grupoDao;

    public InscripcionCursoService(
            InscripcionCursoDao inscripcionDao,
            EstudianteDao estudianteDao,
            GrupoDao grupoDao) {
        this.inscripcionDao = inscripcionDao;
        this.estudianteDao = estudianteDao;
        this.grupoDao = grupoDao;
    }

    public void registrarInscripcion(InscripcionCurso inscripcion) {
        validar(inscripcion, 0);
        inscripcionDao.guardar(inscripcion);
    }

    public List<InscripcionCurso> obtenerTodasLasInscripciones() {
        return inscripcionDao.obtenerTodos();
    }

    public Optional<InscripcionCurso> buscarInscripcionPorId(int id) {
        Validaciones.idPositivo(id, "inscripción");
        return inscripcionDao.buscarPorId(id);
    }

    public void actualizarInscripcion(InscripcionCurso inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción es obligatoria.");
        }
        Validaciones.idPositivo(inscripcion.getIdInscripcion(), "inscripción");
        validar(inscripcion, inscripcion.getIdInscripcion());
        exigirInscripcionExistente(inscripcion.getIdInscripcion());
        inscripcionDao.actualizar(inscripcion);
    }

    public void eliminarInscripcion(int id) {
        Validaciones.idPositivo(id, "inscripción");
        exigirInscripcionExistente(id);
        inscripcionDao.eliminar(id);
    }

    private void exigirInscripcionExistente(int id) {
        if (inscripcionDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe la inscripción con ID " + id + ".");
        }
    }

    private void validar(InscripcionCurso inscripcion, int idInscripcionExcluida) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción es obligatoria.");
        }
        Validaciones.idPositivo(inscripcion.getIdEstudiante(), "estudiante");
        Validaciones.idPositivo(inscripcion.getIdGrupo(), "grupo");
        if (estudianteDao.buscarPorId(inscripcion.getIdEstudiante()).isEmpty()) {
            throw new IllegalArgumentException("No existe el estudiante indicado.");
        }
        if (grupoDao.buscarPorId(inscripcion.getIdGrupo()).isEmpty()) {
            throw new IllegalArgumentException("No existe el grupo indicado.");
        }
        if (inscripcionDao.existeParaEstudianteYGrupo(
                inscripcion.getIdEstudiante(), inscripcion.getIdGrupo(), idInscripcionExcluida)) {
            throw new IllegalArgumentException("El estudiante ya está inscrito en este grupo.");
        }
        Float nota = inscripcion.getNotaFinal();
        if (nota != null && (!Float.isFinite(nota) || nota < 0 || nota > 5)) {
            throw new IllegalArgumentException("La nota final debe estar entre 0 y 5.");
        }
        if (inscripcion.getEstado() == null || !ESTADOS.contains(inscripcion.getEstado())) {
            throw new IllegalArgumentException("El estado de inscripción no es válido.");
        }
    }
}
