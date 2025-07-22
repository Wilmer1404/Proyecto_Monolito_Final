package com.utplist.proyecto.dto;

import com.utplist.proyecto.model.ReporteDocumento;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO que representa la información de un reporte realizado sobre un documento.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteDocumentoDTO {
    /** ID único del reporte */
    private Long id;
    /** ID del documento reportado */
    private Long documentoId;
    /** Título del documento reportado */
    private String documentoTitulo;
    /** ID del usuario que reporta */
    private Long usuarioReportanteId;
    /** Correo del usuario que reporta */
    private String usuarioReportanteCorreo;
    /** Motivo del reporte */
    private String motivo;
    /** Fecha en que se realizó el reporte */
    private LocalDateTime fechaReporte;
    /** Estado actual del reporte */
    private ReporteDocumento.EstadoReporte estado;
    /** Comentario del moderador sobre el reporte */
    private String comentarioModerador;
} 