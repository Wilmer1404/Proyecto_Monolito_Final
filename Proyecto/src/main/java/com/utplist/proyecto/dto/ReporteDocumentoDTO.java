package com.utplist.proyecto.dto;

import com.utplist.proyecto.model.ReporteDocumento;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteDocumentoDTO {
    private Long id;
    private Long documentoId;
    private String documentoTitulo;
    private Long usuarioReportanteId;
    private String usuarioReportanteCorreo;
    private String motivo;
    private LocalDateTime fechaReporte;
    private ReporteDocumento.EstadoReporte estado;
    private String comentarioModerador;
} 