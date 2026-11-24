package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.modelo.InscripcionCurso;

/**
 * Lógica de negocio de las inscripciones (capa <b>Servicio</b>).
 *
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>El estudiante y el grupo indicados deben existir.</li>
 *   <li>Un estudiante no puede inscribirse dos veces en el mismo grupo.</li>
 *   <li>El estado debe ser ACTIVO, APROBADO, REPROBADO o CANCELADO (si viene vacío, queda ACTIVO).</li>
 *   <li>La nota es opcional; si se pone, debe estar entre 0.0 y 5.0.</li>
 *   <li>APROBADO exige nota de 3.0 o más; REPROBADO exige nota menor que 3.0.</li>
 * </ul>
 */
public class InscripcionCursoService {

    /** Nota más baja posible en la escala colombiana. */
    public static final float NOTA_MINIMA = 0.0f;
    /** Nota más alta posible. */
    public static final float NOTA_MAXIMA = 5.0f;
    /** Nota mínima para aprobar. */
    public static final float NOTA_APROBATORIA = 3.0f;
    /**
     * Estados válidos. Es un {@link Set} (conjunto) porque solo interesa preguntar
     * "¿este estado está permitido?" con {@code contains}. {@code Set.of} crea un
     * conjunto que no se puede modificar.
     */
    public static final Set<String> ESTADOS = Set.of(
            InscripcionCurso.ACTIVO, InscripcionCurso.APROBADO,
            InscripcionCurso.REPROBADO, InscripcionCurso.CANCELADO);

    /** DAO para leer y guardar inscripciones. */
    private final InscripcionCursoDao inscripcionDao;
    /** DAO de estudiantes, para comprobar que el estudiante exista. */
    private final EstudianteDao estudianteDao;
    /** DAO de grupos, para comprobar que el grupo exista. */
    private final GrupoDao grupoDao;

    /**
     * Crea el servicio con sus DAO (inyección de dependencias).
     *
     * @param inscripcionDao DAO de inscripciones
     * @param estudianteDao  DAO de estudiantes
     * @param grupoDao       DAO de grupos
     */
    public InscripcionCursoService(InscripcionCursoDao inscripcionDao, EstudianteDao estudianteDao, GrupoDao grupoDao) {
        this.inscripcionDao = inscripcionDao;
        this.estudianteDao = estudianteDao;
        this.grupoDao = grupoDao;
    }

    /**
     * Inscribe a un estudiante en un grupo, después de validar todas las reglas.
     *
     * @param inscripcion datos de la inscripción; al terminar queda con su ID asignado
     * @throws IllegalArgumentException si alguna regla no se cumple
     */
    public void registrarInscripcion(InscripcionCurso inscripcion) {
        validarInscripcion(inscripcion);
        inscripcionDao.guardar(inscripcion);
    }

    /**
     * Devuelve todas las inscripciones.
     *
     * @return lista de inscripciones (vacía si no hay)
     */
    public List<InscripcionCurso> obtenerTodasLasInscripciones() {
        return inscripcionDao.obtenerTodos();
    }

    /**
     * Busca una inscripción por su ID.
     *
     * @param id identificador (debe ser mayor que 0)
     * @return la inscripción si existe, o vacío si no
     * @throws IllegalArgumentException si el ID es 0 o negativo
     */
    public Optional<InscripcionCurso> buscarInscripcionPorId(int id) {
        Validaciones.idValido(id, "inscripción");
        return inscripcionDao.buscarPorId(id);
    }

    /**
     * Actualiza una inscripción existente (por ejemplo, para ponerle nota y estado final).
     *
     * @param inscripcion datos nuevos; su ID indica cuál se actualiza
     * @throws IllegalArgumentException si no existe o si alguna regla no se cumple
     */
    public void actualizarInscripcion(InscripcionCurso inscripcion) {
        Validaciones.obligatorio(inscripcion, "La inscripción");
        exigirExistente(inscripcion.getIdInscripcion());
        validarInscripcion(inscripcion);
        inscripcionDao.actualizar(inscripcion);
    }

    /**
     * Elimina una inscripción. No tiene restricciones extra porque ninguna otra
     * tabla depende de las inscripciones.
     *
     * @param id identificador de la inscripción
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarInscripcion(int id) {
        exigirExistente(id);
        inscripcionDao.eliminar(id);
    }

    /**
     * Comprueba que el ID sea válido y que la inscripción exista en la base.
     *
     * @throws IllegalArgumentException si el ID no es válido o no existe
     */
    private void exigirExistente(int id) {
        Validaciones.idValido(id, "inscripción");
        if (inscripcionDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una inscripción con ID " + id + ".");
        }
    }

    /**
     * Aplica todas las reglas, en este orden: IDs válidos → el estudiante y el grupo
     * existen → no está inscrito ya → nota y estado coherentes.
     *
     * @throws IllegalArgumentException en la primera regla que no se cumpla
     */
    private void validarInscripcion(InscripcionCurso inscripcion) {
        Validaciones.obligatorio(inscripcion, "La inscripción");
        Validaciones.idValido(inscripcion.getIdEstudiante(), "estudiante");
        Validaciones.idValido(inscripcion.getIdGrupo(), "grupo");

        // Integridad referencial: el estudiante y el grupo deben existir.
        if (estudianteDao.buscarPorId(inscripcion.getIdEstudiante()).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID " + inscripcion.getIdEstudiante() + ".");
        }
        if (grupoDao.buscarPorId(inscripcion.getIdGrupo()).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID " + inscripcion.getIdGrupo() + ".");
        }
        // Sin duplicados: si ya hay una inscripción de este estudiante en este grupo
        // (y no es la misma que se está editando), se rechaza.
        inscripcionDao.buscarPorEstudianteYGrupo(inscripcion.getIdEstudiante(), inscripcion.getIdGrupo())
                .filter(otra -> otra.getIdInscripcion() != inscripcion.getIdInscripcion())
                .ifPresent(otra -> {
                    throw new IllegalArgumentException("El estudiante ya está inscrito en ese grupo (inscripción "
                            + otra.getIdInscripcion() + ").");
                });

        validarNotaYEstado(inscripcion);
    }

    /**
     * Normaliza el estado (mayúsculas, ACTIVO si viene vacío) y comprueba que la nota
     * sea coherente con él.
     *
     * <p>{@code isNaN()} descarta el valor especial "Not a Number" de los
     * {@code float}, que no es mayor ni menor que nada y se colaría en las demás
     * comparaciones.</p>
     *
     * @throws IllegalArgumentException si el estado no existe, la nota está fuera de
     *                                  rango o no corresponde al estado
     */
    private void validarNotaYEstado(InscripcionCurso inscripcion) {
        String estado = Validaciones.limpiar(inscripcion.getEstado()).toUpperCase();
        if (estado.isEmpty()) {
            estado = InscripcionCurso.ACTIVO;
        }
        if (!ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("El estado debe ser uno de: ACTIVO, APROBADO, REPROBADO o CANCELADO.");
        }
        inscripcion.setEstado(estado);

        Float nota = inscripcion.getNotaFinal();
        if (nota != null && (nota.isNaN() || nota < NOTA_MINIMA || nota > NOTA_MAXIMA)) {
            throw new IllegalArgumentException("La nota final debe estar entre 0.0 y 5.0.");
        }
        if (InscripcionCurso.APROBADO.equals(estado) && (nota == null || nota < NOTA_APROBATORIA)) {
            throw new IllegalArgumentException("Para marcar APROBADO la nota debe ser 3.0 o mayor.");
        }
        if (InscripcionCurso.REPROBADO.equals(estado) && (nota == null || nota >= NOTA_APROBATORIA)) {
            throw new IllegalArgumentException("Para marcar REPROBADO la nota debe ser menor que 3.0.");
        }
    }
}
