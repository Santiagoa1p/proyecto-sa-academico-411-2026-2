package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JTextField;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

/** Panel Swing para gestionar docentes. */
public class VistaDocenteSwing extends VistaCrudSwing<Docente> {

    private final ControladorDocente controlador;
    private JTextField campoNombre;
    private JTextField campoEspecialidad;

    public VistaDocenteSwing(ControladorDocente controlador) {
        super("Docentes", "docente", new String[]{"ID", "Nombre", "Especialidad"});
        this.controlador = controlador;
    }

    @Override protected List<Docente> listar() { return controlador.listar(); }
    @Override protected Optional<Docente> buscar(int id) { return controlador.buscar(id); }
    @Override protected Docente nuevo() { return new Docente(); }
    @Override protected void registrar(Docente d) { controlador.registrar(d); }
    @Override protected void actualizar(Docente d) { controlador.actualizar(d); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    @Override
    protected Object[] fila(Docente d) {
        return new Object[]{d.getIdDocente(), d.getNombre(), d.getEspecialidad()};
    }

    @Override
    protected void construirFormulario(Formulario f, Docente actual) {
        campoNombre = f.texto("Nombre completo", actual == null ? "" : actual.getNombre());
        campoEspecialidad = f.texto("Especialidad", actual == null ? "" : actual.getEspecialidad());
    }

    @Override
    protected void leerFormulario(Docente destino) {
        destino.setNombre(campoNombre.getText());
        destino.setEspecialidad(campoEspecialidad.getText());
    }
}
