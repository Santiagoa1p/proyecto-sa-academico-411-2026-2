package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

/**
 * Orquesta las peticiones de las vistas (consola o Swing) sobre estudiantes
 * y las delega al servicio, que aplica las reglas de negocio.
 */
public class ControladorEstudiante {

    private final EstudianteService servicio;

    public ControladorEstudiante(EstudianteService servicio) {
        this.servicio = servicio;
    }

    public List<Estudiante> listar() {
        return servicio.obtenerTodosLosEstudiantes();
    }

    public Optional<Estudiante> buscar(int id) {
        return servicio.buscarEstudiantePorId(id);
    }

    public void registrar(Estudiante entidad) {
        servicio.registrarEstudiante(entidad);
    }

    public void actualizar(Estudiante entidad) {
        servicio.actualizarEstudiante(entidad);
    }

    public void eliminar(int id) {
        servicio.eliminarEstudiante(id);
    }
}
