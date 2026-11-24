package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Docente;

/**
 * DAO de la tabla {@code "practica-mvc".docentes} (columnas id_docente, nombre, especialidad).
 *
 * <p>Traduce entre objetos {@link Docente} y filas de la tabla usando JDBC. Sigue el
 * mismo patrón que {@link EstudianteDao}: conexión compartida, {@link PreparedStatement}
 * con parámetros {@code ?} contra la inyección SQL, try-with-resources y errores
 * envueltos en {@link ErrorBaseDatosException}.</p>
 */
public class DocenteDao {

    /** Parte común de todas las consultas de docentes. */
    private static final String SELECT = "SELECT id_docente, nombre, especialidad FROM \"practica-mvc\".docentes";

    /**
     * Inserta un docente nuevo y le asigna el ID generado por la base.
     *
     * @param docente datos a guardar; su ID se actualiza al terminar
     * @throws ErrorBaseDatosException si la base rechaza la inserción
     */
    public void guardar(Docente docente) {
        String sql = "INSERT INTO \"practica-mvc\".docentes (nombre, especialidad) VALUES (?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    docente.setIdDocente(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar el docente", e);
        }
    }

    /**
     * Consulta todos los docentes ordenados por ID.
     *
     * @return lista de docentes; vacía si no hay ninguno
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public List<Docente> obtenerTodos() {
        List<Docente> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id_docente");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar los docentes", e);
        }
        return lista;
    }

    /**
     * Busca un docente por su ID.
     *
     * @param id identificador a buscar
     * @return el docente si existe, o {@code Optional.empty()} si no
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Docente> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE id_docente = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearDocente(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el docente", e);
        }
    }

    /**
     * Actualiza el nombre y la especialidad de un docente existente.
     *
     * @param docente datos nuevos; su ID indica qué fila modificar
     * @throws IllegalArgumentException si no existe un docente con ese ID (0 filas afectadas)
     * @throws ErrorBaseDatosException  si falla la actualización
     */
    public void actualizar(Docente docente) {
        String sql = "UPDATE \"practica-mvc\".docentes SET nombre = ?, especialidad = ? WHERE id_docente = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.setInt(3, docente.getIdDocente());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Docente no encontrado con ID: " + docente.getIdDocente());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar el docente", e);
        }
    }

    /**
     * Elimina un docente por su ID.
     *
     * @param id identificador del docente a borrar
     * @throws IllegalArgumentException si no existe un docente con ese ID
     * @throws ErrorBaseDatosException  si falla el borrado
     */
    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".docentes WHERE id_docente = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Docente no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar el docente", e);
        }
    }

    /** Convierte la fila actual del ResultSet en un objeto {@link Docente}. */
    private Docente mapearDocente(ResultSet rs) throws SQLException {
        return new Docente(rs.getInt("id_docente"), rs.getString("nombre"), rs.getString("especialidad"));
    }
}
