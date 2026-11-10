package com.uniajc;

import java.io.Console;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Scanner;

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
import com.uniajc.db.ConexionDatabase;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.InscripcionCursoService;
import com.uniajc.servicios.MateriaService;
import com.uniajc.vista.LectorConsola;
import com.uniajc.vista.VistaConsola;

/**
 * Punto de entrada. Arma las capas (DAO -> Servicio -> Controlador) y abre
 * la vista de consola.
 */
public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionDatabase::closeConnection));

        Controladores c = crearControladores();
        Scanner scanner = new Scanner(System.in, charsetConsola());
        LectorConsola lector = new LectorConsola(scanner, System.out);

        if (!probarConexion(lector)) {
            return;
        }
        new VistaConsola(lector, c.estudiantes, c.docentes, c.materias, c.grupos, c.inscripciones).iniciar();
    }

    /** Une todas las capas compartiendo las mismas instancias de DAO. */
    static Controladores crearControladores() {
        EstudianteDao estudianteDao = new EstudianteDao();
        DocenteDao docenteDao = new DocenteDao();
        MateriaDao materiaDao = new MateriaDao();
        GrupoDao grupoDao = new GrupoDao();
        InscripcionCursoDao inscripcionDao = new InscripcionCursoDao();

        return new Controladores(
                new ControladorEstudiante(new EstudianteService(estudianteDao, inscripcionDao)),
                new ControladorDocente(new DocenteService(docenteDao, grupoDao)),
                new ControladorMateria(new MateriaService(materiaDao, grupoDao)),
                new ControladorGrupo(new GrupoService(grupoDao, materiaDao, docenteDao, inscripcionDao)),
                new ControladorInscripcionCurso(new InscripcionCursoService(inscripcionDao, estudianteDao, grupoDao)));
    }

    private static boolean probarConexion(LectorConsola lector) {
        lector.mostrar("Conectando a la base de datos...");
        try {
            ConexionDatabase.getConnection();
            return true;
        } catch (SQLException e) {
            lector.error(e.getMessage());
            lector.mostrar("Revisa config.properties (URL, USERNAME, PASSWORD) y que hayas ejecutado schema.sql.");
            return false;
        }
    }

    /** Usa la codificación de la terminal para leer bien tildes y ñ en Windows. */
    private static Charset charsetConsola() {
        Console consola = System.console();
        return consola != null ? consola.charset() : Charset.defaultCharset();
    }

    record Controladores(
            ControladorEstudiante estudiantes,
            ControladorDocente docentes,
            ControladorMateria materias,
            ControladorGrupo grupos,
            ControladorInscripcionCurso inscripciones) {
    }
}
