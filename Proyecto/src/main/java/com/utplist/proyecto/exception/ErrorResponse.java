package com.utplist.proyecto.exception;

import lombok.*;
import java.time.Instant;

/**
 * Clase que representa la estructura de una respuesta de error estándar para la API.
 * Incluye información sobre el estado, mensaje y momento del error.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ErrorResponse {
    /** Código de estado HTTP del error */
    private int status;
    /** Descripción corta del error */
    private String error;
    /** Mensaje detallado del error */
    private String message;
    /** Momento en que ocurrió el error */
    private Instant timestamp;
} 