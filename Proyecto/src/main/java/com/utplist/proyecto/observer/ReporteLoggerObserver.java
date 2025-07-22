package com.utplist.proyecto.observer;

import com.utplist.proyecto.model.ReporteDocumento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

/**
 * Observer que registra en logs los eventos relacionados a reportes de documentos.
 * Permite auditar la creación, aceptación, rechazo y eliminación de documentos por reportes.
 */
@Slf4j
@Component
public class ReporteLoggerObserver implements IReporteObserver {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Se ejecuta cuando se crea un reporte sobre un documento.
     * @param reporte Reporte creado
     */
    @Override
    public void onReporteCreado(ReporteDocumento reporte) {
        log.info("[REPORTE] [{}] Usuario {} reportó el documento '{}' (ID: {}) por motivo: '{}'. Estado: {}", 
            reporte.getFechaReporte().format(FORMATTER),
            reporte.getUsuarioReportante().getEmail(),
            reporte.getDocumento().getTitulo(),
            reporte.getDocumento().getId(),
            reporte.getMotivo(),
            reporte.getEstado()
        );
    }

    /**
     * Se ejecuta cuando un reporte es aceptado por un moderador.
     * @param reporte Reporte aceptado
     * @param moderadorId ID del moderador
     */
    @Override
    public void onReporteAceptado(ReporteDocumento reporte, Long moderadorId) {
        log.info("[REPORTE] [{}] Moderador (ID: {}) aceptó el reporte ID {} sobre documento '{}' (ID: {}). Comentario: {}", 
            reporte.getFechaReporte().format(FORMATTER),
            moderadorId,
            reporte.getId(),
            reporte.getDocumento().getTitulo(),
            reporte.getDocumento().getId(),
            reporte.getComentarioModerador()
        );
    }

    /**
     * Se ejecuta cuando un reporte es rechazado por un moderador.
     * @param reporte Reporte rechazado
     * @param moderadorId ID del moderador
     */
    @Override
    public void onReporteRechazado(ReporteDocumento reporte, Long moderadorId) {
        log.info("[REPORTE] [{}] Moderador (ID: {}) rechazó el reporte ID {} sobre documento '{}' (ID: {}). Comentario: {}", 
            reporte.getFechaReporte().format(FORMATTER),
            moderadorId,
            reporte.getId(),
            reporte.getDocumento().getTitulo(),
            reporte.getDocumento().getId(),
            reporte.getComentarioModerador()
        );
    }

    /**
     * Se ejecuta cuando un documento es eliminado por un reporte aceptado.
     * @param reporte Reporte relacionado
     * @param moderadorId ID del moderador
     */
    @Override
    public void onDocumentoEliminadoPorReporte(ReporteDocumento reporte, Long moderadorId) {
        log.info("[REPORTE] [{}] Documento '{}' (ID: {}) eliminado por moderador (ID: {}) tras reporte ID {}.", 
            reporte.getFechaReporte().format(FORMATTER),
            reporte.getDocumento().getTitulo(),
            reporte.getDocumento().getId(),
            moderadorId,
            reporte.getId()
        );
    }
} 