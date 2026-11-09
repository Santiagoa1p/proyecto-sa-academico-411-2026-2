package com.uniajc.controlador;

import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

import java.util.List;
import java.util.Optional;

public class ControladorDocente {
    private final DocenteService servicio;

    public ControladorDocente(DocenteService servicio) {
        this.servicio = servicio;
    }

    public void registrar(Docente docente) {
        servicio.registrarDocente(docente);
    }

    public List<Docente> listar() {
        return servicio.obtenerTodosLosDocentes();
    }

    public Optional<Docente> buscar(int id) {
        return servicio.buscarDocentePorId(id);
    }

    public void actualizar(Docente docente) {
        servicio.actualizarDocente(docente);
    }

    public void eliminar(int id) {
        servicio.eliminarDocente(id);
    }
}
