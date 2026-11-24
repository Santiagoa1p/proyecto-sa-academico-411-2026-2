package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Materia;

/**
 * DAO de la tabla {@code "practica-mvc".materias} (columnas id_materia, nombre_materia, creditos).
 *
 * <p>Traduce entre objetos {@link Materia} y filas de la tabla usando JDBC. Sigue el
 * mismo patrón que {@link EstudianteDao}: conexión compartida, {@link PreparedStatement}
 * con parámetros {@code ?}, try-with-resources y errores envueltos en
 * {@link ErrorBaseDatosException}.</p>
 */
public class MateriaDao {

    /** Parte común de todas las consultas de materias. */
    private static final String SELECT = "SELECT id_materia, nombre_materia, creditos FROM \"practica-mvc\".materias";

    /**
     * Inserta una materia nueva y le asigna el ID generado por la base.
     *
     * @param materia datos a guardar; su ID se actualiza al terminar
     * @throws ErrorBaseDatosException si la base rechaza la inserción
     */
    public void guardar(Materia materia) {
        String sql = "INSERT INTO \"practica-mvc\".materias (nombre_materia, creditos) VALUES (?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, materia.getNombreMateria());
            pstmt.setInt(2, materia.getCreditos());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    materia.setIdMateria(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar la materia", e);
        }
    }

    /**
     * Consulta todas las materias ordenadas por ID.
     *
     * @return lista de materias; vacía si no hay ninguna
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public List<Materia> obtenerTodos() {
        List<Materia> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id_materia");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMateria(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar las materias", e);
        }
        return lista;
    }

    /**
     * Busca una materia por su ID.
     *
     * @param id identificador a buscar
     * @return la materia si existe, o {@code Optional.empty()} si no
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Materia> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id_materia = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearMateria(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la materia", e);
        }
    }

    /**
     * Busca una materia por su nombre, sin distinguir mayúsculas de minúsculas.
     * La usa el servicio para impedir materias con el mismo nombre.
     *
     * @param nombre nombre a buscar
     * @return la materia con ese nombre, o vacío si no hay ninguna
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Materia> buscarPorNombre(String nombre) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE LOWER(nombre_materia) = LOWER(?)")) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearMateria(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la materia por nombre", e);
        }
    }

    /**
     * Actualiza el nombre y los créditos de una materia existente.
     *
     * @param materia datos nuevos; su ID indica qué fila modificar
     * @throws IllegalArgumentException si no existe una materia con ese ID
     * @throws ErrorBaseDatosException  si falla la actualización
     */
    public void actualizar(Materia materia) {
        String sql = "UPDATE \"practica-mvc\".materias SET nombre_materia = ?, creditos = ? WHERE id_materia = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, materia.getNombreMateria());
            pstmt.setInt(2, materia.getCreditos());
            pstmt.setInt(3, materia.getIdMateria());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Materia no encontrada con ID: " + materia.getIdMateria());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar la materia", e);
        }
    }

    /**
     * Elimina una materia por su ID.
     *
     * @param id identificador de la materia a borrar
     * @throws IllegalArgumentException si no existe una materia con ese ID
     * @throws ErrorBaseDatosException  si falla el borrado
     */
    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".materias WHERE id_materia = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Materia no encontrada con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar la materia", e);
        }
    }

    /** Convierte la fila actual del ResultSet en un objeto {@link Materia}. */
    private Materia mapearMateria(ResultSet rs) throws SQLException {
        return new Materia(rs.getInt("id_materia"), rs.getString("nombre_materia"), rs.getInt("creditos"));
    }
}
