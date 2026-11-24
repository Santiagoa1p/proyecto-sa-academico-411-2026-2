package com.uniajc.vista;

import java.util.List;
import java.util.Optional;

import javax.swing.JSpinner;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

/**
 * Panel Swing para gestionar materias.
 *
 * <p>Hereda de {@link VistaCrudSwing} toda la pantalla (buscador, tabla, botones y
 * diálogo) y solo define lo propio de esta entidad: las columnas, los campos del
 * formulario y a qué método del controlador llamar.</p>
 */
public class VistaMateriaSwing extends VistaCrudSwing<Materia> {

    private final ControladorMateria controlador;
    private JTextField campoNombre;
    private JSpinner campoCreditos;

    /**
     * Crea el panel. {@code super(...)} le pasa a {@link VistaCrudSwing} el título,
     * el nombre de la entidad y los encabezados de las columnas.
     */
    public VistaMateriaSwing(ControladorMateria controlador) {
        super("Materias", "materia", new String[]{"ID", "Nombre", "Créditos"});
        this.controlador = controlador;
    }

    // Operaciones básicas: cada una solo delega en el controlador (una línea cada una).
    @Override protected List<Materia> listar() { return controlador.listar(); }
    @Override protected Optional<Materia> buscar(int id) { return controlador.buscar(id); }
    @Override protected Materia nuevo() { return new Materia(0, "", 3); }
    @Override protected void registrar(Materia m) { controlador.registrar(m); }
    @Override protected void actualizar(Materia m) { controlador.actualizar(m); }
    @Override protected void eliminar(int id) { controlador.eliminar(id); }

    /** Convierte un registro en una fila de la tabla (mismo orden que las columnas). */
    @Override
    protected Object[] fila(Materia m) {
        return new Object[]{m.getIdMateria(), m.getNombreMateria(), m.getCreditos()};
    }

    /**
     * Agrega los campos al formulario. Si {@code actual} es {@code null} (registrar),
     * quedan vacíos; si no (editar), se llenan con los valores actuales.
     */
    @Override
    protected void construirFormulario(Formulario f, Materia actual) {
        campoNombre = f.texto("Nombre", actual == null ? "" : actual.getNombreMateria());
        int creditos = actual == null ? 3 : actual.getCreditos();
        campoCreditos = f.numero("Créditos", creditos, MateriaService.CREDITOS_MINIMOS, MateriaService.CREDITOS_MAXIMOS);
    }

    /**
     * Copia lo escrito en el formulario hacia el objeto. Aquí no se valida nada: las
     * reglas las aplica el servicio cuando el objeto llega a él.
     */
    @Override
    protected void leerFormulario(Materia destino) {
        destino.setNombreMateria(campoNombre.getText());
        destino.setCreditos((Integer) campoCreditos.getValue());
    }
}
