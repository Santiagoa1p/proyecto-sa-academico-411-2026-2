package com.uniajc.vista;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;

/**
 * Menú principal de la interfaz de consola (capa <b>Vista</b>).
 *
 * <p>Muestra las cinco entidades y, según la opción elegida, abre el submenú de la
 * vista correspondiente ({@link VistaEstudiante}, {@link VistaDocente}, etc.).</p>
 */
public class VistaConsola {

    /** Lector compartido por todas las vistas de consola. */
    private final LectorConsola lector;
    // Una vista (submenú) por entidad.
    private final VistaEstudiante vistaEstudiante;
    private final VistaDocente vistaDocente;
    private final VistaMateria vistaMateria;
    private final VistaGrupo vistaGrupo;
    private final VistaInscripcionCurso vistaInscripcion;

    /**
     * Crea el menú principal y una vista por entidad. Recibe los cinco controladores
     * porque algunas vistas necesitan varios (por ejemplo, la de grupos usa también
     * los de materias y docentes para mostrar sus nombres).
     *
     * @param lector        lector de consola
     * @param estudiantes   controlador de estudiantes
     * @param docentes      controlador de docentes
     * @param materias      controlador de materias
     * @param grupos        controlador de grupos
     * @param inscripciones controlador de inscripciones
     */
    public VistaConsola(LectorConsola lector,
                        ControladorEstudiante estudiantes,
                        ControladorDocente docentes,
                        ControladorMateria materias,
                        ControladorGrupo grupos,
                        ControladorInscripcionCurso inscripciones) {
        this.lector = lector;
        this.vistaEstudiante = new VistaEstudiante(lector, estudiantes);
        this.vistaDocente = new VistaDocente(lector, docentes);
        this.vistaMateria = new VistaMateria(lector, materias);
        this.vistaGrupo = new VistaGrupo(lector, grupos, materias, docentes);
        this.vistaInscripcion = new VistaInscripcionCurso(lector, inscripciones, estudiantes, grupos);
    }

    /**
     * Muestra el menú principal en un ciclo hasta que el usuario elige "0. Salir".
     *
     * <p>Si la entrada se termina de golpe (Ctrl+Z / Ctrl+D), {@link LectorConsola}
     * lanza {@link LectorConsola.FinDeEntrada}; aquí se atrapa para salir con un mensaje
     * en lugar de mostrar un error.</p>
     */
    public void iniciar() {
        try {
            while (true) {
                lector.mostrar("");
                lector.mostrar("==========================================");
                lector.mostrar("   SISTEMA DE GESTIÓN ACADÉMICA UNIAJC");
                lector.mostrar("==========================================");
                lector.mostrar("1. Estudiantes");
                lector.mostrar("2. Docentes");
                lector.mostrar("3. Materias");
                lector.mostrar("4. Grupos");
                lector.mostrar("5. Inscripciones");
                lector.mostrar("0. Salir");
                switch (lector.leerTexto("Opción")) {
                    case "1" -> vistaEstudiante.mostrarMenu();
                    case "2" -> vistaDocente.mostrarMenu();
                    case "3" -> vistaMateria.mostrarMenu();
                    case "4" -> vistaGrupo.mostrarMenu();
                    case "5" -> vistaInscripcion.mostrarMenu();
                    case "0" -> {
                        lector.mostrar("¡Hasta pronto!");
                        return;
                    }
                    default -> lector.error("Opción no válida.");
                }
            }
        } catch (LectorConsola.FinDeEntrada fin) {
            lector.mostrar("");
            lector.mostrar("Entrada finalizada. Saliendo...");
        }
    }
}
