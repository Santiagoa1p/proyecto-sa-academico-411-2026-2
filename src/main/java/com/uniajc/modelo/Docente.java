package com.uniajc.modelo;

public class Docente {
    private int idDocente;
    private String nombre;
    private String especialidad;
    private String email;

    public Docente() {
    }

    public Docente(int idDocente, String nombre, String especialidad, String email) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.email = email;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
