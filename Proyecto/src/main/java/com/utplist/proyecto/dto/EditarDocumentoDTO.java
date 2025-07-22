package com.utplist.proyecto.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarDocumentoDTO {
    @Size(max = 255, message = "El título no puede tener más de 255 caracteres.")
    private String titulo;
    private String contenido;
    @Size(max = 100, message = "La categoría no puede tener más de 100 caracteres.")
    private String categoria;
} 