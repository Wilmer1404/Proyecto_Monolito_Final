package com.utplist.proyecto.dto;

import lombok.*;

/**
 * DTO que representa la suscripción de un usuario a un documento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuscripcionDTO {
    /** ID del documento suscrito */
    private Long documentoId;
    /** Título del documento */
    private String titulo;
    /** Categoría del documento */
    private String categoria;
    /** Indica si el documento es público */
    private Boolean publico;
} 