package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.Estudiante;

/** Lógica de negocio de estudiantes. */
public class EstudianteService {

    private final EstudianteDao estudianteDao;
    private final InscripcionCursoDao inscripcionDao;

    public EstudianteService(EstudianteDao estudianteDao) {
        this(estudianteDao, new InscripcionCursoDao());
    }

    public EstudianteService(EstudianteDao estudianteDao, InscripcionCursoDao inscripcionDao) {
        this.estudianteDao = estudianteDao;
        this.inscripcionDao = inscripcionDao;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);
        validarEmailDisponible(estudiante.getEmail(), 0);
        estudianteDao.guardar(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        Validaciones.idValido(id, "estudiante");
        return estudianteDao.buscarPorId(id);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        Validaciones.obligatorio(estudiante, "El estudiante");
        exigirExistente(estudiante.getIdEstudiante());
        validarEstudiante(estudiante);
        validarEmailDisponible(estudiante.getEmail(), estudiante.getIdEstudiante());
        estudianteDao.actualizar(estudiante);
    }

    public void eliminarEstudiante(int id) {
        exigirExistente(id);
        if (inscripcionDao.contarPorEstudiante(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el estudiante porque tiene inscripciones. Elimínalas primero.");
        }
        estudianteDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        Validaciones.idValido(id, "estudiante");
        if (estudianteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID " + id + ".");
        }
    }

    /** Limpia los textos y comprueba formato. El email se guarda en minúsculas. */
    private void validarEstudiante(Estudiante estudiante) {
        Validaciones.obligatorio(estudiante, "El estudiante");
        estudiante.setNombre(Validaciones.limpiar(estudiante.getNombre()));
        estudiante.setApellido(Validaciones.limpiar(estudiante.getApellido()));
        estudiante.setEmail(Validaciones.limpiar(estudiante.getEmail()).toLowerCase());

        Validaciones.nombrePropio(estudiante.getNombre(), "El nombre");
        Validaciones.nombrePropio(estudiante.getApellido(), "El apellido");
        Validaciones.email(estudiante.getEmail());
    }

    private void validarEmailDisponible(String email, int idPropio) {
        estudianteDao.buscarPorEmail(email)
                .filter(otro -> otro.getIdEstudiante() != idPropio)
                .ifPresent(otro -> {
                    throw new IllegalArgumentException("Ya existe un estudiante con el email " + email + ".");
                });
    }
}
