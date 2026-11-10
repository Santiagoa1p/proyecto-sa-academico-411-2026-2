package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Materia;

/** Vista de consola (Scanner) para grupos. Muestra materias y docentes para elegir por ID. */
public class VistaGrupo extends VistaMenuConsola {

    private static final String FORMATO = "%-5s %-30s %-25s %-10s %-25s";

    private final ControladorGrupo controlador;
    private final ControladorMateria materias;
    private final ControladorDocente docentes;

    public VistaGrupo(LectorConsola lector, ControladorGrupo controlador,
                      ControladorMateria materias, ControladorDocente docentes) {
        super(lector, "Grupos");
        this.controlador = controlador;
        this.materias = materias;
        this.docentes = docentes;
    }

    @Override
    protected void listar() {
        List<Grupo> grupos = controlador.listar();
        if (grupos.isEmpty()) {
            lector.mostrar("No hay grupos registrados.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "MATERIA", "DOCENTE", "AULA", "HORARIO"));
        grupos.forEach(this::imprimir);
    }

    @Override
    protected void registrar() {
        Grupo grupo = new Grupo();
        mostrarOpciones();
        grupo.setIdMateria(lector.leerEntero("ID de la materia"));
        grupo.setIdDocente(lector.leerEntero("ID del docente"));
        grupo.setAula(lector.leerTexto("Aula (ej. A-201)"));
        grupo.setHorario(lector.leerTexto("Horario (ej. Lunes 18:00-20:00)"));
        controlador.registrar(grupo);
        lector.exito("Grupo registrado con ID " + grupo.getIdGrupo() + ".");
    }

    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del grupo");
        Grupo grupo = controlador.buscar(id).orElseThrow(() -> noEncontrado("un grupo", id));
        lector.mostrar(String.format(FORMATO, "ID", "MATERIA", "DOCENTE", "AULA", "HORARIO"));
        imprimir(grupo);
    }

    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID del grupo a actualizar");
        Grupo grupo = controlador.buscar(id).orElseThrow(() -> noEncontrado("un grupo", id));
        mostrarOpciones();
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        grupo.setIdMateria(lector.leerEntero("ID de la materia", grupo.getIdMateria()));
        grupo.setIdDocente(lector.leerEntero("ID del docente", grupo.getIdDocente()));
        grupo.setAula(lector.leerTexto("Aula", grupo.getAula()));
        grupo.setHorario(lector.leerTexto("Horario", grupo.getHorario()));
        controlador.actualizar(grupo);
        lector.exito("Grupo actualizado.");
    }

    @Override
    protected void eliminar() {
        eliminarConConfirmacion("grupo", controlador::eliminar);
    }

    private void mostrarOpciones() {
        lector.mostrar("Materias disponibles:");
        for (Materia m : materias.listar()) {
            lector.mostrar("  " + m.getIdMateria() + " - " + m.getNombreMateria());
        }
        lector.mostrar("Docentes disponibles:");
        for (Docente d : docentes.listar()) {
            lector.mostrar("  " + d.getIdDocente() + " - " + d.getNombre() + " (" + d.getEspecialidad() + ")");
        }
    }

    private void imprimir(Grupo g) {
        String materia = materias.buscar(g.getIdMateria()).map(Materia::getNombreMateria).orElse("#" + g.getIdMateria());
        String docente = docentes.buscar(g.getIdDocente()).map(Docente::getNombre).orElse("#" + g.getIdDocente());
        lector.mostrar(String.format(FORMATO, g.getIdGrupo(), cortar(materia, 30), cortar(docente, 25),
                g.getAula(), g.getHorario()));
    }
}
