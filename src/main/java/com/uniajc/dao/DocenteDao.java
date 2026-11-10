package com.uniajc.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.ConexionDatabase;
import com.uniajc.modelo.Docente;

/** Acceso a la tabla "practica-mvc".docentes (id_docente, nombre, especialidad). */
public class DocenteDao {

    private static final String SELECT = "SELECT id_docente, nombre, especialidad FROM \"practica-mvc\".docentes";

    public void guardar(Docente docente) {
        String sql = "INSERT INTO \"practica-mvc\".docentes (nombre, especialidad) VALUES (?, ?)";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    docente.setIdDocente(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo guardar el docente", e);
        }
    }

    public List<Docente> obtenerTodos() {
        List<Docente> lista = new ArrayList<>();
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(SELECT + " ORDER BY id_docente");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron consultar los docentes", e);
        }
        return lista;
    }

    public Optional<Docente> buscarPorId(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement(SELECT + " WHERE id_docente = ?")) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapearDocente(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo buscar el docente", e);
        }
    }

    public void actualizar(Docente docente) {
        String sql = "UPDATE \"practica-mvc\".docentes SET nombre = ?, especialidad = ? WHERE id_docente = ?";
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.setInt(3, docente.getIdDocente());
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Docente no encontrado con ID: " + docente.getIdDocente());
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo actualizar el docente", e);
        }
    }

    public void eliminar(int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection()
                .prepareStatement("DELETE FROM \"practica-mvc\".docentes WHERE id_docente = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Docente no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudo eliminar el docente", e);
        }
    }

    private Docente mapearDocente(ResultSet rs) throws SQLException {
        return new Docente(rs.getInt("id_docente"), rs.getString("nombre"), rs.getString("especialidad"));
    }
}
