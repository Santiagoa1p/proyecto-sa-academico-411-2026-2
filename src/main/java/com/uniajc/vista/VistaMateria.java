package com.uniajc.vista;

import java.util.List;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;

/** Vista de consola (Scanner) para materias. */
public class VistaMateria extends VistaMenuConsola {

    private static final String FORMATO = "%-5s %-40s %-8s";

    private final ControladorMateria controlador;

    public VistaMateria(LectorConsola lector, ControladorMateria controlador) {
        super(lector, "Materias");
        this.controlador = controlador;
    }

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

    @Override
    protected void registrar() {
        Materia materia = new Materia();
        materia.setNombreMateria(lector.leerTexto("Nombre de la materia"));
        materia.setCreditos(lector.leerEntero("Créditos (1-10)"));
        controlador.registrar(materia);
        lector.exito("Materia registrada con ID " + materia.getIdMateria() + ".");
    }

    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID de la materia");
        Materia materia = controlador.buscar(id).orElseThrow(() -> noEncontrado("una materia", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "CRÉDITOS"));
        imprimir(materia);
    }

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

    private void imprimir(Materia m) {
        lector.mostrar(String.format(FORMATO, m.getIdMateria(), m.getNombreMateria(), m.getCreditos()));
    }
}
