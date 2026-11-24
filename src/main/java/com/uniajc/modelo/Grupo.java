package com.uniajc.modelo;

/**
 * Entidad (POJO) que representa un grupo: una materia dictada por un docente en
 * un aula y un horario determinados.
 *
 * <p>Capa <b>Modelo</b>: solo guarda datos. Cada objeto corresponde a una fila de
 * la tabla {@code "practica-mvc".grupos}.</p>
 *
 * <p>Relaciones (según el diagrama del README): una {@link Materia} se dicta en
 * muchos grupos y un {@link Docente} imparte muchos grupos. Por eso el grupo guarda
 * los ID de ambos ({@code idMateria} e {@code idDocente}), que funcionan como
 * claves foráneas.</p>
 */
public class Grupo {

    /** Identificador único generado por la base de datos; 0 mientras no se haya guardado. */
    private int idGrupo;
    /** ID de la materia que se dicta en este grupo. */
    private int idMateria;
    /** ID del docente que imparte este grupo. */
    private int idDocente;
    /** Salón donde se dicta, por ejemplo "A-201". */
    private String aula;
    /** Día y hora de la clase, por ejemplo "Lunes 7:00-9:00". */
    private String horario;

    /** Constructor vacío (convención POJO). */
    public Grupo() {
    }

    /**
     * Crea un grupo con todos sus datos.
     *
     * @param idGrupo   identificador; 0 si es un grupo nuevo
     * @param idMateria ID de la materia que se dicta
     * @param idDocente ID del docente que la imparte
     * @param aula      salón
     * @param horario   día y hora
     */
    public Grupo(int idGrupo, int idMateria, int idDocente, String aula, String horario) {
        this.idGrupo = idGrupo;
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.aula = aula;
        this.horario = horario;
    }

    // Getters y setters (encapsulamiento de los atributos privados).

    public int getIdGrupo() { return idGrupo; }
    public void setIdGrupo(int idGrupo) { this.idGrupo = idGrupo; }

    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}
