package com.utplist.proyecto.dto;

import lombok.*;

/**
 * DTO para crear un nuevo reporte sobre un documento.
 * Contiene los datos necesarios para registrar el reporte.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CrearReporteDocumentoDTO {
    /** ID del documento a reportar */
    private Long documentoId;
    /** ID del usuario que realiza el reporte */
    private Long usuarioReportanteId;
    /** Motivo del reporte */
    private String motivo;
} 