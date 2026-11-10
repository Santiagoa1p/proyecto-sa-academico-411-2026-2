package com.uniajc.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexión única (singleton) a la base de datos.
 *
 * Lee URL, USERNAME y PASSWORD desde config.properties (en la raíz del proyecto).
 * La URL puede escribirse en formato JDBC (jdbc:postgresql://...) o pegarse tal
 * cual la entrega Neon (postgresql://usuario:clave@host/base?sslmode=require).
 *
 * Esta clase no imprime nada: ante cualquier problema lanza SQLException con un
 * mensaje claro para que la Vista decida cómo mostrarlo.
 */
public final class ConexionDatabase {

    private static final String ARCHIVO_CONFIG = "config.properties";
    private static final int SEGUNDOS_VALIDACION = 5;

    private static Connection connection;

    private ConexionDatabase() {
    }

    /**
     * Devuelve la conexión abierta. Si no existe, se cerró o dejó de responder
     * (Neon suspende la base tras unos minutos sin uso), crea una nueva.
     * Los DAO NO deben cerrar esta conexión.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(SEGUNDOS_VALIDACION)) {
            connection = crearConexion();
        }
        return connection;
    }

    /** Cierra la conexión si está abierta. Se llama al salir de la aplicación. */
    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignorada) {
                // Al salir no hay nada más que hacer con este error.
            } finally {
                connection = null;
            }
        }
    }

    private static Connection crearConexion() throws SQLException {
        Properties properties = cargarPropiedades();
        String url = properties.getProperty("URL", "").trim();
        String usuario = properties.getProperty("USERNAME", "").trim();
        String clave = properties.getProperty("PASSWORD", "").trim();

        if (url.isEmpty()) {
            throw new SQLException("Falta la propiedad URL en " + ARCHIVO_CONFIG + ".");
        }

        DatosConexion datos = normalizar(url, usuario, clave);
        try {
            return DriverManager.getConnection(datos.url(), datos.usuario(), datos.clave());
        } catch (SQLException e) {
            throw new SQLException("No se pudo conectar a la base de datos: " + e.getMessage(), e.getSQLState(), e);
        }
    }

    private static Properties cargarPropiedades() throws SQLException {
        Path ruta = Path.of(System.getProperty("config", ARCHIVO_CONFIG)).toAbsolutePath();
        Properties properties = new Properties();
        try (InputStream entrada = Files.newInputStream(ruta)) {
            properties.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se encontró el archivo de configuración en " + ruta
                    + ". Ejecuta la aplicación desde la carpeta del proyecto.", e);
        }
        return properties;
    }

    /**
     * Convierte la URL a formato JDBC. Si viene como la entrega Neon
     * (postgresql://usuario:clave@host/base?...), extrae el usuario y la clave
     * cuando no se escribieron aparte en USERNAME y PASSWORD.
     */
    static DatosConexion normalizar(String url, String usuario, String clave) throws SQLException {
        if (url.chars().anyMatch(Character::isWhitespace)) {
            throw new SQLException("La URL no puede contener espacios.");
        }
        if (url.startsWith("jdbc:")) {
            return new DatosConexion(url, usuario, clave);
        }
        if (!url.startsWith("postgresql://") && !url.startsWith("postgres://")) {
            throw new SQLException("La URL debe empezar por jdbc:postgresql:// o postgresql://");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new SQLException("La URL de conexión no es válida: " + e.getMessage(), e);
        }

        String info = uri.getRawUserInfo();
        if (info != null) {
            int separador = info.indexOf(':');
            String usuarioUrl = decodificar(separador >= 0 ? info.substring(0, separador) : info);
            String claveUrl = separador >= 0 ? decodificar(info.substring(separador + 1)) : "";
            if (usuario.isEmpty()) {
                usuario = usuarioUrl;
            }
            if (clave.isEmpty()) {
                clave = claveUrl;
            }
        }

        String puerto = uri.getPort() > 0 ? ":" + uri.getPort() : "";
        String ruta = uri.getRawPath() == null ? "" : uri.getRawPath();
        String consulta = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
        String jdbc = "jdbc:postgresql://" + uri.getHost() + puerto + ruta + consulta;
        return new DatosConexion(jdbc, usuario, clave);
    }

    private static String decodificar(String texto) {
        return URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }

    record DatosConexion(String url, String usuario, String clave) {
    }
}
