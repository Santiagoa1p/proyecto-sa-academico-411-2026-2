package com.uniajc.controlador;

import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

import java.util.List;
import java.util.Optional;

public class ControladorMateria {
    private final MateriaService servicio;

    public ControladorMateria(MateriaService servicio) {
        this.servicio = servicio;
    }

    public void registrar(Materia materia) {
        servicio.registrarMateria(materia);
    }

    public List<Materia> listar() {
        return servicio.obtenerTodasLasMaterias();
    }

    public Optional<Materia> buscar(int id) {
        return servicio.buscarMateriaPorId(id);
    }

    public void actualizar(Materia materia) {
        servicio.actualizarMateria(materia);
    }

    public void eliminar(int id) {
        servicio.eliminarMateria(id);
    }
}
