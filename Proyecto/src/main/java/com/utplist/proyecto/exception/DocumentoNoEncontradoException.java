package com.utplist.proyecto.exception;

/**
 * Excepción personalizada para indicar que un documento no fue encontrado en la base de datos.
 */
public class DocumentoNoEncontradoException extends RuntimeException {
    public DocumentoNoEncontradoException(Long id) {
        super("Documento no encontrado con id: " + id);
    }
} 