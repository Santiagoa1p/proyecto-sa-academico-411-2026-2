package com.uniajc.controlador;

import com.uniajc.modelo.Grupo;
import com.uniajc.servicios.GrupoService;

import java.util.List;
import java.util.Optional;

public class ControladorGrupo {
    private final GrupoService servicio;

    public ControladorGrupo(GrupoService servicio) {
        this.servicio = servicio;
    }

    public void registrar(Grupo grupo) {
        servicio.registrarGrupo(grupo);
    }

    public List<Grupo> listar() {
        return servicio.obtenerTodosLosGrupos();
    }

    public Optional<Grupo> buscar(int id) {
        return servicio.buscarGrupoPorId(id);
    }

    public void actualizar(Grupo grupo) {
        servicio.actualizarGrupo(grupo);
    }

    public void eliminar(int id) {
        servicio.eliminarGrupo(id);
    }
}
