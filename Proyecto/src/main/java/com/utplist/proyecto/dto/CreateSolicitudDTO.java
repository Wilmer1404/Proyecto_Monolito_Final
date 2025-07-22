package com.utplist.proyecto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSolicitudDTO {
    @NotNull(message = "El id del documento no puede ser nulo.")
    private Long documentoId;
} 