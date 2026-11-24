package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Estudiante;

/**
 * DAO (Data Access Object) de la tabla {@code "practica-mvc".estudiantes}.
 *
 * <p>Es la única clase que sabe escribir SQL para estudiantes. Traduce entre el
 * mundo de Java (objetos {@link Estudiante}) y el de la base de datos (filas de una
 * tabla) usando JDBC. Las capas de arriba solo llaman a sus métodos y no saben
 * nada de SQL.</p>
 *
 * <p>Patrón que se repite en todos los métodos:</p>
 * <ol>
 *   <li>Se pide la conexión compartida a {@link ConexionDatabase}.</li>
 *   <li>Se prepara la sentencia con {@link PreparedStatement}. Los valores van como
 *       {@code ?} y se llenan con {@code setString}/{@code setInt}; así el driver los
 *       escapa y se evita la <b>inyección SQL</b>.</li>
 *   <li>Se ejecuta y, si es una consulta, se recorre el {@link ResultSet}.</li>
 *   <li>Todo va dentro de un <b>try-with-resources</b>, que cierra automáticamente
 *       el PreparedStatement y el ResultSet. La conexión <b>no</b> se cierra porque
 *       es compartida.</li>
 *   <li>Si falla, la {@link SQLException} se envuelve en {@link ErrorBaseDatosException}.</li>
 * </ol>
 *
 * <p>El nombre del esquema va entre comillas dobles ({@code "practica-mvc"}) porque
 * tiene un guion, y PostgreSQL no acepta guiones en nombres sin comillas.</p>
 */
public class EstudianteDao {

    /** Parte común de todas las consultas; cada método le agrega su WHERE u ORDER BY. */
    private static final String SELECT = "SELECT id, name, lastname, email FROM \"practica-mvc\".estudiantes";

    /**
     * Inserta un estudiante nuevo (INSERT) y le asigna el ID que generó la base.
     *
     * <p>{@code RETURN_GENERATED_KEYS} le pide a la base que devuelva el {@code id}
     * autogenerado; se lee con {@code getGeneratedKeys()} y se guarda en el objeto.
     * Así quien llamó ya conoce el ID del estudiante nuevo.</p>
     *
     * @param estudiante datos a guardar; su ID se actualiza al terminar
     * @throws ErrorBaseDatosException si la base rechaza la inserción
     */
    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO \"practica-mvc\".estudiantes (name, lastname, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Cada ? se llena por posición (empiezan en 1, no en 0).
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    estudiante.setIdEstudiante(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar el estudiante", e);
        }
    }

    /**
     * Consulta todos los estudiantes (SELECT) ordenados por ID.
     *
     * @return lista con todos los estudiantes; vacía si no hay ninguno (nunca {@code null})
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public List<Estudiante> obtenerTodos() {
        List<Estudiante> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id");
             ResultSet rs = pstmt.executeQuery()) {
            // rs.next() avanza a la siguiente fila y devuelve false cuando ya no hay más.
            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar los estudiantes", e);
        }
        return lista;
    }

    /**
     * Busca un estudiante por su ID.
     *
     * <p>Devuelve un {@link Optional}: una "caja" que puede traer el estudiante o venir
     * vacía. Obliga a quien llama a revisar si existe, en vez de recibir {@code null}
     * y arriesgarse a un {@code NullPointerException}.</p>
     *
     * @param id identificador a buscar
     * @return el estudiante si existe, o {@code Optional.empty()} si no
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Estudiante> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearEstudiante(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el estudiante", e);
        }
    }

    /**
     * Busca un estudiante por su email, sin distinguir mayúsculas de minúsculas.
     *
     * <p>Lo usa el servicio para impedir emails repetidos. {@code LOWER()} pasa ambos
     * lados a minúsculas, así {@code ANA@x.co} y {@code ana@x.co} se consideran iguales.</p>
     *
     * @param email correo a buscar
     * @return el estudiante con ese email, o vacío si nadie lo tiene
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Estudiante> buscarPorEmail(String email) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE LOWER(email) = LOWER(?)")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearEstudiante(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el estudiante por email", e);
        }
    }

    /**
     * Actualiza los datos de un estudiante existente (UPDATE), identificado por su ID.
     *
     * @param estudiante datos nuevos; su ID indica qué fila modificar
     * @throws IllegalArgumentException si no existe ningún estudiante con ese ID
     * @throws ErrorBaseDatosException  si falla la actualización
     */
    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE \"practica-mvc\".estudiantes SET name = ?, lastname = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.setInt(4, estudiante.getIdEstudiante());
            // executeUpdate devuelve cuántas filas cambió; 0 significa que el ID no existe.
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Estudiante no encontrado con ID: " + estudiante.getIdEstudiante());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar el estudiante", e);
        }
    }

    /**
     * Elimina un estudiante por su ID (DELETE).
     *
     * @param id identificador del estudiante a borrar
     * @throws IllegalArgumentException si no existe ningún estudiante con ese ID
     * @throws ErrorBaseDatosException  si falla el borrado
     */
    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".estudiantes WHERE id = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Estudiante no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar el estudiante", e);
        }
    }

    /**
     * Convierte la fila actual del {@link ResultSet} en un objeto {@link Estudiante}.
     *
     * <p>Aquí está la "traducción" de nombres: la columna {@code name} de la base pasa a
     * ser el nombre del objeto, {@code lastname} el apellido, etc.</p>
     *
     * @param rs resultado posicionado en la fila a leer
     * @return el estudiante armado con los datos de esa fila
     * @throws SQLException si alguna columna no existe
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        return new Estudiante(rs.getInt("id"), rs.getString("name"), rs.getString("lastname"), rs.getString("email"));
    }
}
