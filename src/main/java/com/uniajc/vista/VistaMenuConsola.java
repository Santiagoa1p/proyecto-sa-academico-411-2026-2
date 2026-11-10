package com.uniajc.vista;

import com.uniajc.dao.ErrorBaseDatosException;

/**
 * Menú CRUD común para las vistas de consola. Cada vista concreta solo
 * implementa las cinco operaciones; aquí se maneja el menú y los errores.
 */
public abstract class VistaMenuConsola {

    protected final LectorConsola lector;
    private final String titulo;

    protected VistaMenuConsola(LectorConsola lector, String titulo) {
        this.lector = lector;
        this.titulo = titulo;
    }

    public void mostrarMenu() {
        while (true) {
            lector.mostrar("");
            lector.mostrar("===== " + titulo.toUpperCase() + " =====");
            lector.mostrar("1. Listar");
            lector.mostrar("2. Registrar");
            lector.mostrar("3. Buscar por ID");
            lector.mostrar("4. Actualizar");
            lector.mostrar("5. Eliminar");
            lector.mostrar("0. Volver");
            String opcion = lector.leerTexto("Opción");
            if (opcion.equals("0")) {
                return;
            }
            ejecutar(opcion);
        }
    }

    private void ejecutar(String opcion) {
        try {
            switch (opcion) {
                case "1" -> listar();
                case "2" -> registrar();
                case "3" -> buscar();
                case "4" -> actualizar();
                case "5" -> eliminar();
                default -> lector.error("Opción no válida.");
            }
        } catch (IllegalArgumentException e) {
            lector.error(e.getMessage());
        } catch (ErrorBaseDatosException e) {
            lector.error("Error de base de datos. " + e.getMessage());
        }
    }

    protected abstract void listar();

    protected abstract void registrar();

    protected abstract void buscar();

    protected abstract void actualizar();

    protected abstract void eliminar();

    /** Pregunta y elimina el registro si el usuario confirma. */
    protected void eliminarConConfirmacion(String entidad, java.util.function.IntConsumer eliminar) {
        int id = lector.leerEntero("ID del " + entidad + " a eliminar");
        if (lector.confirmar("¿Seguro que deseas eliminar el " + entidad + " " + id + "?")) {
            eliminar.accept(id);
            lector.exito("Registro eliminado.");
        } else {
            lector.mostrar("Operación cancelada.");
        }
    }

    protected IllegalArgumentException noEncontrado(String entidad, int id) {
        return new IllegalArgumentException("No existe " + entidad + " con ID " + id + ".");
    }
}
