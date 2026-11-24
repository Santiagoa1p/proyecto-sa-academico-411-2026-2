package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Grupo;

/**
 * DAO de la tabla {@code "practica-mvc".grupos}
 * (columnas id_grupo, id_materia, id_docente, aula, horario).
 *
 * <p>Traduce entre objetos {@link Grupo} y filas de la tabla usando JDBC, con el mismo
 * patrón que {@link EstudianteDao}. Además del CRUD, ofrece métodos para contar
 * grupos por materia y por docente, que usan los servicios para no borrar materias
 * o docentes que todavía tienen grupos.</p>
 */
public class GrupoDao {

    /** Parte común de todas las consultas de grupos. */
    private static final String SELECT =
            "SELECT id_grupo, id_materia, id_docente, aula, horario FROM \"practica-mvc\".grupos";

    /**
     * Inserta un grupo nuevo y le asigna el ID generado por la base.
     *
     * @param grupo datos a guardar; su ID se actualiza al terminar
     * @throws ErrorBaseDatosException si la base rechaza la inserción
     */
    public void guardar(Grupo grupo) {
        String sql = "INSERT INTO \"practica-mvc\".grupos (id_materia, id_docente, aula, horario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, grupo.getIdMateria());
            pstmt.setInt(2, grupo.getIdDocente());
            pstmt.setString(3, grupo.getAula());
            pstmt.setString(4, grupo.getHorario());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    grupo.setIdGrupo(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar el grupo", e);
        }
    }

    /**
     * Consulta todos los grupos ordenados por ID.
     *
     * @return lista de grupos; vacía si no hay ninguno
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public List<Grupo> obtenerTodos() {
        List<Grupo> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id_grupo");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearGrupo(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar los grupos", e);
        }
        return lista;
    }

    /**
     * Busca un grupo por su ID.
     *
     * @param id identificador a buscar
     * @return el grupo si existe, o {@code Optional.empty()} si no
     * @throws ErrorBaseDatosException si falla la consulta
     */
    public Optional<Grupo> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id_grupo = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearGrupo(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el grupo", e);
        }
    }

    /**
     * Cuenta cuántos grupos dictan la materia indicada.
     * Lo usa {@code MateriaService} para no borrar materias en uso.
     *
     * @param idMateria ID de la materia
     * @return número de grupos de esa materia (0 si ninguno)
     */
    public int contarPorMateria(int idMateria) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".grupos WHERE id_materia = ?", idMateria);
    }

    /**
     * Cuenta cuántos grupos tiene asignados el docente.
     * Lo usa {@code DocenteService} para no borrar docentes en uso.
     *
     * @param idDocente ID del docente
     * @return número de grupos del docente (0 si ninguno)
     */
    public int contarPorDocente(int idDocente) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".grupos WHERE id_docente = ?", idDocente);
    }

    /**
     * Actualiza materia, docente, aula y horario de un grupo existente.
     *
     * @param grupo datos nuevos; su ID indica qué fila modificar
     * @throws IllegalArgumentException si no existe un grupo con ese ID
     * @throws ErrorBaseDatosException  si falla la actualización
     */
    public void actualizar(Grupo grupo) {
        String sql = "UPDATE \"practica-mvc\".grupos SET id_materia = ?, id_docente = ?, aula = ?, horario = ? WHERE id_grupo = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, grupo.getIdMateria());
            pstmt.setInt(2, grupo.getIdDocente());
            pstmt.setString(3, grupo.getAula());
            pstmt.setString(4, grupo.getHorario());
            pstmt.setInt(5, grupo.getIdGrupo());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Grupo no encontrado con ID: " + grupo.getIdGrupo());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar el grupo", e);
        }
    }

    /**
     * Elimina un grupo por su ID.
     *
     * @param id identificador del grupo a borrar
     * @throws IllegalArgumentException si no existe un grupo con ese ID
     * @throws ErrorBaseDatosException  si falla el borrado
     */
    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".grupos WHERE id_grupo = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Grupo no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar el grupo", e);
        }
    }

    /**
     * Ejecuta una consulta {@code SELECT COUNT(*) ... WHERE columna = ?} y devuelve el número.
     * Se reutiliza en los dos métodos {@code contarPor...} para no repetir código.
     */
    private int contar(String sql, int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                // COUNT(*) siempre devuelve exactamente una fila con un número.
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron contar los grupos", e);
        }
    }

    /** Convierte la fila actual del ResultSet en un objeto {@link Grupo}. */
    private Grupo mapearGrupo(ResultSet rs) throws SQLException {
        return new Grupo(rs.getInt("id_grupo"), rs.getInt("id_materia"), rs.getInt("id_docente"),
                rs.getString("aula"), rs.getString("horario"));
    }
}
