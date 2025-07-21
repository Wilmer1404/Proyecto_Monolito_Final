package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.CrearReporteDocumentoDTO;
import com.utplist.proyecto.dto.ReporteDocumentoDTO;
import java.util.List;

public interface IReporteDocumentoService {
    ReporteDocumentoDTO crearReporte(CrearReporteDocumentoDTO dto);
    List<ReporteDocumentoDTO> listarReportesPendientes();
    List<ReporteDocumentoDTO> buscarReportesPorDocumento(Long documentoId);
    void aceptarReporte(Long reporteId, Long moderadorId, String comentarioModerador);
    void rechazarReporte(Long reporteId, Long moderadorId, String comentarioModerador);
} 