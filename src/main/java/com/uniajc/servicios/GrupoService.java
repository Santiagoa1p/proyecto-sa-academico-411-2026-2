package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Grupo;

/**
 * Lógica de negocio de grupos. Un grupo une una materia con un docente en un
 * aula y horario; ni el docente ni el aula pueden estar ocupados dos veces en
 * el mismo horario.
 */
public class GrupoService {

    private final GrupoDao grupoDao;
    private final MateriaDao materiaDao;
    private final DocenteDao docenteDao;
    private final InscripcionCursoDao inscripcionDao;

    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao) {
        this(grupoDao, materiaDao, docenteDao, new InscripcionCursoDao());
    }

    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao,
                        InscripcionCursoDao inscripcionDao) {
        this.grupoDao = grupoDao;
        this.materiaDao = materiaDao;
        this.docenteDao = docenteDao;
        this.inscripcionDao = inscripcionDao;
    }

    public void registrarGrupo(Grupo grupo) {
        validarGrupo(grupo);
        grupoDao.guardar(grupo);
    }

    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.obtenerTodos();
    }

    public Optional<Grupo> buscarGrupoPorId(int id) {
        Validaciones.idValido(id, "grupo");
        return grupoDao.buscarPorId(id);
    }

    public void actualizarGrupo(Grupo grupo) {
        Validaciones.obligatorio(grupo, "El grupo");
        exigirExistente(grupo.getIdGrupo());
        validarGrupo(grupo);
        grupoDao.actualizar(grupo);
    }

    public void eliminarGrupo(int id) {
        exigirExistente(id);
        if (inscripcionDao.contarPorGrupo(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el grupo porque tiene estudiantes inscritos. Elimina esas inscripciones primero.");
        }
        grupoDao.eliminar(id);
    }

    private void exigirExistente(int id) {
        Validaciones.idValido(id, "grupo");
        if (grupoDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID " + id + ".");
        }
    }

    private void validarGrupo(Grupo grupo) {
        Validaciones.obligatorio(grupo, "El grupo");
        grupo.setAula(Validaciones.limpiar(grupo.getAula()).toUpperCase());
        grupo.setHorario(Validaciones.limpiar(grupo.getHorario()));

        Validaciones.idValido(grupo.getIdMateria(), "materia");
        Validaciones.idValido(grupo.getIdDocente(), "docente");
        if (materiaDao.buscarPorId(grupo.getIdMateria()).isEmpty()) {
            throw new IllegalArgumentException("No existe una materia con ID " + grupo.getIdMateria() + ".");
        }
        if (docenteDao.buscarPorId(grupo.getIdDocente()).isEmpty()) {
            throw new IllegalArgumentException("No existe un docente con ID " + grupo.getIdDocente() + ".");
        }
        Validaciones.texto(grupo.getAula(), "El aula", 1, 50);
        Validaciones.texto(grupo.getHorario(), "El horario", 3, 100);
        validarCrucesDeHorario(grupo);
    }

    private void validarCrucesDeHorario(Grupo grupo) {
        for (Grupo otro : grupoDao.obtenerTodos()) {
            if (otro.getIdGrupo() == grupo.getIdGrupo()
                    || !otro.getHorario().equalsIgnoreCase(grupo.getHorario())) {
                continue;
            }
            if (otro.getIdDocente() == grupo.getIdDocente()) {
                throw new IllegalArgumentException(
                        "El docente ya tiene el grupo " + otro.getIdGrupo() + " en el horario " + grupo.getHorario() + ".");
            }
            if (otro.getAula().equalsIgnoreCase(grupo.getAula())) {
                throw new IllegalArgumentException(
                        "El aula " + grupo.getAula() + " ya está ocupada en el horario " + grupo.getHorario()
                                + " (grupo " + otro.getIdGrupo() + ").");
            }
        }
    }
}
