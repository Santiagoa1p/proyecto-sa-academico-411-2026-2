package com.uniajc.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class ConexionDatabaseTest {

    @Test
    void convierteLaUrlDeNeonAFormatoJdbc() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "postgresql://neondb_owner:abc%40123@ep-demo.us-east-2.aws.neon.tech/neondb?sslmode=require",
                "", "");

        assertEquals("jdbc:postgresql://ep-demo.us-east-2.aws.neon.tech/neondb?sslmode=require", datos.url());
        assertEquals("neondb_owner", datos.usuario());
        assertEquals("abc@123", datos.clave());
    }

    @Test
    void respetaUsuarioYClaveEscritosAparte() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "postgresql://otro:otra@host:5432/base", "usuario", "clave");

        assertEquals("jdbc:postgresql://host:5432/base", datos.url());
        assertEquals("usuario", datos.usuario());
        assertEquals("clave", datos.clave());
    }

    @Test
    void dejaIgualUnaUrlJdbc() throws SQLException {
        ConexionDatabase.DatosConexion datos = ConexionDatabase.normalizar(
                "jdbc:postgresql://host/base?sslmode=require", "u", "c");

        assertEquals("jdbc:postgresql://host/base?sslmode=require", datos.url());
    }

    @Test
    void rechazaUrlsConEspaciosOFormatoDesconocido() {
        assertThrows(SQLException.class, () -> ConexionDatabase.normalizar("jdbc:postgresql://host/ base", "", ""));
        assertThrows(SQLException.class, () -> ConexionDatabase.normalizar("mysql//host/base", "", ""));
    }
}
