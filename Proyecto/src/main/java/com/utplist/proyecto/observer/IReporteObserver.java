package com.utplist.proyecto.observer;

import com.utplist.proyecto.model.ReporteDocumento;

public interface IReporteObserver {
    void onReporteCreado(ReporteDocumento reporte);
    void onReporteAceptado(ReporteDocumento reporte, Long moderadorId);
    void onReporteRechazado(ReporteDocumento reporte, Long moderadorId);
    void onDocumentoEliminadoPorReporte(ReporteDocumento reporte, Long moderadorId);
} 