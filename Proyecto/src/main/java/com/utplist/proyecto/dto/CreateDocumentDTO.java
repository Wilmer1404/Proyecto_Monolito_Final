package com.utplist.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para la creación de un nuevo documento.
 * Utilizado para recibir los datos necesarios desde el cliente.
 */
@Schema(description = "DTO para crear un documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDocumentDTO {
    /** Título del documento */
    @Schema(description = "Título del documento", example = "Mi documento", required = true)
    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 255, message = "El título no puede tener más de 255 caracteres.")
    private String titulo;

    /** Indica si el documento será público */
    @Schema(description = "Indica si el documento será público", example = "true")
    private Boolean publico;
} 