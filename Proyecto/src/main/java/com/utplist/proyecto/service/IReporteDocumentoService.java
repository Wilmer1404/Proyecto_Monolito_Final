package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.CrearReporteDocumentoDTO;
import com.utplist.proyecto.dto.ReporteDocumentoDTO;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de reportes sobre documentos.
 * Define operaciones para crear, listar, aceptar y rechazar reportes.
 */
public interface IReporteDocumentoService {
    /**
     * Crea un nuevo reporte sobre un documento.
     * @param dto Datos del reporte
     * @return Reporte creado
     */
    ReporteDocumentoDTO crearReporte(CrearReporteDocumentoDTO dto);
    /**
     * Lista los reportes pendientes de revisión.
     * @return Lista de reportes pendientes
     */
    List<ReporteDocumentoDTO> listarReportesPendientes();
    /**
     * Busca reportes por documento.
     * @param documentoId ID del documento
     * @return Lista de reportes encontrados
     */
    List<ReporteDocumentoDTO> buscarReportesPorDocumento(Long documentoId);
    /**
     * Acepta un reporte (solo para moderadores).
     * @param reporteId ID del reporte
     * @param moderadorId ID del moderador
     * @param comentarioModerador Comentario opcional del moderador
     */
    void aceptarReporte(Long reporteId, Long moderadorId, String comentarioModerador);
    /**
     * Rechaza un reporte (solo para moderadores).
     * @param reporteId ID del reporte
     * @param moderadorId ID del moderador
     * @param comentarioModerador Comentario opcional del moderador
     */
    void rechazarReporte(Long reporteId, Long moderadorId, String comentarioModerador);
} 