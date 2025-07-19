package com.utplist.proyecto.exception;

public class PermisoDenegadoException extends RuntimeException {
    public PermisoDenegadoException(String msg) {
        super("Permiso denegado: " + msg);
    }
} 