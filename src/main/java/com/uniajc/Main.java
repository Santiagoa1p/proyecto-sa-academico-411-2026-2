package com.uniajc;

import java.io.Console;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

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
import com.uniajc.vista.VistaPrincipalSwing;

/**
 * Punto de entrada. Arma las capas (DAO -> Servicio -> Controlador) y abre
 * la vista de consola o la ventana Swing.
 */
public class Main {

    /**
     * Uso: sin argumentos pregunta qué interfaz abrir; con "swing" abre la
     * ventana gráfica y con "consola" el menú de texto.
     */
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionDatabase::closeConnection));

        Controladores c = crearControladores();
        Scanner scanner = new Scanner(System.in, charsetConsola());
        LectorConsola lector = new LectorConsola(scanner, System.out);

        String modo = args.length > 0 ? args[0].replace("-", "").toLowerCase() : elegirModo(lector);
        if (modo.equals("swing") || modo.equals("2")) {
            iniciarSwing(c);
            return;
        }
        if (!probarConexion(lector)) {
            return;
        }
        new VistaConsola(lector, c.estudiantes, c.docentes, c.materias, c.grupos, c.inscripciones).iniciar();
    }

    private static String elegirModo(LectorConsola lector) {
        lector.mostrar("¿Qué interfaz deseas usar?");
        lector.mostrar("1. Consola");
        lector.mostrar("2. Ventana (Swing)");
        try {
            return lector.leerTexto("Opción");
        } catch (LectorConsola.FinDeEntrada fin) {
            return "swing";
        }
    }

    private static void iniciarSwing(Controladores c) {
        SwingUtilities.invokeLater(() -> {
            try {
                ConexionDatabase.getConnection();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage() + "\n\nRevisa config.properties (URL, USERNAME, PASSWORD)"
                                + " y que hayas ejecutado schema.sql en la base de datos.",
                        "No se pudo conectar", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new VistaPrincipalSwing(c.estudiantes, c.docentes, c.materias, c.grupos, c.inscripciones)
                    .setVisible(true);
        });
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
