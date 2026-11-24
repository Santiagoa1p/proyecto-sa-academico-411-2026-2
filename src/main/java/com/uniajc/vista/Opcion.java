package com.uniajc.vista;

import java.util.List;

/**
 * Elemento de una lista desplegable ({@code JComboBox}) de Swing.
 *
 * <p>Guarda dos cosas: el {@code id} que se envía al servicio (por ejemplo, el ID de
 * una materia) y el {@code texto} que ve el usuario (por ejemplo, "3 - Bases de
 * datos"). Así el usuario elige por nombre y el programa trabaja con el ID.</p>
 *
 * <p>Es un {@code record}: Java genera automáticamente el constructor, los métodos
 * {@code id()} y {@code texto()}, y {@code equals}. Lo de {@code equals} importa: dos
 * {@code Opcion} con el mismo id y texto se consideran iguales, y así el combo puede
 * dejar preseleccionado el valor actual al editar.</p>
 *
 * @param id    identificador del registro que representa
 * @param texto lo que se muestra en la lista
 */
public record Opcion(int id, String texto) {

    /**
     * {@code JComboBox} llama a {@code toString()} para saber qué texto pintar en cada
     * fila. Se sobrescribe para mostrar solo el texto, en vez del formato por defecto
     * de los records ({@code Opcion[id=3, texto=...]}).
     */
    @Override
    public String toString() {
        return texto;
    }

    /**
     * Busca en una lista la opción que tenga el ID indicado.
     *
     * <p>Usa <i>streams</i>: {@code filter} deja pasar solo las opciones con ese ID,
     * {@code findFirst} toma la primera y {@code orElse(null)} devuelve {@code null} si
     * no hubo ninguna.</p>
     *
     * @param opciones lista donde buscar
     * @param id       ID buscado
     * @return la opción encontrada, o {@code null} si no existe
     */
    public static Opcion conId(List<Opcion> opciones, int id) {
        return opciones.stream().filter(o -> o.id() == id).findFirst().orElse(null);
    }
}
