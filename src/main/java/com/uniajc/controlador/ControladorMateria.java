package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

/**
 * Orquesta las peticiones de las vistas (consola o Swing) sobre materias
 * y las delega al servicio, que aplica las reglas de negocio.
 */
public class ControladorMateria {

    private final MateriaService servicio;

    public ControladorMateria(MateriaService servicio) {
        this.servicio = servicio;
    }

    public List<Materia> listar() {
        return servicio.obtenerTodasLasMaterias();
    }

    public Optional<Materia> buscar(int id) {
        return servicio.buscarMateriaPorId(id);
    }

    public void registrar(Materia entidad) {
        servicio.registrarMateria(entidad);
    }

    public void actualizar(Materia entidad) {
        servicio.actualizarMateria(entidad);
    }

    public void eliminar(int id) {
        servicio.eliminarMateria(id);
    }
}
