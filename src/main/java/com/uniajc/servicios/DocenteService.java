package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.modelo.Docente;

/**
 * Lógica de negocio de los docentes (capa <b>Servicio</b>).
 *
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>Nombre obligatorio, de 2 a 100 caracteres y solo letras.</li>
 *   <li>Especialidad obligatoria, de 2 a 100 caracteres.</li>
 *   <li>Solo se actualiza o elimina un docente que exista.</li>
 *   <li>No se elimina un docente con grupos asignados, para no dejar grupos sin docente.</li>
 * </ul>
 *
 * <p>Los DAO se reciben por el constructor (inyección de dependencias), igual que en
 * {@link EstudianteService}.</p>
 */
public class DocenteService {

    /** DAO para leer y guardar docentes. */
    private final DocenteDao docenteDao;
    /** DAO de grupos, necesario para la regla de no borrar docentes con grupos. */
    private final GrupoDao grupoDao;

    /**
     * Crea el servicio con el DAO de docentes y un DAO de grupos real.
     *
     * @param docenteDao DAO de docentes
     */
    public DocenteService(DocenteDao docenteDao) {
        this(docenteDao, new GrupoDao());
    }

    /**
     * Crea el servicio con todos sus DAO. Es el constructor que usan las pruebas.
     *
     * @param docenteDao DAO de docentes
     * @param grupoDao   DAO de grupos
     */
    public DocenteService(DocenteDao docenteDao, GrupoDao grupoDao) {
        this.docenteDao = docenteDao;
        this.grupoDao = grupoDao;
    }

    /**
     * Registra un docente nuevo, después de limpiar y validar sus datos.
     *
     * @param docente datos del docente; al terminar queda con su ID asignado
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void registrarDocente(Docente docente) {
        validarDocente(docente);
        docenteDao.guardar(docente);
    }

    /**
     * Devuelve todos los docentes.
     *
     * @return lista de docentes (vacía si no hay)
     */
    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.obtenerTodos();
    }

    /**
     * Busca un docente por su ID.
     *
     * @param id identificador (debe ser mayor que 0)
     * @return el docente si existe, o vacío si no
     * @throws IllegalArgumentException si el ID es 0 o negativo
     */
    public Optional<Docente> buscarDocentePorId(int id) {
        Validaciones.idValido(id, "docente");
        return docenteDao.buscarPorId(id);
    }

    /**
     * Actualiza los datos de un docente existente.
     *
     * @param docente datos nuevos; su ID indica a quién se actualiza
     * @throws IllegalArgumentException si no existe o si algún dato no es válido
     */
    public void actualizarDocente(Docente docente) {
        Validaciones.obligatorio(docente, "El docente");
        exigirExistente(docente.getIdDocente());
        validarDocente(docente);
        docenteDao.actualizar(docente);
    }

    /**
     * Elimina un docente, siempre que exista y no tenga grupos asignados.
     *
     * @param id identificador del docente
     * @throws IllegalArgumentException si no existe o si tiene grupos
     */
    public void eliminarDocente(int id) {
        exigirExistente(id);
        // Regla de integridad: un grupo no puede quedarse sin docente.
        if (grupoDao.contarPorDocente(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el docente porque tiene grupos asignados. Reasígnalos o elimínalos primero.");
        }
        docenteDao.eliminar(id);
    }

    /**
     * Comprueba que el ID sea válido y que el docente exista en la base.
     *
     * @throws IllegalArgumentException si el ID no es válido o no existe
     */
    private void exigirExistente(int id) {
        Validaciones.idValido(id, "docente");
        if (docenteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un docente con ID " + id + ".");
        }
    }

    /**
     * Limpia los textos y comprueba su formato.
     *
     * @throws IllegalArgumentException si el docente es null o algún campo no es válido
     */
    private void validarDocente(Docente docente) {
        Validaciones.obligatorio(docente, "El docente");
        docente.setNombre(Validaciones.limpiar(docente.getNombre()));
        docente.setEspecialidad(Validaciones.limpiar(docente.getEspecialidad()));

        Validaciones.nombrePropio(docente.getNombre(), "El nombre");
        Validaciones.texto(docente.getEspecialidad(), "La especialidad", 2, 100);
    }
}
