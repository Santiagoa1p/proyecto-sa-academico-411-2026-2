package com.uniajc.modelo;

/**
 * Entidad (POJO) que representa la inscripción de un estudiante en un grupo.
 *
 * <p>Capa <b>Modelo</b>: solo guarda datos. Cada objeto corresponde a una fila de
 * la tabla {@code "practica-mvc".inscripciones}.</p>
 *
 * <p>Es la entidad que une a {@link Estudiante} con {@link Grupo}: un estudiante
 * se inscribe en muchos grupos y un grupo contiene muchos estudiantes (relación
 * muchos a muchos resuelta con esta tabla intermedia).</p>
 *
 * <p>La nota final es opcional ({@code null}) mientras el curso está en progreso.</p>
 */
public class InscripcionCurso {

    // Estados posibles de una inscripción. Se declaran como constantes
    // (public static final) para no escribir el texto a mano en cada lugar
    // y evitar errores de tipeo como "APROVADO".

    /** El estudiante está cursando la materia. Es el estado por defecto. */
    public static final String ACTIVO = "ACTIVO";
    /** El estudiante terminó con nota mayor o igual a 3.0. */
    public static final String APROBADO = "APROBADO";
    /** El estudiante terminó con nota menor a 3.0. */
    public static final String REPROBADO = "REPROBADO";
    /** La inscripción se canceló antes de terminar. */
    public static final String CANCELADO = "CANCELADO";

    /** Identificador único generado por la base de datos; 0 mientras no se haya guardado. */
    private int idInscripcion;
    /** ID del estudiante inscrito. */
    private int idEstudiante;
    /** ID del grupo en el que se inscribió. */
    private int idGrupo;
    /**
     * Nota final de 0.0 a 5.0. Es {@code Float} (clase envoltorio) y no {@code float}
     * (primitivo) porque necesita poder valer {@code null} cuando aún no hay nota.
     */
    private Float notaFinal;
    /** Estado actual; empieza en {@link #ACTIVO}. */
    private String estado = ACTIVO;

    /** Constructor vacío (convención POJO). El estado queda en {@link #ACTIVO}. */
    public InscripcionCurso() {
    }

    /**
     * Crea una inscripción con todos sus datos.
     *
     * @param idInscripcion identificador; 0 si es una inscripción nueva
     * @param idEstudiante  ID del estudiante
     * @param idGrupo       ID del grupo
     * @param notaFinal     nota de 0 a 5, o {@code null} si aún no tiene
     * @param estado        una de las constantes ACTIVO, APROBADO, REPROBADO o CANCELADO
     */
    public InscripcionCurso(int idInscripcion, int idEstudiante, int idGrupo, Float notaFinal, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

    // Getters y setters (encapsulamiento de los atributos privados).

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public int getIdGrupo() { return idGrupo; }
    public void setIdGrupo(int idGrupo) { this.idGrupo = idGrupo; }

    public Float getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Float notaFinal) { this.notaFinal = notaFinal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
