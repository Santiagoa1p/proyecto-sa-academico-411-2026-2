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
 * Gestiona la conexión única a la base de datos PostgreSQL (Neon) usando JDBC.
 *
 * <p><b>Patrón Singleton:</b> toda la aplicación comparte una sola conexión,
 * guardada en el atributo estático {@link #connection}. Abrir una conexión a Neon
 * es lento (viaja por internet y negocia SSL), así que se abre una vez y se reutiliza.</p>
 *
 * <p><b>Configuración:</b> lee {@code URL}, {@code USERNAME} y {@code PASSWORD} desde
 * {@code config.properties}, en la raíz del proyecto. Ese archivo no se sube a Git
 * porque tiene la clave. La URL puede escribirse en formato JDBC
 * ({@code jdbc:postgresql://...}) o pegarse tal cual la entrega Neon
 * ({@code postgresql://usuario:clave@host/base?sslmode=require}).</p>
 *
 * <p>Esta clase no imprime nada: ante cualquier problema lanza {@link SQLException}
 * con un mensaje claro, para que la Vista decida cómo mostrarlo.</p>
 */
public final class ConexionDatabase {

    /** Nombre del archivo de configuración que se busca en la carpeta desde donde se ejecuta. */
    private static final String ARCHIVO_CONFIG = "config.properties";
    /** Segundos que se espera a que la base responda al verificar si la conexión sigue viva. */
    private static final int SEGUNDOS_VALIDACION = 5;

    /** La única conexión de la aplicación ({@code null} hasta que alguien la pide por primera vez). */
    private static Connection connection;

    /** Constructor privado: impide crear objetos de esta clase; todo se usa de forma estática. */
    private ConexionDatabase() {
    }

    /**
     * Devuelve la conexión abierta, creándola si hace falta.
     *
     * <p>Crea una conexión nueva si todavía no existe, si se cerró o si dejó de
     * responder. Esto último pasa con Neon, que suspende la base tras unos minutos
     * sin uso y corta las conexiones abiertas. {@code isValid} hace una consulta
     * mínima para comprobarlo.</p>
     *
     * <p>Es {@code synchronized} para que dos hilos no creen dos conexiones a la vez.
     * Los DAO <b>no</b> deben cerrar esta conexión, porque la comparten todos.</p>
     *
     * @return una conexión lista para usar
     * @throws SQLException si falta la configuración o no se puede conectar
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

    /**
     * Lee la configuración y abre una conexión nueva con {@link DriverManager}.
     *
     * <p>{@code DriverManager} es la puerta de entrada de JDBC: según el prefijo de la
     * URL ({@code jdbc:postgresql:}) elige el driver de PostgreSQL, que se agregó como
     * dependencia en el {@code pom.xml}.</p>
     *
     * @return la conexión recién abierta
     * @throws SQLException si falta la URL o la base rechaza la conexión
     */
    private static Connection crearConexion() throws SQLException {
        Properties properties = cargarPropiedades();
        // trim() quita espacios accidentales al inicio o al final de cada valor.
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
            // Se relanza con un mensaje en español, conservando el error original como causa.
            throw new SQLException("No se pudo conectar a la base de datos: " + e.getMessage(), e.getSQLState(), e);
        }
    }

    /**
     * Carga {@code config.properties} en un objeto {@link Properties} (pares clave=valor).
     *
     * <p>Por defecto busca el archivo en la carpeta desde donde se ejecuta el programa.
     * Se puede indicar otra ruta con {@code -Dconfig=ruta/archivo.properties} al ejecutar Java.</p>
     *
     * @return las propiedades leídas
     * @throws SQLException si el archivo no existe o no se puede leer
     */
    private static Properties cargarPropiedades() throws SQLException {
        Path ruta = Path.of(System.getProperty("config", ARCHIVO_CONFIG)).toAbsolutePath();
        Properties properties = new Properties();
        // try-with-resources: el archivo se cierra solo al terminar, aunque haya error.
        try (InputStream entrada = Files.newInputStream(ruta)) {
            properties.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se encontró el archivo de configuración en " + ruta
                    + ". Ejecuta la aplicación desde la carpeta del proyecto.", e);
        }
        return properties;
    }

    /**
     * Convierte la URL a formato JDBC, que es el único que entiende {@link DriverManager}.
     *
     * <p>Si la URL viene como la entrega Neon ({@code postgresql://usuario:clave@host/base?...}),
     * extrae el usuario y la clave de ella cuando no se escribieron aparte en
     * {@code USERNAME} y {@code PASSWORD}. Tiene visibilidad de paquete (sin {@code private})
     * para poder probarla en {@code ConexionDatabaseTest}.</p>
     *
     * @param url     la URL tal como está en config.properties
     * @param usuario el valor de USERNAME (puede estar vacío)
     * @param clave   el valor de PASSWORD (puede estar vacío)
     * @return la URL en formato JDBC junto con el usuario y la clave definitivos
     * @throws SQLException si la URL tiene espacios o un formato no reconocido
     */
    static DatosConexion normalizar(String url, String usuario, String clave) throws SQLException {
        if (url.chars().anyMatch(Character::isWhitespace)) {
            throw new SQLException("La URL no puede contener espacios.");
        }
        // Ya está en formato JDBC: se usa tal cual.
        if (url.startsWith("jdbc:")) {
            return new DatosConexion(url, usuario, clave);
        }
        if (!url.startsWith("postgresql://") && !url.startsWith("postgres://")) {
            throw new SQLException("La URL debe empezar por jdbc:postgresql:// o postgresql://");
        }

        // URI descompone la URL en partes: usuario:clave, host, puerto, ruta (/base) y consulta (?...).
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new SQLException("La URL de conexión no es válida: " + e.getMessage(), e);
        }

        // "usuario:clave" (la parte antes de la @). Viene codificada (%40 = @, etc.) y se decodifica.
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

        // Se rearma la URL sin usuario ni clave y con el prefijo jdbc:.
        String puerto = uri.getPort() > 0 ? ":" + uri.getPort() : "";
        String ruta = uri.getRawPath() == null ? "" : uri.getRawPath();
        String consulta = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
        String jdbc = "jdbc:postgresql://" + uri.getHost() + puerto + ruta + consulta;
        return new DatosConexion(jdbc, usuario, clave);
    }

    /** Decodifica caracteres especiales escritos en formato URL, por ejemplo {@code %40} → {@code @}. */
    private static String decodificar(String texto) {
        return URLDecoder.decode(texto, StandardCharsets.UTF_8);
    }

    /**
     * Agrupa los tres datos necesarios para conectarse.
     *
     * <p>Es un {@code record}: una clase compacta de solo datos en la que Java genera
     * automáticamente el constructor y los métodos {@code url()}, {@code usuario()}
     * y {@code clave()}.</p>
     */
    record DatosConexion(String url, String usuario, String clave) {
    }
}
