package com.uniajc.dao;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DataAccessExceptionTest {
    @Test
    void explicaCuandoLaTablaONoExisteElEsquema() {
        DataAccessException error = new DataAccessException("No se pudieron consultar los estudiantes.",
                new SQLException("relation does not exist", "42P01"));

        assertTrue(error.getMessage().contains("Ejecuta schema-postgres.sql"));
    }

    @Test
    void explicaCuandoNeonRechazaLasCredenciales() {
        DataAccessException error = new DataAccessException("No se pudo consultar la base de datos.",
                new SQLException("password authentication failed", "28P01"));

        assertTrue(error.getMessage().contains("rechazó las credenciales"));
        assertTrue(!error.getMessage().contains("password authentication failed"));
    }
}
