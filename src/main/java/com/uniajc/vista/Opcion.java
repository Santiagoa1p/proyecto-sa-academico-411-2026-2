package com.uniajc.vista;

import java.util.List;

/** Elemento de un JComboBox: guarda el ID y muestra un texto legible. */
public record Opcion(int id, String texto) {

    @Override
    public String toString() {
        return texto;
    }

    /** Devuelve la opción con ese ID, o null si no existe. */
    public static Opcion conId(List<Opcion> opciones, int id) {
        return opciones.stream().filter(o -> o.id() == id).findFirst().orElse(null);
    }
}
