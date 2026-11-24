package com.uniajc.dao;

/**
 * Excepción propia que lanzan los DAO cuando falla el acceso a la base de datos.
 *
 * <p>JDBC lanza {@link java.sql.SQLException}, que es una excepción <i>comprobada</i>:
 * Java obliga a capturarla o declararla con {@code throws} en cada método que la
 * atraviese. Si se dejara así, el servicio, el controlador y la vista tendrían que
 * declarar {@code throws SQLException}, y todas las capas quedarían "contaminadas"
 * con un detalle técnico de la base de datos.</p>
 *
 * <p>Por eso los DAO la envuelven en esta clase, que hereda de {@link RuntimeException}
 * (excepción <i>no comprobada</i>): sube sola hasta la vista, que la muestra al
 * usuario. La excepción original se conserva como causa para poder depurar.</p>
 */
public class ErrorBaseDatosException extends RuntimeException {

    /**
     * Crea el error combinando la acción que falló con la causa real.
     *
     * @param accion qué se intentaba hacer, por ejemplo "No se pudo guardar el estudiante"
     * @param causa  la excepción original (normalmente una SQLException)
     */
    public ErrorBaseDatosException(String accion, Throwable causa) {
        super(accion + ": " + (causa.getMessage() == null ? causa.toString() : causa.getMessage()), causa);
    }
}
