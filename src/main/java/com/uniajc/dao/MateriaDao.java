package com.uniajc.dao;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Materia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MateriaDao {
    private static final String TABLE = "\"practica-mvc\".materias";
    private static final String COLUMNS = "id, name, credits";

    public void guardar(Materia materia) {
        String sql = "INSERT INTO " + TABLE + " (name, credits) VALUES (?, ?)";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, materia.getNombreMateria());
            statement.setInt(2, materia.getCreditos());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    materia.setIdMateria(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar la materia.", e);
        }
    }

    public List<Materia> obtenerTodos() {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " ORDER BY id";
        List<Materia> materias = new ArrayList<>();
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                materias.add(mapear(results));
            }
            return materias;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar las materias.", e);
        }
    }

    public Optional<Materia> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapear(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar la materia.", e);
        }
    }

    public void actualizar(Materia materia) {
        String sql = "UPDATE " + TABLE + " SET name = ?, credits = ? WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, materia.getNombreMateria());
            statement.setInt(2, materia.getCreditos());
            statement.setInt(3, materia.getIdMateria());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe la materia con ID " + materia.getIdMateria() + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la materia.", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe la materia con ID " + id + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la materia.", e);
        }
    }

    private Materia mapear(ResultSet results) throws SQLException {
        return new Materia(results.getInt("id"), results.getString("name"), results.getInt("credits"));
    }
}
