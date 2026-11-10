package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Estudiante;

/**
 * Acceso a la tabla "practica-mvc".estudiantes.
 * Usa la conexión compartida de ConexionDatabase: solo se cierran los
 * PreparedStatement/ResultSet, nunca la conexión.
 */
public class EstudianteDao {

    private static final String SELECT = "SELECT id, name, lastname, email FROM \"practica-mvc\".estudiantes";

    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO \"practica-mvc\".estudiantes (name, lastname, email) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    estudiante.setIdEstudiante(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar el estudiante", e);
        }
    }

    public List<Estudiante> obtenerTodos() {
        List<Estudiante> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar los estudiantes", e);
        }
        return lista;
    }

    public Optional<Estudiante> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearEstudiante(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el estudiante", e);
        }
    }

    public Optional<Estudiante> buscarPorEmail(String email) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE LOWER(email) = LOWER(?)")) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearEstudiante(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el estudiante por email", e);
        }
    }

    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE \"practica-mvc\".estudiantes SET name = ?, lastname = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.setInt(4, estudiante.getIdEstudiante());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Estudiante no encontrado con ID: " + estudiante.getIdEstudiante());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar el estudiante", e);
        }
    }

    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".estudiantes WHERE id = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Estudiante no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar el estudiante", e);
        }
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        return new Estudiante(rs.getInt("id"), rs.getString("name"), rs.getString("lastname"), rs.getString("email"));
    }
}
