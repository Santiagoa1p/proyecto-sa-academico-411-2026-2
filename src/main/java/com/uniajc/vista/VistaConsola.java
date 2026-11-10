package com.uniajc.vista;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcionCurso;
import com.uniajc.controlador.ControladorMateria;

/** Menú principal de consola: deriva a la vista de cada entidad. */
public class VistaConsola {

    private final LectorConsola lector;
    private final VistaEstudiante vistaEstudiante;
    private final VistaDocente vistaDocente;
    private final VistaMateria vistaMateria;
    private final VistaGrupo vistaGrupo;
    private final VistaInscripcionCurso vistaInscripcion;

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
