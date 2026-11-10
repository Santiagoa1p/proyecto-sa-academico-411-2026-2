package com.uniajc.dao;

/**
 * Error al acceder a la base de datos. Envuelve la SQLException original y
 * deja en el mensaje la causa real para que la Vista pueda mostrarla.
 */
public class ErrorBaseDatosException extends RuntimeException {

    public ErrorBaseDatosException(String accion, Throwable causa) {
        super(accion + ": " + (causa.getMessage() == null ? causa.toString() : causa.getMessage()), causa);
    }
}
