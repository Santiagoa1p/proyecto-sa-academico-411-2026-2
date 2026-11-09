package com.uniajc.dao;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.InscripcionCurso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InscripcionCursoDao {
    private static final String TABLE = "\"practica-mvc\".inscripciones";
    private static final String COLUMNS = "id, id_estudiante, id_grupo, final_grade, status";

    public void guardar(InscripcionCurso inscripcion) {
        String sql = "INSERT INTO " + TABLE + " (id_estudiante, id_grupo, final_grade, status) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, inscripcion.getIdEstudiante());
            statement.setInt(2, inscripcion.getIdGrupo());
            setNota(statement, 3, inscripcion.getNotaFinal());
            statement.setString(4, inscripcion.getEstado());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    inscripcion.setIdInscripcion(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar la inscripción.", e);
        }
    }

    public List<InscripcionCurso> obtenerTodos() {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " ORDER BY id";
        List<InscripcionCurso> inscripciones = new ArrayList<>();
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                inscripciones.add(mapear(results));
            }
            return inscripciones;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las inscripciones.", e);
        }
    }

    public Optional<InscripcionCurso> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapear(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar la inscripción.", e);
        }
    }

    public boolean existeParaEstudianteYGrupo(int idEstudiante, int idGrupo, int idInscripcionExcluida) {
        String sql = "SELECT 1 FROM " + TABLE
                + " WHERE id_estudiante = ? AND id_grupo = ? AND id <> ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEstudiante);
            statement.setInt(2, idGrupo);
            statement.setInt(3, idInscripcionExcluida);
            try (ResultSet results = statement.executeQuery()) {
                return results.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo verificar la inscripción existente.", e);
        }
    }

    public void actualizar(InscripcionCurso inscripcion) {
        String sql = "UPDATE " + TABLE + " SET id_estudiante = ?, id_grupo = ?, final_grade = ?, status = ? WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inscripcion.getIdEstudiante());
            statement.setInt(2, inscripcion.getIdGrupo());
            setNota(statement, 3, inscripcion.getNotaFinal());
            statement.setString(4, inscripcion.getEstado());
            statement.setInt(5, inscripcion.getIdInscripcion());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe la inscripción con ID " + inscripcion.getIdInscripcion() + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la inscripción.", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe la inscripción con ID " + id + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la inscripción.", e);
        }
    }

    private void setNota(PreparedStatement statement, int index, Float nota) throws SQLException {
        if (nota == null) {
            statement.setNull(index, Types.DECIMAL);
        } else {
            statement.setFloat(index, nota);
        }
    }

    private InscripcionCurso mapear(ResultSet results) throws SQLException {
        float nota = results.getFloat("final_grade");
        Float notaFinal = results.wasNull() ? null : nota;
        return new InscripcionCurso(
                results.getInt("id"),
                results.getInt("id_estudiante"),
                results.getInt("id_grupo"),
                notaFinal,
                results.getString("status"));
    }
}
