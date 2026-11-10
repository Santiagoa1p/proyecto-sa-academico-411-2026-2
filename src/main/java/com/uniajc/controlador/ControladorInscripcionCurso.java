package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.servicios.InscripcionCursoService;

/**
 * Orquesta las peticiones de las vistas (consola o Swing) sobre inscripciones
 * y las delega al servicio, que aplica las reglas de negocio.
 */
public class ControladorInscripcionCurso {

    private final InscripcionCursoService servicio;

    public ControladorInscripcionCurso(InscripcionCursoService servicio) {
        this.servicio = servicio;
    }

    public List<InscripcionCurso> listar() {
        return servicio.obtenerTodasLasInscripciones();
    }

    public Optional<InscripcionCurso> buscar(int id) {
        return servicio.buscarInscripcionPorId(id);
    }

    public void registrar(InscripcionCurso entidad) {
        servicio.registrarInscripcion(entidad);
    }

    public void actualizar(InscripcionCurso entidad) {
        servicio.actualizarInscripcion(entidad);
    }

    public void eliminar(int id) {
        servicio.eliminarInscripcion(id);
    }
}
