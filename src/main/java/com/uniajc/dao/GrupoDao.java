package com.uniajc.dao;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Grupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrupoDao {
    private static final String TABLE = "\"practica-mvc\".grupos";
    private static final String COLUMNS = "id, id_materia, id_docente, classroom, schedule";

    public void guardar(Grupo grupo) {
        String sql = "INSERT INTO " + TABLE + " (id_materia, id_docente, classroom, schedule) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, grupo.getIdMateria());
            statement.setInt(2, grupo.getIdDocente());
            statement.setString(3, grupo.getAula());
            statement.setString(4, grupo.getHorario());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    grupo.setIdGrupo(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar el grupo.", e);
        }
    }

    public List<Grupo> obtenerTodos() {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " ORDER BY id";
        List<Grupo> grupos = new ArrayList<>();
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                grupos.add(mapear(results));
            }
            return grupos;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los grupos.", e);
        }
    }

    public Optional<Grupo> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapear(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el grupo.", e);
        }
    }

    public void actualizar(Grupo grupo) {
        String sql = "UPDATE " + TABLE + " SET id_materia = ?, id_docente = ?, classroom = ?, schedule = ? WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, grupo.getIdMateria());
            statement.setInt(2, grupo.getIdDocente());
            statement.setString(3, grupo.getAula());
            statement.setString(4, grupo.getHorario());
            statement.setInt(5, grupo.getIdGrupo());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el grupo con ID " + grupo.getIdGrupo() + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el grupo.", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el grupo con ID " + id + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el grupo.", e);
        }
    }

    private Grupo mapear(ResultSet results) throws SQLException {
        return new Grupo(
                results.getInt("id"),
                results.getInt("id_materia"),
                results.getInt("id_docente"),
                results.getString("classroom"),
                results.getString("schedule"));
    }
}
