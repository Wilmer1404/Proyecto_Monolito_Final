package com.utplist.proyecto.exception;

/**
 * Excepción personalizada para indicar que un usuario no tiene permisos suficientes para realizar una acción.
 */
public class PermisoDenegadoException extends RuntimeException {
    public PermisoDenegadoException(String msg) {
        super("Permiso denegado: " + msg);
    }
} 