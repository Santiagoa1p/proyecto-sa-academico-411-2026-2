package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Grupo;
import com.uniajc.servicios.GrupoService;

/**
 * Orquesta las peticiones de las vistas (consola o Swing) sobre grupos
 * y las delega al servicio, que aplica las reglas de negocio.
 */
public class ControladorGrupo {

    private final GrupoService servicio;

    public ControladorGrupo(GrupoService servicio) {
        this.servicio = servicio;
    }

    public List<Grupo> listar() {
        return servicio.obtenerTodosLosGrupos();
    }

    public Optional<Grupo> buscar(int id) {
        return servicio.buscarGrupoPorId(id);
    }

    public void registrar(Grupo entidad) {
        servicio.registrarGrupo(entidad);
    }

    public void actualizar(Grupo entidad) {
        servicio.actualizarGrupo(entidad);
    }

    public void eliminar(int id) {
        servicio.eliminarGrupo(id);
    }
}
