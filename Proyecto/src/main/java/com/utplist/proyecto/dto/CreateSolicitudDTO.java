package com.utplist.proyecto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para crear una nueva solicitud de edición sobre un documento.
 * Contiene el ID del documento al que se solicita la edición.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSolicitudDTO {
    /** ID del documento al que se solicita la edición */
    @NotNull(message = "El id del documento no puede ser nulo.")
    private Long documentoId;
} 