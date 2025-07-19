package com.utplist.proyecto.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuscripcionDTO {
    private Long documentoId;
    private String titulo;
    private String categoria;
    private Boolean publico;
} 