package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.Estudiante;

/**
 * Lógica de negocio de los estudiantes (capa <b>Servicio</b>).
 *
 * <p>Decide si una operación está permitida antes de llegar a la base de datos.
 * Si algo no cumple una regla, lanza {@link IllegalArgumentException} con un mensaje
 * para el usuario y la operación <b>no</b> llega al DAO.</p>
 *
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>Nombre y apellido obligatorios, de 2 a 100 caracteres y solo letras.</li>
 *   <li>Email obligatorio, con formato válido; se guarda en minúsculas.</li>
 *   <li>No puede haber dos estudiantes con el mismo email (sin importar mayúsculas).</li>
 *   <li>Solo se actualiza o elimina un estudiante que exista.</li>
 *   <li>No se elimina un estudiante con inscripciones, para no dejar inscripciones
 *       apuntando a un estudiante que ya no existe.</li>
 * </ul>
 *
 * <p>Los DAO se reciben por el constructor (<b>inyección de dependencias</b>): el
 * servicio no los crea por dentro. Gracias a eso, en las pruebas se le pueden pasar
 * DAO falsos que guardan en memoria en lugar de Neon.</p>
 */
public class EstudianteService {

    /** DAO para leer y guardar estudiantes. */
    private final EstudianteDao estudianteDao;
    /** DAO de inscripciones, necesario para la regla de no borrar estudiantes inscritos. */
    private final InscripcionCursoDao inscripcionDao;

    /**
     * Crea el servicio con el DAO de estudiantes y un DAO de inscripciones real.
     *
     * @param estudianteDao DAO de estudiantes
     */
    public EstudianteService(EstudianteDao estudianteDao) {
        // this(...) llama al otro constructor para no repetir código.
        this(estudianteDao, new InscripcionCursoDao());
    }

    /**
     * Crea el servicio con todos sus DAO. Es el constructor que usan las pruebas.
     *
     * @param estudianteDao  DAO de estudiantes
     * @param inscripcionDao DAO de inscripciones
     */
    public EstudianteService(EstudianteDao estudianteDao, InscripcionCursoDao inscripcionDao) {
        this.estudianteDao = estudianteDao;
        this.inscripcionDao = inscripcionDao;
    }

    /**
     * Registra un estudiante nuevo, después de limpiar y validar sus datos.
     *
     * @param estudiante datos del estudiante; al terminar queda con su ID asignado
     * @throws IllegalArgumentException si algún dato no es válido o el email ya está en uso
     */
    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);
        // idPropio = 0: es un estudiante nuevo, así que cualquier coincidencia es otro estudiante.
        validarEmailDisponible(estudiante.getEmail(), 0);
        estudianteDao.guardar(estudiante);
    }

    /**
     * Devuelve todos los estudiantes. No tiene reglas: solo se los pide al DAO.
     *
     * @return lista de estudiantes (vacía si no hay)
     */
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    /**
     * Busca un estudiante por su ID.
     *
     * @param id identificador (debe ser mayor que 0)
     * @return el estudiante si existe, o vacío si no
     * @throws IllegalArgumentException si el ID es 0 o negativo
     */
    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        Validaciones.idValido(id, "estudiante");
        return estudianteDao.buscarPorId(id);
    }

    /**
     * Actualiza los datos de un estudiante existente.
     *
     * <p>El email puede seguir siendo el mismo que ya tenía; solo se rechaza si
     * pertenece a <b>otro</b> estudiante.</p>
     *
     * @param estudiante datos nuevos; su ID indica a quién se actualiza
     * @throws IllegalArgumentException si no existe, si algún dato no es válido o si el email es de otro estudiante
     */
    public void actualizarEstudiante(Estudiante estudiante) {
        Validaciones.obligatorio(estudiante, "El estudiante");
        exigirExistente(estudiante.getIdEstudiante());
        validarEstudiante(estudiante);
        validarEmailDisponible(estudiante.getEmail(), estudiante.getIdEstudiante());
        estudianteDao.actualizar(estudiante);
    }

    /**
     * Elimina un estudiante, siempre que exista y no tenga inscripciones.
     *
     * @param id identificador del estudiante
     * @throws IllegalArgumentException si no existe o si tiene inscripciones
     */
    public void eliminarEstudiante(int id) {
        exigirExistente(id);
        // Regla de integridad: primero hay que borrar sus inscripciones.
        if (inscripcionDao.contarPorEstudiante(id) > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el estudiante porque tiene inscripciones. Elimínalas primero.");
        }
        estudianteDao.eliminar(id);
    }

    /**
     * Comprueba que el ID sea válido y que el estudiante exista en la base.
     *
     * @throws IllegalArgumentException si el ID no es válido o no existe
     */
    private void exigirExistente(int id) {
        Validaciones.idValido(id, "estudiante");
        if (estudianteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID " + id + ".");
        }
    }

    /**
     * Limpia los textos (espacios sobrantes) y comprueba su formato.
     * El email se pasa a minúsculas para que la comparación de repetidos sea consistente.
     *
     * @throws IllegalArgumentException si el estudiante es null o algún campo no es válido
     */
    private void validarEstudiante(Estudiante estudiante) {
        Validaciones.obligatorio(estudiante, "El estudiante");
        estudiante.setNombre(Validaciones.limpiar(estudiante.getNombre()));
        estudiante.setApellido(Validaciones.limpiar(estudiante.getApellido()));
        estudiante.setEmail(Validaciones.limpiar(estudiante.getEmail()).toLowerCase());

        Validaciones.nombrePropio(estudiante.getNombre(), "El nombre");
        Validaciones.nombrePropio(estudiante.getApellido(), "El apellido");
        Validaciones.email(estudiante.getEmail());
    }

    /**
     * Comprueba que ningún <b>otro</b> estudiante tenga ese email.
     *
     * <p>Usa la API de {@link Optional}: {@code filter} descarta el resultado si es el
     * mismo estudiante que se está editando, e {@code ifPresent} lanza el error solo si
     * quedó alguien (es decir, el email es de otra persona).</p>
     *
     * @param email    email a revisar
     * @param idPropio ID del estudiante que se edita, o 0 si es uno nuevo
     * @throws IllegalArgumentException si otro estudiante ya usa ese email
     */
    private void validarEmailDisponible(String email, int idPropio) {
        estudianteDao.buscarPorEmail(email)
                .filter(otro -> otro.getIdEstudiante() != idPropio)
                .ifPresent(otro -> {
                    throw new IllegalArgumentException("Ya existe un estudiante con el email " + email + ".");
                });
    }
}
