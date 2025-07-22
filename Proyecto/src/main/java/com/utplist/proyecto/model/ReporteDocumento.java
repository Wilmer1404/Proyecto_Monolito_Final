package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un reporte realizado sobre un documento.
 * Incluye información sobre el documento, usuario reportante, motivo, fecha, estado y comentarios del moderador.
 */
@Entity
@Table(name = "reportes_documento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteDocumento {
    /** Identificador único del reporte */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Documento reportado */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id", nullable = false)
    private Document documento;

    /** Usuario que realizó el reporte */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_reportante_id", nullable = false)
    private User usuarioReportante;

    /** Motivo del reporte */
    @Column(nullable = false, length = 500)
    private String motivo;

    /** Fecha en que se realizó el reporte */
    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    /** Estado actual del reporte */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado;

    /** Comentario del moderador sobre el reporte */
    @Column(length = 500)
    private String comentarioModerador;

    /**
     * Enumeración de los posibles estados de un reporte.
     */
    public enum EstadoReporte {
        PENDIENTE,
        ACEPTADO,
        RECHAZADO
    }
} 