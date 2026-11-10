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

/** Acceso a la tabla "practica-mvc".materias. */
public class MateriaDao {

    private static final String SELECT = "SELECT id, name, credits FROM \"practica-mvc\".materias";

    public void guardar(Materia materia) {
        String sql = "INSERT INTO \"practica-mvc\".materias (name, credits) VALUES (?, ?)";
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

    public List<Materia> obtenerTodos() {
        List<Materia> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMateria(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar las materias", e);
        }
        return lista;
    }

    public Optional<Materia> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " WHERE id = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearMateria(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la materia", e);
        }
    }

    public Optional<Materia> buscarPorNombre(String nombre) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE LOWER(name) = LOWER(?)")) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearMateria(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar la materia por nombre", e);
        }
    }

    public void actualizar(Materia materia) {
        String sql = "UPDATE \"practica-mvc\".materias SET name = ?, credits = ? WHERE id = ?";
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

    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".materias WHERE id = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Materia no encontrada con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar la materia", e);
        }
    }

    private Materia mapearMateria(ResultSet rs) throws SQLException {
        return new Materia(rs.getInt("id"), rs.getString("name"), rs.getInt("credits"));
    }
}
