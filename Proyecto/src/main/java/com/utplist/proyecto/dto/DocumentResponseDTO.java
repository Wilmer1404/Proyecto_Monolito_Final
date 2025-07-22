package com.utplist.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa los datos de un documento para el cliente.
 */
@Schema(description = "DTO de respuesta para un documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {
    /** ID único del documento */
    @Schema(description = "ID del documento", example = "1")
    private Long id;
    /** Título del documento */
    @Schema(description = "Título del documento", example = "Mi documento")
    private String titulo;
    /** Correo del autor del documento */
    @Schema(description = "Correo del autor", example = "autor@utpl.edu.ec")
    private String autorCorreo;
    /** Fecha de creación del documento */
    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;
    /** Fecha de última actualización del documento */
    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;
    /** Indica si el documento es público */
    @Schema(description = "¿Es público?", example = "true")
    private Boolean publico;
} 