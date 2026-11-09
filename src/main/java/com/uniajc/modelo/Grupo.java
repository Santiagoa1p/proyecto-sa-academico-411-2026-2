package com.uniajc.modelo;

public class Grupo {
    private int idGrupo;
    private int idMateria;
    private int idDocente;
    private String aula;
    private String horario;

    public Grupo() {
    }

    public Grupo(int idGrupo, int idMateria, int idDocente, String aula, String horario) {
        this.idGrupo = idGrupo;
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.aula = aula;
        this.horario = horario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}
