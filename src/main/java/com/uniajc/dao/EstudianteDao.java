package com.uniajc.dao;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstudianteDao {
    private static final String TABLE = "\"practica-mvc\".estudiantes";
    private static final String COLUMNS = "id, name, lastname, email";

    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO " + TABLE + " (name, lastname, email) VALUES (?, ?, ?)";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getApellido());
            statement.setString(3, estudiante.getEmail());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    estudiante.setIdEstudiante(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar el estudiante.", e);
        }
    }

    public List<Estudiante> obtenerTodos() {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " ORDER BY id";
        List<Estudiante> estudiantes = new ArrayList<>();
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                estudiantes.add(mapear(results));
            }
            return estudiantes;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los estudiantes.", e);
        }
    }

    public Optional<Estudiante> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapear(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el estudiante.", e);
        }
    }

    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE " + TABLE + " SET name = ?, lastname = ?, email = ? WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getApellido());
            statement.setString(3, estudiante.getEmail());
            statement.setInt(4, estudiante.getIdEstudiante());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el estudiante con ID " + estudiante.getIdEstudiante() + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el estudiante.", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el estudiante con ID " + id + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el estudiante.", e);
        }
    }

    private Estudiante mapear(ResultSet results) throws SQLException {
        return new Estudiante(
                results.getInt("id"),
                results.getString("name"),
                results.getString("lastname"),
                results.getString("email"));
    }
}
