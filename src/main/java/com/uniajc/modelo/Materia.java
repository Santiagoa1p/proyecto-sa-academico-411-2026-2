package com.uniajc.modelo;

/** Materia del plan de estudios. Tabla: "practica-mvc".materias */
public class Materia {
    private int idMateria;
    private String nombreMateria;
    private int creditos;

    public Materia() {
    }

    public Materia(int idMateria, String nombreMateria, int creditos) {
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
        this.creditos = creditos;
    }

    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }

    public String getNombreMateria() { return nombreMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }

    public int getCreditos() { return creditos; }
    public void setCreditos(int creditos) { this.creditos = creditos; }
}
