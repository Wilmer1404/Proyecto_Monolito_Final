package com.utplist.proyecto.exception;

/**
 * Excepción personalizada para indicar que ya existe una invitación para el usuario y documento especificados.
 */
public class InvitacionDuplicadaException extends RuntimeException {
    public InvitacionDuplicadaException(String correo, Long docId) {
        super("Invitación duplicada para '" + correo + "' en documento " + docId);
    }
} 