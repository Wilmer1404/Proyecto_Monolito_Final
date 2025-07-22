package com.utplist.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "DTO de respuesta para un documento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {
    @Schema(description = "ID del documento", example = "1")
    private Long id;
    @Schema(description = "Título del documento", example = "Mi documento")
    private String titulo;
    @Schema(description = "Correo del autor", example = "autor@utpl.edu.ec")
    private String autorCorreo;
    @Schema(description = "Fecha de creación")
    private LocalDateTime fechaCreacion;
    @Schema(description = "Fecha de actualización")
    private LocalDateTime fechaActualizacion;
    @Schema(description = "¿Es público?", example = "true")
    private Boolean publico;
    private String rutaArchivo;
    // Rol del usuario invitado sobre el documento (EDITOR, VISUALIZADOR, null si es autor)
    private String rolInvitacion;
} 