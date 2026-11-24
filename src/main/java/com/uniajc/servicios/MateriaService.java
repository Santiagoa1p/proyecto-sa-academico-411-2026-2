package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

/**
 * Lógica de negocio de las materias (capa <b>Servicio</b>).
 *
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>Nombre obligatorio, de 3 a 100 caracteres.</li>
 *   <li>No puede haber dos materias con el mismo nombre (sin importar mayúsculas).</li>
 *   <li>Créditos entre {@link #CREDITOS_MINIMOS} y {@link #CREDITOS_MAXIMOS}.</li>
 *   <li>Solo se actualiza o elimina una materia que exista.</li>
 *   <li>No se elimina una materia que tenga grupos, para no dejar grupos sin materia.</li>
 * </ul>
 */
public class MateriaService {

    /** Mínimo de créditos permitido. Es {@code public} para que las vistas puedan mostrarlo. */
    public static final int CREDITOS_MINIMOS = 1;
    /** Máximo de créditos permitido. */
    public static final int CREDITOS_MAXIMOS = 10;

    /** DAO para leer y guardar materias. */
    private final MateriaDao materiaDao;
    /** DAO de grupos, necesario para la regla de no borrar materias en uso. */
    private final GrupoDao grupoDao;

    /**
     * Crea el servicio con el DAO de materias y un DAO de grupos real.
     *
     * @param materiaDao DAO de materias
     */
    public MateriaService(MateriaDao materiaDao) {
        this(materiaDao, new GrupoDao());
    }

    /**
     * Crea el servicio con todos sus DAO (inyección de dependencias). Lo usan las pruebas.
     *
     * @param materiaDao DAO de materias
     * @param grupoDao   DAO de grupos
     */
    public MateriaService(MateriaDao materiaDao, GrupoDao grupoDao) {
        this.materiaDao = materiaDao;
        this.grupoDao = grupoDao;
    }

    /**
     * Registra una materia nueva, después de validar sus datos y que el nombre no esté en uso.
     *
     * @param materia datos de la materia; al terminar queda con su ID asignado
     * @throws IllegalArgumentException si algún dato no es válido o el nombre ya existe
     */
    public void registrarMateria(Materia materia) {
        validarMateria(materia);
        validarNombreDisponible(materia.getNombreMateria(), 0);
        materiaDao.guardar(materia);
    }

    /**
     * Devuelve todas las materias.
     *
     * @return lista de materias (vacía si no hay)
     */
    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.obtenerTodos();
    }

    /**
     * Busca una materia por su ID.
     *
     * @param id identificador (debe ser mayor que 0)
     * @return la materia si existe, o vacío si no
     * @throws IllegalArgumentException si el ID es 0 o negativo
     */
    public Optional<Materia> buscarMateriaPorId(int id) {
        Validaciones.idValido(id, "materia");
        return materiaDao.buscarPorId(id);
    }

    /**
     * Actualiza una materia existente. Puede conservar su propio nombre, pero no
     * tomar el de otra materia.
     *
     * @param materia datos nuevos; su ID indica cuál se actualiza
     * @throws IllegalArgumentException si no existe, si algún dato no es válido o si el nombre es de otra materia
     */
    public void actualizarMateria(Materia materia) {
        Validaciones.obligatorio(materia, "La materia");
        exigirExistente(materia.getIdMateria());
        validarMateria(materia);
        validarNombreDisponible(materia.getNombreMateria(), materia.getIdMateria());
        materiaDao.actualizar(materia);
    }

    /**
     * Elimina una materia, siempre que exista y no tenga grupos.
     *
     * @param id identificador de la materia
     * @throws IllegalArgumentException si no existe o si tiene grupos
     */
    public void eliminarMateria(int id) {
        exigirExistente(id);
        // Regla de integridad: un grupo no puede quedarse sin materia.
        if (grupoDao.contarPorMateria(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la materia porque tiene grupos. Elimina esos grupos primero.");
        }
        materiaDao.eliminar(id);
    }

    /**
     * Comprueba que el ID sea válido y que la materia exista en la base.
     *
     * @throws IllegalArgumentException si el ID no es válido o no existe
     */
    private void exigirExistente(int id) {
        Validaciones.idValido(id, "materia");
        if (materiaDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una materia con ID " + id + ".");
        }
    }

    /**
     * Limpia el nombre y comprueba su longitud y el rango de créditos.
     *
     * @throws IllegalArgumentException si la materia es null o algún dato no es válido
     */
    private void validarMateria(Materia materia) {
        Validaciones.obligatorio(materia, "La materia");
        materia.setNombreMateria(Validaciones.limpiar(materia.getNombreMateria()));
        Validaciones.texto(materia.getNombreMateria(), "El nombre de la materia", 3, 100);
        if (materia.getCreditos() < CREDITOS_MINIMOS || materia.getCreditos() > CREDITOS_MAXIMOS) {
            throw new IllegalArgumentException(
                    "Los créditos deben estar entre " + CREDITOS_MINIMOS + " y " + CREDITOS_MAXIMOS + ".");
        }
    }

    /**
     * Comprueba que ninguna <b>otra</b> materia tenga ese nombre. Funciona igual que
     * {@code validarEmailDisponible} de {@link EstudianteService}: {@code filter}
     * descarta la propia materia e {@code ifPresent} lanza el error si queda otra.
     *
     * @param nombre   nombre a revisar
     * @param idPropio ID de la materia que se edita, o 0 si es nueva
     * @throws IllegalArgumentException si otra materia ya tiene ese nombre
     */
    private void validarNombreDisponible(String nombre, int idPropio) {
        materiaDao.buscarPorNombre(nombre)
                .filter(otra -> otra.getIdMateria() != idPropio)
                .ifPresent(otra -> {
                    throw new IllegalArgumentException("Ya existe una materia llamada " + nombre + ".");
                });
    }
}
