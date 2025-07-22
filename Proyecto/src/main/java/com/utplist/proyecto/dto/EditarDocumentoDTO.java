package com.utplist.proyecto.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para editar los datos de un documento existente.
 * Permite modificar el título, contenido y categoría del documento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarDocumentoDTO {
    /** Nuevo título del documento (opcional) */
    @Size(max = 255, message = "El título no puede tener más de 255 caracteres.")
    private String titulo;
    /** Nuevo contenido del documento (opcional) */
    private String contenido;
    /** Nueva categoría del documento (opcional) */
    @Size(max = 100, message = "La categoría no puede tener más de 100 caracteres.")
    private String categoria;
} 