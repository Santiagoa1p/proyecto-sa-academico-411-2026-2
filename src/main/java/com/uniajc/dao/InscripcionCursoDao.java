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

/**
 * DAO de la tabla {@code "practica-mvc".inscripciones}
 * (columnas id_inscripcion, id_estudiante, id_grupo, nota_final, estado).
 *
 * <p>Traduce entre objetos {@link InscripcionCurso} y filas de la tabla usando JDBC,
 * con el mismo patrón que {@link EstudianteDao}. Detalle especial: la nota puede
 * ser {@code null} (curso en progreso), así que se lee y se escribe con cuidado
 * (ver {@link #asignarNota} y {@link #mapearInscripcion}).</p>
 */
public class InscripcionCursoDao {

    /** Parte común de todas las consultas de inscripciones. */
    private static final String SELECT =
            "SELECT id_inscripcion, id_estudiante, id_grupo, nota_final, estado FROM \"practica-mvc\".inscripciones";

    /**
     * Inserta una inscripción nueva y le asigna el ID generado por la base.
     *
     * @param inscripcion datos a guardar; su ID se actualiza al terminar
     * @throws ErrorBaseDatosException si la base rechaza la inserción
     */
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

    /**
     * Consulta todas las inscripciones ordenadas por ID.
     *
     * @return lista de inscripciones; vacía si no hay ninguna
     * @throws ErrorBaseDatosException si falla la consulta
     */
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

    /**
     * Busca una inscripción por su ID.
     *
     * @param id identificador a buscar
     * @return la inscripción si existe, o {@code Optional.empty()} si no
     * @throws ErrorBaseDatosException si falla la consulta
     */
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

    /**
     * Busca si un estudiante ya está inscrito en un grupo.
     * Lo usa el servicio para impedir inscripciones duplicadas.
     *
     * @param idEstudiante ID del estudiante
     * @param idGrupo      ID del grupo
     * @return la inscripción existente, o vacío si el estudiante no está en ese grupo
     * @throws ErrorBaseDatosException si falla la consulta
     */
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

    /**
     * Cuenta en cuántos grupos está inscrito un estudiante.
     * Lo usa {@code EstudianteService} para no borrar estudiantes con inscripciones.
     *
     * @param idEstudiante ID del estudiante
     * @return número de inscripciones del estudiante
     */
    public int contarPorEstudiante(int idEstudiante) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".inscripciones WHERE id_estudiante = ?", idEstudiante);
    }

    /**
     * Cuenta cuántos estudiantes hay inscritos en un grupo.
     * Lo usa {@code GrupoService} para no borrar grupos con estudiantes.
     *
     * @param idGrupo ID del grupo
     * @return número de inscripciones del grupo
     */
    public int contarPorGrupo(int idGrupo) {
        return contar("SELECT COUNT(*) FROM \"practica-mvc\".inscripciones WHERE id_grupo = ?", idGrupo);
    }

    /**
     * Actualiza todos los datos de una inscripción existente (estudiante, grupo, nota y estado).
     *
     * @param inscripcion datos nuevos; su ID indica qué fila modificar
     * @throws IllegalArgumentException si no existe una inscripción con ese ID
     * @throws ErrorBaseDatosException  si falla la actualización
     */
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

    /**
     * Elimina una inscripción por su ID.
     *
     * @param id identificador de la inscripción a borrar
     * @throws IllegalArgumentException si no existe una inscripción con ese ID
     * @throws ErrorBaseDatosException  si falla el borrado
     */
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

    /**
     * Ejecuta una consulta {@code SELECT COUNT(*) ... WHERE columna = ?} y devuelve el número.
     * Se reutiliza en los dos métodos {@code contarPor...}.
     */
    private int contar(String sql, int id) {
        try (PreparedStatement pstmt = ConexionDatabase.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                // COUNT(*) siempre devuelve exactamente una fila con un número.
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new ErrorBaseDatosException("No se pudieron contar las inscripciones", e);
        }
    }

    /**
     * Pone la nota en el parámetro {@code indice} de la sentencia.
     *
     * <ul>
     *   <li>Si la nota es {@code null} (aún no hay nota), se guarda NULL en la base con
     *       {@code setNull}, porque {@code setDouble} no acepta null.</li>
     *   <li>La columna es {@code double precision}. Convertir un {@code float} directo a
     *       {@code double} arrastra error de redondeo binario (4.2f → 4.199999809...).
     *       Por eso se pasa por texto: {@code "4.2"} → 4.2 exacto.</li>
     * </ul>
     *
     * @param pstmt  sentencia a la que se le asigna el valor
     * @param indice posición del {@code ?} (empieza en 1)
     * @param nota   nota a guardar, o {@code null}
     * @throws SQLException si el índice no es válido
     */
    private void asignarNota(PreparedStatement pstmt, int indice, Float nota) throws SQLException {
        if (nota == null) {
            pstmt.setNull(indice, Types.DOUBLE);
        } else {
            pstmt.setDouble(indice, Double.parseDouble(Float.toString(nota)));
        }
    }

    /**
     * Convierte la fila actual del ResultSet en un objeto {@link InscripcionCurso}.
     *
     * <p>{@code getDouble} devuelve 0.0 cuando la columna es NULL, y así no se podría
     * distinguir "sin nota" de "nota 0". Por eso, justo después, {@code wasNull()}
     * pregunta si el último valor leído era NULL de verdad.</p>
     */
    private InscripcionCurso mapearInscripcion(ResultSet rs) throws SQLException {
        double nota = rs.getDouble("nota_final");
        Float notaFinal = rs.wasNull() ? null : (float) nota;
        return new InscripcionCurso(rs.getInt("id_inscripcion"), rs.getInt("id_estudiante"), rs.getInt("id_grupo"),
                notaFinal, rs.getString("estado"));
    }
}
