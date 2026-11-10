package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

/**
 * Orquesta las peticiones de las vistas (consola o Swing) sobre docentes
 * y las delega al servicio, que aplica las reglas de negocio.
 */
public class ControladorDocente {

    private final DocenteService servicio;

    public ControladorDocente(DocenteService servicio) {
        this.servicio = servicio;
    }

    public List<Docente> listar() {
        return servicio.obtenerTodosLosDocentes();
    }

    public Optional<Docente> buscar(int id) {
        return servicio.buscarDocentePorId(id);
    }

    public void registrar(Docente entidad) {
        servicio.registrarDocente(entidad);
    }

    public void actualizar(Docente entidad) {
        servicio.actualizarDocente(entidad);
    }

    public void eliminar(int id) {
        servicio.eliminarDocente(id);
    }
}
