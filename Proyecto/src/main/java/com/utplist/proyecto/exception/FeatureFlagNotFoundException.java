package com.utplist.proyecto.exception;

public class FeatureFlagNotFoundException extends RuntimeException {
    public FeatureFlagNotFoundException(String nombre) {
        super("Feature flag no encontrada: " + nombre);
    }
}