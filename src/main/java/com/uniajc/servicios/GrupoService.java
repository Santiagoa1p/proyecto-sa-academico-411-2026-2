package com.uniajc.servicios;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Grupo;

import java.util.List;
import java.util.Optional;

public class GrupoService {
    private final GrupoDao grupoDao;
    private final MateriaDao materiaDao;
    private final DocenteDao docenteDao;

    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao) {
        this.grupoDao = grupoDao;
        this.materiaDao = materiaDao;
        this.docenteDao = docenteDao;
    }

    public void registrarGrupo(Grupo grupo) {
        validar(grupo);
        grupoDao.guardar(grupo);
    }

    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.obtenerTodos();
    }

    public Optional<Grupo> buscarGrupoPorId(int id) {
        Validaciones.idPositivo(id, "grupo");
        return grupoDao.buscarPorId(id);
    }

    public void actualizarGrupo(Grupo grupo) {
        validar(grupo);
        Validaciones.idPositivo(grupo.getIdGrupo(), "grupo");
        exigirGrupoExistente(grupo.getIdGrupo());
        grupoDao.actualizar(grupo);
    }

    public void eliminarGrupo(int id) {
        Validaciones.idPositivo(id, "grupo");
        exigirGrupoExistente(id);
        grupoDao.eliminar(id);
    }

    private void exigirGrupoExistente(int id) {
        if (grupoDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe el grupo con ID " + id + ".");
        }
    }

    private void validar(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo es obligatorio.");
        }
        Validaciones.idPositivo(grupo.getIdMateria(), "materia");
        Validaciones.idPositivo(grupo.getIdDocente(), "docente");
        Validaciones.requerido(grupo.getAula(), "aula");
        Validaciones.requerido(grupo.getHorario(), "horario");
        if (materiaDao.buscarPorId(grupo.getIdMateria()).isEmpty()) {
            throw new IllegalArgumentException("No existe la materia indicada.");
        }
        if (docenteDao.buscarPorId(grupo.getIdDocente()).isEmpty()) {
            throw new IllegalArgumentException("No existe el docente indicado.");
        }
    }
}
