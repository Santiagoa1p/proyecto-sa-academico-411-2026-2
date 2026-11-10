package com.uniajc.dao;

import java.sql.SQLException;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message + diagnostico(cause), cause);
    }

    public DataAccessException(String message) {
        super(message);
    }

    private static String diagnostico(Throwable cause) {
        if (!(cause instanceof SQLException sqlException)) {
            return "";
        }

        String estado = sqlException.getSQLState();
        if (estado == null) {
            return ": " + sqlException.getMessage();
        }
        if (estado.startsWith("08")) {
            return " No se pudo conectar a PostgreSQL. Revisa la URL de Neon y la conectividad "
                    + "(SQLState " + estado + ").";
        }
        if ("28P01".equals(estado)) {
            return " PostgreSQL rechazó las credenciales configuradas (SQLState " + estado + ").";
        }
        if ("42P01".equals(estado) || "3F000".equals(estado)) {
            return " No existe la tabla o el esquema. Ejecuta schema-postgres.sql en la base de Neon.";
        }
        if ("23505".equals(estado)) {
            return " Ya existe un registro con un valor que debe ser único.";
        }
        if ("23503".equals(estado)) {
            return " La operación viola una relación entre registros; revisa los datos vinculados.";
        }
        return " Error de PostgreSQL (SQLState " + estado + ").";
    }
}
