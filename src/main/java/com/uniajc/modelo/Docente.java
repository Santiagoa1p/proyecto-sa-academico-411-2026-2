package com.uniajc.modelo;

/**
 * Entidad (POJO) que representa a un docente que imparte grupos.
 *
 * <p>Capa <b>Modelo</b>: solo guarda datos. Cada objeto corresponde a una fila de
 * la tabla {@code "practica-mvc".docentes}. La tabla del profesor no tiene columna
 * de email, por eso esta clase tampoco la tiene.</p>
 *
 * <p>Correspondencia atributo → columna: {@code idDocente} → {@code id_docente},
 * {@code nombre} → {@code nombre}, {@code especialidad} → {@code especialidad}.</p>
 */
public class Docente {

    /** Identificador único generado por la base de datos; 0 mientras no se haya guardado. */
    private int idDocente;
    /** Nombre completo del docente. */
    private String nombre;
    /** Área en la que se especializa, por ejemplo "Bases de datos". */
    private String especialidad;

    /** Constructor vacío (convención POJO). */
    public Docente() {
    }

    /**
     * Crea un docente con todos sus datos.
     *
     * @param idDocente    identificador; 0 si es un docente nuevo
     * @param nombre       nombre completo
     * @param especialidad área de especialidad
     */
    public Docente(int idDocente, String nombre, String especialidad) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Getters y setters (encapsulamiento de los atributos privados).

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
