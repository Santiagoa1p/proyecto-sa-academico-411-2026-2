package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

/** Vista de consola (Scanner) para docentes. */
public class VistaDocente extends VistaMenuConsola {

    private static final String FORMATO = "%-5s %-30s %-30s";

    private final ControladorDocente controlador;

    public VistaDocente(LectorConsola lector, ControladorDocente controlador) {
        super(lector, "Docentes");
        this.controlador = controlador;
    }

    @Override
    protected void listar() {
        List<Docente> docentes = controlador.listar();
        if (docentes.isEmpty()) {
            lector.mostrar("No hay docentes registrados.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "ESPECIALIDAD"));
        docentes.forEach(this::imprimir);
    }

    @Override
    protected void registrar() {
        Docente docente = new Docente();
        docente.setNombre(lector.leerTexto("Nombre completo"));
        docente.setEspecialidad(lector.leerTexto("Especialidad"));
        controlador.registrar(docente);
        lector.exito("Docente registrado con ID " + docente.getIdDocente() + ".");
    }

    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del docente");
        Docente docente = controlador.buscar(id).orElseThrow(() -> noEncontrado("un docente", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "ESPECIALIDAD"));
        imprimir(docente);
    }

    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID del docente a actualizar");
        Docente docente = controlador.buscar(id).orElseThrow(() -> noEncontrado("un docente", id));
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        docente.setNombre(lector.leerTexto("Nombre completo", docente.getNombre()));
        docente.setEspecialidad(lector.leerTexto("Especialidad", docente.getEspecialidad()));
        controlador.actualizar(docente);
        lector.exito("Docente actualizado.");
    }

    @Override
    protected void eliminar() {
        eliminarConConfirmacion("docente", controlador::eliminar);
    }

    private void imprimir(Docente d) {
        lector.mostrar(String.format(FORMATO, d.getIdDocente(), d.getNombre(), d.getEspecialidad()));
    }
}
