package com.uniajc.modelo;

/**
 * Entidad (POJO) que representa a un estudiante de la universidad.
 *
 * <p>Pertenece a la capa <b>Modelo</b> del patrón MVC: solo guarda datos y no
 * contiene lógica de negocio, acceso a base de datos ni interacción con el
 * usuario. Cada objeto corresponde a una fila de la tabla
 * {@code "practica-mvc".estudiantes}.</p>
 *
 * <p>Correspondencia atributo → columna (la hace {@code EstudianteDao}):</p>
 * <ul>
 *   <li>{@code idEstudiante} → {@code id}</li>
 *   <li>{@code nombre} → {@code name}</li>
 *   <li>{@code apellido} → {@code lastname}</li>
 *   <li>{@code email} → {@code email}</li>
 * </ul>
 */
public class Estudiante {

    /** Identificador único. Lo genera la base de datos al guardar; vale 0 mientras no se haya guardado. */
    private int idEstudiante;
    /** Nombre(s) del estudiante. Obligatorio. */
    private String nombre;
    /** Apellido(s). Puede ser {@code null} en registros antiguos de la base compartida. */
    private String apellido;
    /** Correo electrónico. El servicio garantiza que no se repita. */
    private String email;

    /** Constructor vacío, requerido por la convención POJO; los datos se asignan luego con los setters. */
    public Estudiante() {
    }

    /**
     * Crea un estudiante con todos sus datos.
     *
     * @param idEstudiante identificador; usar 0 para un estudiante nuevo que aún no está en la base
     * @param nombre       nombre(s) del estudiante
     * @param apellido     apellido(s) del estudiante
     * @param email        correo electrónico
     */
    public Estudiante(int idEstudiante, String nombre, String apellido, String email) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Getters y setters: dan acceso controlado a los atributos privados (encapsulamiento).

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Devuelve el nombre y el apellido juntos, para mostrarlos en listas y menús.
     *
     * <p>Si el apellido es {@code null} o está vacío (pasa en registros antiguos de la
     * base compartida), devuelve solo el nombre para no mostrar "null".</p>
     *
     * @return el nombre completo, por ejemplo {@code "Ana Pérez"}
     */
    public String getNombreCompleto() {
        return apellido == null || apellido.isBlank() ? nombre : nombre + " " + apellido;
    }
}
