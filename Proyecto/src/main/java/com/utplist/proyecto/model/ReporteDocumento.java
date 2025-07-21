package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_documento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id", nullable = false)
    private Document documento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_reportante_id", nullable = false)
    private User usuarioReportante;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fechaReporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado;

    @Column(length = 500)
    private String comentarioModerador;

    public enum EstadoReporte {
        PENDIENTE,
        ACEPTADO,
        RECHAZADO
    }
} 