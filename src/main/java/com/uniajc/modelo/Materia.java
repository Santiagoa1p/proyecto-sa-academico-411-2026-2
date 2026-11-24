package com.uniajc.modelo;

/**
 * Entidad (POJO) que representa una materia del plan de estudios.
 *
 * <p>Capa <b>Modelo</b>: solo guarda datos. Cada objeto corresponde a una fila de
 * la tabla {@code "practica-mvc".materias}.</p>
 *
 * <p>Correspondencia atributo → columna: {@code idMateria} → {@code id_materia},
 * {@code nombreMateria} → {@code nombre_materia}, {@code creditos} → {@code creditos}.</p>
 */
public class Materia {

    /** Identificador único generado por la base de datos; 0 mientras no se haya guardado. */
    private int idMateria;
    /** Nombre de la materia. El servicio garantiza que no se repita. */
    private String nombreMateria;
    /** Número de créditos académicos. El servicio exige un valor entre 1 y 10. */
    private int creditos;

    /** Constructor vacío (convención POJO). */
    public Materia() {
    }

    /**
     * Crea una materia con todos sus datos.
     *
     * @param idMateria     identificador; 0 si es una materia nueva
     * @param nombreMateria nombre de la materia
     * @param creditos      número de créditos
     */
    public Materia(int idMateria, String nombreMateria, int creditos) {
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
        this.creditos = creditos;
    }

    // Getters y setters (encapsulamiento de los atributos privados).

    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }

    public String getNombreMateria() { return nombreMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }
}
