package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JTextField;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/**
 * Panel Swing para gestionar estudiantes.
 *
 * <p>Hereda de {@link VistaCrudSwing} toda la pantalla (buscador, tabla, botones y
 * diálogo) y solo define lo propio de esta entidad: las columnas, los campos del
 * formulario y a qué método del controlador llamar.</p>
 */
public class VistaEstudianteSwing extends VistaCrudSwing<Estudiante> {

    private final ControladorEstudiante controlador;
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoEmail;

    /**
     * Crea el panel. {@code super(...)} le pasa a {@link VistaCrudSwing} el título,
     * el nombre de la entidad y los encabezados de las columnas.
     */
    public VistaEstudianteSwing(ControladorEstudiante controlador) {
        super("Estudiantes", "estudiante", new String[]{"ID", "Nombre", "Apellido", "Email"});
        this.controlador = controlador;
    }

    // Operaciones básicas: cada una solo delega en el controlador (una línea cada una).
    @Override protected List<Estudiante> listar() { return controlador.listar(); }
    @Override protected Optional<Estudiante> buscar(int id) { return controlador.buscar(id); }
    @Override protected Estudiante nuevo() { return new Estudiante(); }
    @Override protected void registrar(Estudiante e) { controlador.registrar(e); }
    @Override protected void actualizar(Estudiante e) { controlador.actualizar(e); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    /** Convierte un registro en una fila de la tabla (mismo orden que las columnas). */
    @Override
    protected Object[] fila(Estudiante e) {
        return new Object[]{e.getIdEstudiante(), e.getNombre(), e.getApellido(), e.getEmail()};
    }

    /**
     * Agrega los campos al formulario. Si {@code actual} es {@code null} (registrar),
     * quedan vacíos; si no (editar), se llenan con los valores actuales.
     */
    @Override
    protected void construirFormulario(Formulario f, Estudiante actual) {
        campoNombre = f.texto("Nombre", actual == null ? "" : actual.getNombre());
        campoApellido = f.texto("Apellido", actual == null ? "" : actual.getApellido());
        campoEmail = f.texto("Email", actual == null ? "" : actual.getEmail());
    }

    /**
     * Copia lo escrito en el formulario hacia el objeto. Aquí no se valida nada: las
     * reglas las aplica el servicio cuando el objeto llega a él.
     */
    @Override
    protected void leerFormulario(Estudiante destino) {
        destino.setNombre(campoNombre.getText());
        destino.setApellido(campoApellido.getText());
        destino.setEmail(campoEmail.getText());
    }
}
