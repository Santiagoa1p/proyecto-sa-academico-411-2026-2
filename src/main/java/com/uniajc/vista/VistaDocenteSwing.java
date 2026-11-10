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
    private JTextField campoEmail;

    public VistaDocenteSwing(ControladorDocente controlador) {
        super("Docentes", "docente", new String[]{"ID", "Nombre", "Especialidad", "Email"});
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
        return new Object[]{d.getIdDocente(), d.getNombre(), d.getEspecialidad(), d.getEmail()};
    }

    @Override
    protected void construirFormulario(Formulario f, Docente actual) {
        campoNombre = f.texto("Nombre completo", actual == null ? "" : actual.getNombre());
        campoEspecialidad = f.texto("Especialidad", actual == null ? "" : actual.getEspecialidad());
        campoEmail = f.texto("Email", actual == null ? "" : actual.getEmail());
    }

    @Override
    protected void leerFormulario(Docente destino) {
        destino.setNombre(campoNombre.getText());
        destino.setEspecialidad(campoEspecialidad.getText());
        destino.setEmail(campoEmail.getText());
    }
}
