package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Grupo;

/**
 * Lógica de negocio de los grupos (capa <b>Servicio</b>).
 *
 * <p>Un grupo une una materia con un docente en un aula y un horario.</p>
 *
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>La materia y el docente indicados deben existir (integridad referencial: la
 *       base del profesor no tiene llaves foráneas, así que la revisa el servicio).</li>
 *   <li>Aula obligatoria (1 a 50 caracteres; se guarda en mayúsculas) y horario
 *       obligatorio (3 a 100 caracteres).</li>
 *   <li>Un docente no puede tener dos grupos en el mismo horario.</li>
 *   <li>Un aula no puede estar ocupada por dos grupos en el mismo horario.</li>
 *   <li>No se elimina un grupo con estudiantes inscritos.</li>
 * </ul>
 */
public class GrupoService {

    /** DAO para leer y guardar grupos. */
    private final GrupoDao grupoDao;
    /** DAO de materias, para comprobar que la materia exista. */
    private final MateriaDao materiaDao;
    /** DAO de docentes, para comprobar que el docente exista. */
    private final DocenteDao docenteDao;
    /** DAO de inscripciones, para no borrar grupos con estudiantes. */
    private final InscripcionCursoDao inscripcionDao;

    /**
     * Crea el servicio con un DAO de inscripciones real.
     *
     * @param grupoDao   DAO de grupos
     * @param materiaDao DAO de materias
     * @param docenteDao DAO de docentes
     */
    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao) {
        this(grupoDao, materiaDao, docenteDao, new InscripcionCursoDao());
    }

    /**
     * Crea el servicio con todos sus DAO (inyección de dependencias). Lo usan las pruebas.
     *
     * @param grupoDao       DAO de grupos
     * @param materiaDao     DAO de materias
     * @param docenteDao     DAO de docentes
     * @param inscripcionDao DAO de inscripciones
     */
    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao,
                        InscripcionCursoDao inscripcionDao) {
        this.grupoDao = grupoDao;
        this.materiaDao = materiaDao;
        this.docenteDao = docenteDao;
        this.inscripcionDao = inscripcionDao;
    }

    /**
     * Registra un grupo nuevo, después de validar sus datos y que no haya cruces de horario.
     *
     * @param grupo datos del grupo; al terminar queda con su ID asignado
     * @throws IllegalArgumentException si algún dato no es válido o hay un cruce
     */
    public void registrarGrupo(Grupo grupo) {
        validarGrupo(grupo);
        grupoDao.guardar(grupo);
    }

    /**
     * Devuelve todos los grupos.
     *
     * @return lista de grupos (vacía si no hay)
     */
    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.obtenerTodos();
    }

    /**
     * Busca un grupo por su ID.
     *
     * @param id identificador (debe ser mayor que 0)
     * @return el grupo si existe, o vacío si no
     * @throws IllegalArgumentException si el ID es 0 o negativo
     */
    public Optional<Grupo> buscarGrupoPorId(int id) {
        Validaciones.idValido(id, "grupo");
        return grupoDao.buscarPorId(id);
    }

    /**
     * Actualiza un grupo existente, con las mismas validaciones que al registrar.
     *
     * @param grupo datos nuevos; su ID indica cuál se actualiza
     * @throws IllegalArgumentException si no existe, si algún dato no es válido o hay un cruce
     */
    public void actualizarGrupo(Grupo grupo) {
        Validaciones.obligatorio(grupo, "El grupo");
        exigirExistente(grupo.getIdGrupo());
        validarGrupo(grupo);
        grupoDao.actualizar(grupo);
    }

    /**
     * Elimina un grupo, siempre que exista y no tenga estudiantes inscritos.
     *
     * @param id identificador del grupo
     * @throws IllegalArgumentException si no existe o si tiene inscripciones
     */
    public void eliminarGrupo(int id) {
        exigirExistente(id);
        // Regla de integridad: una inscripción no puede quedarse sin grupo.
        if (inscripcionDao.contarPorGrupo(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el grupo porque tiene estudiantes inscritos. Elimina esas inscripciones primero.");
        }
        grupoDao.eliminar(id);
    }

    /**
     * Comprueba que el ID sea válido y que el grupo exista en la base.
     *
     * @throws IllegalArgumentException si el ID no es válido o no existe
     */
    private void exigirExistente(int id) {
        Validaciones.idValido(id, "grupo");
        if (grupoDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID " + id + ".");
        }
    }

    /**
     * Limpia los textos y aplica todas las reglas del grupo, en este orden:
     * IDs válidos → la materia y el docente existen → aula y horario válidos → sin cruces.
     *
     * @throws IllegalArgumentException en la primera regla que no se cumpla
     */
    private void validarGrupo(Grupo grupo) {
        Validaciones.obligatorio(grupo, "El grupo");
        // El aula va en mayúsculas para que "a-201" y "A-201" se consideren la misma.
        grupo.setAula(Validaciones.limpiar(grupo.getAula()).toUpperCase());
        grupo.setHorario(Validaciones.limpiar(grupo.getHorario()));

        Validaciones.idValido(grupo.getIdMateria(), "materia");
        Validaciones.idValido(grupo.getIdDocente(), "docente");
        // Integridad referencial: no se puede crear un grupo de una materia o un docente inexistente.
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

    /**
     * Revisa los demás grupos buscando choques en el mismo horario.
     *
     * <p>Recorre todos los grupos y se salta (con {@code continue}) el propio grupo
     * (cuando se está editando) y los que tienen otro horario. Entre los que quedan,
     * que comparten horario, lanza error si alguno tiene el mismo docente o la misma aula.</p>
     *
     * <p>El horario se compara como texto exacto (sin distinguir mayúsculas), así que
     * "Lunes 7-9" y "Lunes 7:00-9:00" se consideran horarios distintos.</p>
     *
     * @throws IllegalArgumentException si el docente o el aula ya están ocupados en ese horario
     */
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
