package com.utplist.proyecto.exception;

import lombok.*;
import java.time.Instant;
import java.util.List;

/**
 * Clase que representa la respuesta de error para validaciones fallidas.
 * Incluye detalles de los campos inválidos y el momento del error.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidationErrorResponse {
    /** Código de estado HTTP del error */
    private int status;
    /** Descripción corta del error */
    private String error;
    /** Momento en que ocurrió el error */
    private Instant timestamp;
    /** Lista de errores de validación por campo */
    private List<ValidationError> errors;
} 