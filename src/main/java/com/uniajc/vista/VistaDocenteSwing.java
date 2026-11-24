package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JTextField;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

/**
 * Panel Swing para gestionar docentes.
 *
 * <p>Hereda de {@link VistaCrudSwing} toda la pantalla (buscador, tabla, botones y
 * diálogo) y solo define lo propio de esta entidad: las columnas, los campos del
 * formulario y a qué método del controlador llamar.</p>
 */
public class VistaDocenteSwing extends VistaCrudSwing<Docente> {

    private final ControladorDocente controlador;
    private JTextField campoNombre;
    private JTextField campoEspecialidad;

    /**
     * Crea el panel. {@code super(...)} le pasa a {@link VistaCrudSwing} el título,
     * el nombre de la entidad y los encabezados de las columnas.
     */
    public VistaDocenteSwing(ControladorDocente controlador) {
        super("Docentes", "docente", new String[]{"ID", "Nombre", "Especialidad"});
        this.controlador = controlador;
    }

    // Operaciones básicas: cada una solo delega en el controlador (una línea cada una).
    @Override protected List<Docente> listar() { return controlador.listar(); }
    @Override protected Optional<Docente> buscar(int id) { return controlador.buscar(id); }
    @Override protected Docente nuevo() { return new Docente(); }
    @Override protected void registrar(Docente d) { controlador.registrar(d); }
    @Override protected void actualizar(Docente d) { controlador.actualizar(d); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    /** Convierte un registro en una fila de la tabla (mismo orden que las columnas). */
    @Override
    protected Object[] fila(Docente d) {
        return new Object[]{d.getIdDocente(), d.getNombre(), d.getEspecialidad()};
    }

    /**
     * Agrega los campos al formulario. Si {@code actual} es {@code null} (registrar),
     * quedan vacíos; si no (editar), se llenan con los valores actuales.
     */
    @Override
    protected void construirFormulario(Formulario f, Docente actual) {
        campoNombre = f.texto("Nombre completo", actual == null ? "" : actual.getNombre());
        campoEspecialidad = f.texto("Especialidad", actual == null ? "" : actual.getEspecialidad());
    }

    /**
     * Copia lo escrito en el formulario hacia el objeto. Aquí no se valida nada: las
     * reglas las aplica el servicio cuando el objeto llega a él.
     */
    @Override
    protected void leerFormulario(Docente destino) {
        destino.setNombre(campoNombre.getText());
        destino.setEspecialidad(campoEspecialidad.getText());
    }
}
