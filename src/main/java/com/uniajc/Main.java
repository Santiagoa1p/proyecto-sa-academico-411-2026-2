package com.uniajc;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionCursoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.InscripcionCursoService;
import com.uniajc.servicios.MateriaService;
import com.uniajc.vista.VistaConsola;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EstudianteDao estudianteDao = new EstudianteDao();
        DocenteDao docenteDao = new DocenteDao();
        MateriaDao materiaDao = new MateriaDao();
        GrupoDao grupoDao = new GrupoDao();
        InscripcionCursoDao inscripcionDao = new InscripcionCursoDao();

        ControladorEstudiante estudiantes = new ControladorEstudiante(new EstudianteService(estudianteDao));
        ControladorDocente docentes = new ControladorDocente(new DocenteService(docenteDao));
        ControladorMateria materias = new ControladorMateria(new MateriaService(materiaDao));
        ControladorGrupo grupos = new ControladorGrupo(new GrupoService(grupoDao, materiaDao, docenteDao));
        ControladorInscripcionCurso inscripciones = new ControladorInscripcionCurso(
                new InscripcionCursoService(inscripcionDao, estudianteDao, grupoDao));

        VistaConsola vista = new VistaConsola(
                new Scanner(System.in), estudiantes, docentes, materias, grupos, inscripciones);
        vista.iniciar();
    }
}