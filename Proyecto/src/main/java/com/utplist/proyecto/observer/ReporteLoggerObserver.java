package com.utplist.proyecto.observer;

import com.utplist.proyecto.model.ReporteDocumento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ReporteLoggerObserver implements IReporteObserver {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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