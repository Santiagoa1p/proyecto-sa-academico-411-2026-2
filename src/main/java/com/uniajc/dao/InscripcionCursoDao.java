package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.InscripcionCurso;

/** Acceso a la tabla "practica-mvc".inscripciones. */
public class InscripcionCursoDao {

    private static final String SELECT =
            "SELECT id_inscripcion, id_estudiante, id_grupo, nota_final, estado FROM \"practica-mvc\".inscripciones";

    public void guardar(InscripcionCurso inscripcion) {
        String sql = "INSERT INTO \"practica-mvc\".inscripciones (id_estudiante, id_grupo, nota_final, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inscripcion.getIdEstudiante());
            pstmt.setInt(2, inscripcion.getIdGrupo());
            asignarNota(pstmt, 3, inscripcion.getNotaFinal());
            pstmt.setString(4, inscripcion.getEstado());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    inscripcion.setIdInscripcion(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar la inscripción", e);
        }
    }

    public List<InscripcionCurso> obtenerTodos() {
        List<InscripcionCurso> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id_inscripcion");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearInscripcion(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar las inscripciones", e);
        }
        return lista;
    }

    public Optional<InscripcionCurso> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id_inscripcion = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearInscripcion(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la inscripción", e);
        }
    }

    /** Busca si el estudiante ya está inscrito en el grupo. */
    public Optional<InscripcionCurso> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE id_estudiante = ? AND id_grupo = ?")) {
            pstmt.setInt(1, idEstudiante);
            pstmt.setInt(2, idGrupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearInscripcion(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la inscripción", e);
        }
    }

    public int contarPorEstudiante(int idEstudiante) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".inscripciones WHERE id_estudiante = ?", idEstudiante);
    }

    public int contarPorGrupo(int idGrupo) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".inscripciones WHERE id_grupo = ?", idGrupo);
    }

    public void actualizar(InscripcionCurso inscripcion) {
        String sql = "UPDATE \"practica-mvc\".inscripciones SET id_estudiante = ?, id_grupo = ?, nota_final = ?, estado = ? WHERE id_inscripcion = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, inscripcion.getIdEstudiante());
            pstmt.setInt(2, inscripcion.getIdGrupo());
            asignarNota(pstmt, 3, inscripcion.getNotaFinal());
            pstmt.setString(4, inscripcion.getEstado());
            pstmt.setInt(5, inscripcion.getIdInscripcion());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Inscripción no encontrada con ID: " + inscripcion.getIdInscripcion());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar la inscripción", e);
        }
    }

    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".inscripciones WHERE id_inscripcion = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Inscripción no encontrada con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar la inscripción", e);
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
            throw new ErrorBaseDatosException("No se pudieron contar las inscripciones", e);
        }
    }

    /**
     * La nota es double precision en la BD. Se convierte desde el texto del
     * float para que 4.2f se guarde como 4.2 y no como 4.199999809.
     */
    private void asignarNota(PreparedStatement pstmt, int indice, Float nota) throws SQLException {
        if (nota == null) {
            pstmt.setNull(indice, Types.DOUBLE);
        } else {
            pstmt.setDouble(indice, Double.parseDouble(Float.toString(nota)));
        }
    }

    private InscripcionCurso mapearInscripcion(ResultSet rs) throws SQLException {
        double nota = rs.getDouble("nota_final");
        Float notaFinal = rs.wasNull() ? null : (float) nota;
        return new InscripcionCurso(rs.getInt("id_inscripcion"), rs.getInt("id_estudiante"), rs.getInt("id_grupo"),
                notaFinal, rs.getString("estado"));
    }
}
