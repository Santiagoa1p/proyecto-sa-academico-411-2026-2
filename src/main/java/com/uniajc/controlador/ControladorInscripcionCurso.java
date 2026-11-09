package com.uniajc.controlador;

import com.uniajc.modelo.InscripcionCurso;
import com.uniajc.servicios.InscripcionCursoService;

import java.util.List;
import java.util.Optional;

public class ControladorInscripcionCurso {
    private final InscripcionCursoService servicio;

    public ControladorInscripcionCurso(InscripcionCursoService servicio) {
        this.servicio = servicio;
    }

    public void registrar(InscripcionCurso inscripcion) {
        servicio.registrarInscripcion(inscripcion);
    }

    public List<InscripcionCurso> listar() {
        return servicio.obtenerTodasLasInscripciones();
    }

    public Optional<InscripcionCurso> buscar(int id) {
        return servicio.buscarInscripcionPorId(id);
    }

    public void actualizar(InscripcionCurso inscripcion) {
        servicio.actualizarInscripcion(inscripcion);
    }

    public void eliminar(int id) {
        servicio.eliminarInscripcion(id);
    }
}
