package com.uniajc.modelo;

/**
 * Inscripción de un estudiante en un grupo. Tabla: "practica-mvc".inscripciones
 * La nota final es opcional (null) mientras el curso está en progreso.
 */
public class InscripcionCurso {
    public static final String ACTIVO = "ACTIVO";
    public static final String APROBADO = "APROBADO";
    public static final String REPROBADO = "REPROBADO";
    public static final String CANCELADO = "CANCELADO";

    private int idInscripcion;
    private int idEstudiante;
    private int idGrupo;
    private Float notaFinal;
    private String estado = ACTIVO;

    public InscripcionCurso() {
    }

    public InscripcionCurso(int idInscripcion, int idEstudiante, int idGrupo, Float notaFinal, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

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
