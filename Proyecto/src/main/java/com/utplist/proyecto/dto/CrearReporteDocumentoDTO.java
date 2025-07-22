package com.utplist.proyecto.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CrearReporteDocumentoDTO {
    private Long documentoId;
    private Long usuarioReportanteId;
    private String motivo;
} 