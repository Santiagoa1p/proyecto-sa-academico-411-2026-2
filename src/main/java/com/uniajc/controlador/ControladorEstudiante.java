package com.uniajc.controlador;

import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

import java.util.List;
import java.util.Optional;

public class ControladorEstudiante {
    private final EstudianteService servicio;

    public ControladorEstudiante(EstudianteService servicio) {
        this.servicio = servicio;
    }

    public void registrar(Estudiante estudiante) {
        servicio.registrarEstudiante(estudiante);
    }

    public List<Estudiante> listar() {
        return servicio.obtenerTodosLosEstudiantes();
    }

    public Optional<Estudiante> buscar(int id) {
        return servicio.buscarEstudiantePorId(id);
    }

    public void actualizar(Estudiante estudiante) {
        servicio.actualizarEstudiante(estudiante);
    }

    public void eliminar(int id) {
        servicio.eliminarEstudiante(id);
    }
}
