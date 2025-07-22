package com.utplist.proyecto.exception;

import lombok.*;

/**
 * Clase que representa un error de validación en un campo específico.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidationError {
    /** Nombre del campo con error */
    private String field;
    /** Mensaje de error asociado al campo */
    private String message;
} 