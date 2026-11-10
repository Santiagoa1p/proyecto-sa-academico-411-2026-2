package com.uniajc.db;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConexionDatabaseTest {
    @Test
    void aceptaUrlJdbcDePostgresqlSinEspacios() {
        assertDoesNotThrow(() ->
                ConexionDatabase.validarUrl("jdbc:postgresql://db.example:5432/academico?sslmode=require"));
    }

    @Test
    void rechazaUrlNeonQueNoEsteEnFormatoJdbc() {
        SQLException error = assertThrows(SQLException.class,
                () -> ConexionDatabase.validarUrl("postgresql://usuario@db.example/academico"));

        assertTrue(error.getMessage().contains("jdbc:postgresql://HOST:5432/BASE_DE_DATOS"));
    }

    @Test
    void rechazaUrlConEspacios() {
        SQLException error = assertThrows(SQLException.class,
                () -> ConexionDatabase.validarUrl("jdbc:postgresql://db.example:5432/academico con espacios"));

        assertTrue(error.getMessage().contains("sin espacios"));
    }
}
