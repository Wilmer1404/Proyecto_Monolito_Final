package com.utplist.proyecto.exception;

/**
 * Excepción personalizada para indicar que un feature flag solicitado no fue encontrado.
 */
public class FeatureFlagNotFoundException extends RuntimeException {
    public FeatureFlagNotFoundException(String nombre) {
        super("Feature flag no encontrada: " + nombre);
    }
} 