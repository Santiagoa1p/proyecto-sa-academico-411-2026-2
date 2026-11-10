package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JTextField;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/** Panel Swing para gestionar estudiantes. */
public class VistaEstudianteSwing extends VistaCrudSwing<Estudiante> {

    private final ControladorEstudiante controlador;
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoEmail;

    public VistaEstudianteSwing(ControladorEstudiante controlador) {
        super("Estudiantes", "estudiante", new String[]{"ID", "Nombre", "Apellido", "Email"});
        this.controlador = controlador;
    }

    @Override protected List<Estudiante> listar() { return controlador.listar(); }
    @Override protected Optional<Estudiante> buscar(int id) { return controlador.buscar(id); }
    @Override protected Estudiante nuevo() { return new Estudiante(); }
    @Override protected void registrar(Estudiante e) { controlador.registrar(e); }
    @Override protected void actualizar(Estudiante e) { controlador.actualizar(e); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    @Override
    protected Object[] fila(Estudiante e) {
        return new Object[]{e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()};
    }

    @Override
    protected void construirFormulario(Formulario f, Estudiante actual) {
        campoNombre = f.texto("Nombre", actual == null ? "" : actual.getNombre());
        campoApellido = f.texto("Apellido", actual == null ? "" : actual.getApellido());
        campoEmail = f.texto("Email", actual == null ? "" : actual.getEmail());
    }

    @Override
    protected void leerFormulario(Estudiante destino) {
        destino.setNombre(campoNombre.getText());
        destino.setApellido(campoApellido.getText());
        destino.setEmail(campoEmail.getText());
    }
}
