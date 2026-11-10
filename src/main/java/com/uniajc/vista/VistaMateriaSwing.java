package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

/** Panel Swing para gestionar materias. */
public class VistaMateriaSwing extends VistaCrudSwing<Materia> {

    private final ControladorMateria controlador;
    private JTextField campoNombre;
    private JSpinner campoCreditos;

    public VistaMateriaSwing(ControladorMateria controlador) {
        super("Materias", "materia", new String[]{"ID", "Nombre", "Créditos"});
        this.controlador = controlador;
    }

    @Override protected List<Materia> listar() { return controlador.listar(); }
    @Override protected Optional<Materia> buscar(int id) { return controlador.buscar(id); }
    @Override protected Materia nuevo() { return new Materia(0, "", 3); }
    @Override protected void registrar(Materia m) { controlador.registrar(m); }
    @Override protected void actualizar(Materia m) { controlador.actualizar(m); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    @Override
    protected Object[] fila(Materia m) {
        return new Object[]{m.getIdMateria(), m.getNombreMateria(), m.getCreditos()};
    }

    @Override
    protected void construirFormulario(Formulario f, Materia actual) {
        campoNombre = f.texto("Nombre", actual == null ? "" : actual.getNombreMateria());
        int creditos = actual == null ? 3 : actual.getCreditos();
        campoCreditos = f.numero("Créditos", creditos, MateriaService.CREDITOS_MINIMOS, MateriaService.CREDITOS_MAXIMOS);
    }

    @Override
    protected void leerFormulario(Materia destino) {
        destino.setNombreMateria(campoNombre.getText());
        destino.setCreditos((Integer) campoCreditos.getValue());
    }
}
