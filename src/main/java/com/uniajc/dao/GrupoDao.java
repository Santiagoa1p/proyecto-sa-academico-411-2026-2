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

/** Acceso a la tabla "practica-mvc".grupos. */
public class GrupoDao {

    private static final String SELECT =
            "SELECT id_grupo, id_materia, id_docente, aula, horario FROM \"practica-mvc\".grupos";

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

    /** Cuántos grupos usan la materia indicada (para no borrar materias en uso). */
    public int contarPorMateria(int idMateria) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".grupos WHERE id_materia = ?", idMateria);
    }

    /** Cuántos grupos tiene asignados el docente (para no borrar docentes en uso). */
    public int contarPorDocente(int idDocente) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".grupos WHERE id_docente = ?", idDocente);
    }

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

    private int contar(String sql, int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron contar los grupos", e);
        }
    }

    private Grupo mapearGrupo(ResultSet rs) throws SQLException {
        return new Grupo(rs.getInt("id_grupo"), rs.getInt("id_materia"), rs.getInt("id_docente"),
                rs.getString("aula"), rs.getString("horario"));
    }
}
