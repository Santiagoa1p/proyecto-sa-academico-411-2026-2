package com.uniajc.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la conversión de URLs de {@link ConexionDatabase}.
 *
 * <p>Solo prueban el método {@code normalizar}, que no se conecta a nada, así que
 * corren sin internet y sin tocar Neon.</p>
 */
class ConexionDatabaseTest {

    /** La URL tal como la entrega Neon se convierte a JDBC, y el usuario y la clave se extraen de ella (decodificando %40 → @). */
    @Test
    void convierteLaUrlDeNeonAFormatoJdbc() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "postgresql://neondb_owner:abc%40123@ep-demo.us-east-2.aws.neon.tech/neondb?sslmode=require",
                "", "");

        assertEquals("jdbc:postgresql://ep-demo.us-east-2.aws.neon.tech/neondb?sslmode=require", datos.url());
        assertEquals("neondb_owner", datos.usuario());
        assertEquals("abc@123", datos.clave());
    }

    /** Si USERNAME y PASSWORD vienen escritos aparte, tienen prioridad sobre los que trae la URL. */
    @Test
    void respetaUsuarioYClaveEscritosAparte() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "postgresql://otro:otra@host:5432/base", "usuario", "clave");

        assertEquals("jdbc:postgresql://host:5432/base", datos.url());
        assertEquals("usuario", datos.usuario());
        assertEquals("clave", datos.clave());
    }

    /** Una URL que ya está en formato JDBC no se modifica. */
    @Test
    void dejaIgualUnaUrlJdbc() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "jdbc:postgresql://host/base?sslmode=require", "u", "c");

        assertEquals("jdbc:postgresql://host/base?sslmode=require", datos.url());
    }

    /** Se rechazan URLs con espacios o con un prefijo que no es de PostgreSQL. */
    @Test
    void rechazaUrlsConEspaciosOFormatoDesconocido() {
        assertThrows(SQLException.class, () -> ConexionDatabase.normalizar("jdbc:postgresql://host/ base", "", ""));
        assertThrows(SQLException.class, () -> ConexionDatabase.normalizar("mysql//host/base", "", ""));
    }
}
