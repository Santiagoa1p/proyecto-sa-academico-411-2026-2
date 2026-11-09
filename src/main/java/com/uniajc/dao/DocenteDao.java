package com.uniajc.dao;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Docente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocenteDao {
    private static final String TABLE = "\"practica-mvc\".docentes";
    private static final String COLUMNS = "id, name, specialty, email";

    public void guardar(Docente docente) {
        String sql = "INSERT INTO " + TABLE + " (name, specialty, email) VALUES (?, ?, ?)";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, docente.getNombre());
            statement.setString(2, docente.getEspecialidad());
            statement.setString(3, docente.getEmail());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    docente.setIdDocente(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo guardar el docente.", e);
        }
    }

    public List<Docente> obtenerTodos() {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " ORDER BY id";
        List<Docente> docentes = new ArrayList<>();
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                docentes.add(mapear(results));
            }
            return docentes;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron consultar los docentes.", e);
        }
    }

    public Optional<Docente> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNS + " FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapear(results)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el docente.", e);
        }
    }

    public void actualizar(Docente docente) {
        String sql = "UPDATE " + TABLE + " SET name = ?, specialty = ?, email = ? WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, docente.getNombre());
            statement.setString(2, docente.getEspecialidad());
            statement.setString(3, docente.getEmail());
            statement.setInt(4, docente.getIdDocente());
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el docente con ID " + docente.getIdDocente() + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el docente.", e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection connection = ConexionDatabase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("No existe el docente con ID " + id + ".");
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el docente.", e);
        }
    }

    private Docente mapear(ResultSet results) throws SQLException {
        return new Docente(
                results.getInt("id"),
                results.getString("name"),
                results.getString("specialty"),
                results.getString("email"));
    }
}
