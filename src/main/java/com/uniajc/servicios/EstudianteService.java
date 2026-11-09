package com.uniajc.servicios;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;

import java.util.List;
import java.util.Optional;

public class EstudianteService {
    private final EstudianteDao estudianteDao;

    public EstudianteService(EstudianteDao estudianteDao) {
        this.estudianteDao = estudianteDao;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        validar(estudiante);
        estudianteDao.guardar(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        Validaciones.idPositivo(id, "estudiante");
        return estudianteDao.buscarPorId(id);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        validar(estudiante);
        Validaciones.idPositivo(estudiante.getIdEstudiante(), "estudiante");
        exigirExistente(estudiante.getIdEstudiante());
        estudianteDao.actualizar(estudiante);
    }

    public void eliminarEstudiante(int id) {
        Validaciones.idPositivo(id, "estudiante");
        exigirExistente(id);
        estudianteDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        if (estudianteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe el estudiante con ID " + id + ".");
        }
    }

    private void validar(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio.");
        }
        Validaciones.requerido(estudiante.getNombre(), "nombre");
        Validaciones.requerido(estudiante.getApellido(), "apellido");
        Validaciones.email(estudiante.getEmail());
    }
}
