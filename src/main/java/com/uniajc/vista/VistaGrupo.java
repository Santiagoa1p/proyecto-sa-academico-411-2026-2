package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Docente;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Materia;

/**
 * Vista de consola (Scanner) para grupos (capa <b>Vista</b>).
 *
 * <p>Hereda de {@link VistaMenuConsola} el menú y el manejo de errores, e implementa
 * las cinco operaciones CRUD. Solo habla con el usuario (a través de
 * {@link LectorConsola}) y con su controlador; nunca con el servicio ni con el DAO.</p>
 *
 * <p>También recibe los controladores de materias y docentes, para mostrar sus
 * nombres en la tabla y listarlos como opciones al registrar o actualizar.</p>
 */
public class VistaGrupo extends VistaMenuConsola {

    /**
     * Formato de cada fila para {@code String.format}: {@code %-20s} significa "texto
     * alineado a la izquierda en 20 espacios", así las columnas quedan derechas.
     */
    private static final String FORMATO = "%-5s %-30s %-25s %-10s %-25s";

    private final ControladorGrupo controlador;
    private final ControladorMateria materias;
    private final ControladorDocente docentes;

    /**
     * Crea la vista. {@code super(...)} llama al constructor de {@link VistaMenuConsola}
     * con el lector y el título del menú.
     */
    public VistaGrupo(LectorConsola lector, ControladorGrupo controlador,
                      ControladorMateria materias, ControladorDocente docentes) {
        super(lector, "Grupos");
        this.controlador = controlador;
        this.materias = materias;
        this.docentes = docentes;
    }

    /** Lista todos los grupos en forma de tabla, o avisa si no hay ninguno. */
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

    /**
     * Pide los datos de un grupo nuevo, lo envía al controlador y muestra el ID asignado.
     * Si algún dato no cumple las reglas, el servicio lanza un error que muestra
     * {@link VistaMenuConsola}.
     */
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

    /**
     * Pide un ID y muestra ese registro. {@code orElseThrow} lanza el error
     * "No existe..." si el {@code Optional} viene vacío.
     */
    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del grupo");
        Grupo grupo = controlador.buscar(id).orElseThrow(() -> noEncontrado("un grupo", id));
        lector.mostrar(String.format(FORMATO, "ID", "MATERIA", "DOCENTE", "AULA", "HORARIO"));
        imprimir(grupo);
    }

    /**
     * Pide un ID, trae el registro actual y deja cambiar cada campo
     * (Enter conserva el valor que ya tenía).
     */
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

    /** Pide un ID y elimina ese registro después de pedir confirmación. */
    @Override
    protected void eliminar() {
        eliminarConConfirmacion("grupo", controlador::eliminar);
    }

    /**
     * Muestra los ID disponibles de las entidades relacionadas, para que el usuario
     * sepa qué número escribir.
     */
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

    /** Escribe un registro como una fila de la tabla, con el mismo formato que los títulos. */
    private void imprimir(Grupo g) {
        String materia = materias.buscar(g.getIdMateria()).map(Materia::getNombreMateria).orElse("#" + g.getIdMateria());
        String docente = docentes.buscar(g.getIdDocente()).map(Docente::getNombre).orElse("#" + g.getIdDocente());
        lector.mostrar(String.format(FORMATO, g.getIdGrupo(), cortar(materia, 30), cortar(docente, 25),
                g.getAula(), g.getHorario()));
    }
}
