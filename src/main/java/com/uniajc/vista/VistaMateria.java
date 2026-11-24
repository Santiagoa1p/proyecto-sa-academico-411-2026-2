package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;

/**
 * Vista de consola (Scanner) para materias (capa <b>Vista</b>).
 *
 * <p>Hereda de {@link VistaMenuConsola} el menú y el manejo de errores, e implementa
 * las cinco operaciones CRUD. Solo habla con el usuario (a través de
 * {@link LectorConsola}) y con su controlador; nunca con el servicio ni con el DAO.</p>
 */
public class VistaMateria extends VistaMenuConsola {

    /**
     * Formato de cada fila para {@code String.format}: {@code %-20s} significa "texto
     * alineado a la izquierda en 20 espacios", así las columnas quedan derechas.
     */
    private static final String FORMATO = "%-5s %-40s %-8s";

    private final ControladorMateria controlador;

    /**
     * Crea la vista. {@code super(...)} llama al constructor de {@link VistaMenuConsola}
     * con el lector y el título del menú.
     */
    public VistaMateria(LectorConsola lector, ControladorMateria controlador) {
        super(lector, "Materias");
        this.controlador = controlador;
    }

    /** Lista todas las materias en forma de tabla, o avisa si no hay ninguna. */
    @Override
    protected void listar() {
        List<Materia> materias = controlador.listar();
        if (materias.isEmpty()) {
            lector.mostrar("No hay materias registradas.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "CRÉDITOS"));
        materias.forEach(this::imprimir);
    }

    /**
     * Pide los datos de una materia nueva, lo envía al controlador y muestra el ID asignado.
     * Si algún dato no cumple las reglas, el servicio lanza un error que muestra
     * {@link VistaMenuConsola}.
     */
    @Override
    protected void registrar() {
        Materia materia = new Materia();
        materia.setNombreMateria(lector.leerTexto("Nombre de la materia"));
        materia.setCreditos(lector.leerEntero("Créditos (1-10)"));
        controlador.registrar(materia);
        lector.exito("Materia registrada con ID " + materia.getIdMateria() + ".");
    }

    /**
     * Pide un ID y muestra ese registro. {@code orElseThrow} lanza el error
     * "No existe..." si el {@code Optional} viene vacío.
     */
    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID de la materia");
        Materia materia = controlador.buscar(id).orElseThrow(() -> noEncontrado("una materia", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "CRÉDITOS"));
        imprimir(materia);
    }

    /**
     * Pide un ID, trae el registro actual y deja cambiar cada campo
     * (Enter conserva el valor que ya tenía).
     */
    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID de la materia a actualizar");
        Materia materia = controlador.buscar(id).orElseThrow(() -> noEncontrado("una materia", id));
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        materia.setNombreMateria(lector.leerTexto("Nombre de la materia", materia.getNombreMateria()));
        materia.setCreditos(lector.leerEntero("Créditos (1-10)", materia.getCreditos()));
        controlador.actualizar(materia);
        lector.exito("Materia actualizada.");
    }

    /** Pide un ID y elimina ese registro después de pedir confirmación. */
    @Override
    protected void eliminar() {
        int id = lector.leerEntero("ID de la materia a eliminar");
        if (lector.confirmar("¿Seguro que deseas eliminar la materia " + id + "?")) {
            controlador.eliminar(id);
            lector.exito("Materia eliminada.");
        } else {
            lector.mostrar("Operación cancelada.");
        }
    }

    /** Escribe un registro como una fila de la tabla, con el mismo formato que los títulos. */
    private void imprimir(Materia m) {
        lector.mostrar(String.format(FORMATO, m.getIdMateria(), m.getNombreMateria(), m.getCreditos()));
    }
}
