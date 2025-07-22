package com.utplist.proyecto.exception;

public class InvitacionDuplicadaException extends RuntimeException {
    public InvitacionDuplicadaException(String correo, Long docId) {
        super("Invitación duplicada para '" + correo + "' en documento " + docId);
    }
} 