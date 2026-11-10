package com.uniajc.vista;

import java.util.List;
import java.util.Objects;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/** Vista de consola (Scanner) para estudiantes. */
public class VistaEstudiante extends VistaMenuConsola {

    private static final String FORMATO = "%-5s %-20s %-20s %-35s";

    private final ControladorEstudiante controlador;

    public VistaEstudiante(LectorConsola lector, ControladorEstudiante controlador) {
        super(lector, "Estudiantes");
        this.controlador = controlador;
    }

    @Override
    protected void listar() {
        List<Estudiante> estudiantes = controlador.listar();
        if (estudiantes.isEmpty()) {
            lector.mostrar("No hay estudiantes registrados.");
            return;
        }
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "APELLIDO", "EMAIL"));
        estudiantes.forEach(this::imprimir);
    }

    @Override
    protected void registrar() {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(lector.leerTexto("Nombre"));
        estudiante.setApellido(lector.leerTexto("Apellido"));
        estudiante.setEmail(lector.leerTexto("Email"));
        controlador.registrar(estudiante);
        lector.exito("Estudiante registrado con ID " + estudiante.getIdEstudiante() + ".");
    }

    @Override
    protected void buscar() {
        int id = lector.leerEntero("ID del estudiante");
        Estudiante estudiante = controlador.buscar(id).orElseThrow(() -> noEncontrado("un estudiante", id));
        lector.mostrar(String.format(FORMATO, "ID", "NOMBRE", "APELLIDO", "EMAIL"));
        imprimir(estudiante);
    }

    @Override
    protected void actualizar() {
        int id = lector.leerEntero("ID del estudiante a actualizar");
        Estudiante estudiante = controlador.buscar(id).orElseThrow(() -> noEncontrado("un estudiante", id));
        lector.mostrar("Pulsa Enter para conservar el valor actual.");
        estudiante.setNombre(lector.leerTexto("Nombre", estudiante.getNombre()));
        estudiante.setApellido(lector.leerTexto("Apellido", estudiante.getApellido()));
        estudiante.setEmail(lector.leerTexto("Email", estudiante.getEmail()));
        controlador.actualizar(estudiante);
        lector.exito("Estudiante actualizado.");
    }

    @Override
    protected void eliminar() {
        eliminarConConfirmacion("estudiante", controlador::eliminar);
    }

    private void imprimir(Estudiante e) {
        lector.mostrar(String.format(FORMATO, e.getIdEstudiante(), e.getNombre(),
                Objects.toString(e.getApellido(), ""), Objects.toString(e.getEmail(), "")));
    }
}
