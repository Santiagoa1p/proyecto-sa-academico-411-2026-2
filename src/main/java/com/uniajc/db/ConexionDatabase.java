package com.uniajc.db;

// 1. Importar las clases necesarias de JDBC
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConexionDatabase {
    private ConexionDatabase() {
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(Path.of("config.properties"))) {
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException("No se pudo cargar config.properties.", e);
        }

        String url = properties.getProperty("URL");
        if (url == null || url.isBlank()) {
            throw new SQLException("La propiedad URL no está configurada en config.properties.");
        }

        validarUrl(url);

        String username = properties.getProperty("USERNAME", "");
        String password = properties.getProperty("PASSWORD", "");
        return DriverManager.getConnection(url.trim(), username.trim(), password);
    }

    static void validarUrl(String url) throws SQLException {
        if (url == null || url.isBlank()
                || !url.trim().startsWith("jdbc:postgresql://")
                || url.chars().anyMatch(Character::isWhitespace)) {
            throw new SQLException(
                    "La propiedad URL debe tener formato JDBC: "
                            + "jdbc:postgresql://HOST:5432/BASE_DE_DATOS?sslmode=require, sin espacios. "
                            + "Usa el host y la base de datos de Neon; configura USERNAME y PASSWORD por separado.");
        }
    }
}
