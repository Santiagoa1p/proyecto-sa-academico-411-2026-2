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
 * Punto de entrada de la aplicación: lo primero que se ejecuta.
 *
 * <p>Su trabajo es <b>armar las capas</b> y conectarlas entre sí
 * (DAO → Servicio → Controlador), para luego abrir la vista de consola o la ventana
 * Swing. Es el único lugar donde se crean los objetos con {@code new}; las demás
 * clases los reciben por el constructor (inyección de dependencias).</p>
 */
public class Main {

    /**
     * Método principal. Java lo busca para arrancar el programa.
     *
     * <p>Uso: sin argumentos pregunta qué interfaz abrir; con {@code swing} abre la
     * ventana gráfica y con {@code consola} el menú de texto. Los argumentos llegan en
     * {@code args}, por ejemplo desde {@code launch.json} de VS Code o desde
     * {@code mvn exec:java "-Dexec.args=swing"}.</p>
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        // Shutdown hook: código que Java ejecuta justo antes de cerrar el programa
        // (por cualquier motivo). Se usa para cerrar la conexión a Neon ordenadamente.
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionDatabase::closeConnection));

        Controladores c = crearControladores();
        Scanner scanner = new Scanner(System.in, charsetConsola());
        LectorConsola lector = new LectorConsola(scanner, System.out);

        // Operador ternario (condición ? siVerdadero : siFalso): si vino un argumento se
        // usa ese; si no, se le pregunta al usuario. replace("-", "") acepta también "--swing".
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

    /**
     * Pregunta en la consola qué interfaz abrir.
     *
     * @return lo que escribió el usuario; "swing" si no hay consola para leer
     */
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

    /**
     * Prueba la conexión y abre la ventana principal.
     *
     * <p>{@link SwingUtilities#invokeLater} ejecuta el código en el <i>hilo de eventos de
     * Swing</i> (EDT). Swing exige que todo lo que toca la interfaz se haga en ese hilo;
     * si no, pueden aparecer errores raros de dibujo.</p>
     *
     * <p>Si no se puede conectar, muestra el error en una ventana y cierra el programa.</p>
     */
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

    /**
     * Arma todas las capas de abajo hacia arriba: primero los DAO, luego los servicios
     * (que reciben los DAO) y por último los controladores (que reciben los servicios).
     *
     * <p>Se crea un solo DAO de cada tipo y se comparte entre los servicios que lo
     * necesitan. Por ejemplo, el mismo {@code inscripcionDao} lo usan los servicios de
     * estudiantes, grupos e inscripciones.</p>
     *
     * @return los cinco controladores listos para entregar a las vistas
     */
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

    /**
     * Intenta conectarse antes de mostrar el menú de consola, para avisar de inmediato
     * si config.properties está mal y no a mitad de una operación.
     *
     * @return {@code true} si se pudo conectar
     */
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

    /**
     * Agrupa los cinco controladores para pasarlos juntos (un {@code record}: clase
     * compacta de solo datos).
     */
    record Controladores(
            ControladorEstudiante estudiantes,
            ControladorDocente docentes,
            ControladorMateria materias,
            ControladorGrupo grupos,
            ControladorInscripcionCurso inscripciones) {
    }
}
