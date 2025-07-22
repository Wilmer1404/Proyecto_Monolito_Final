package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.dto.CrearReporteDocumentoDTO;
import com.utplist.proyecto.dto.ReporteDocumentoDTO;
import com.utplist.proyecto.exception.PermisoDenegadoException;
import com.utplist.proyecto.model.*;
import com.utplist.proyecto.repository.*;
import com.utplist.proyecto.service.IReporteDocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.utplist.proyecto.observer.IReporteObserver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementación del servicio para la gestión de reportes sobre documentos.
 * Permite crear, listar, aceptar y rechazar reportes, y notificar a los observadores.
 */
@Service
@RequiredArgsConstructor
public class ReporteDocumentoServiceImpl implements IReporteDocumentoService {
    private final ReporteDocumentoRepository reporteRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    @Autowired(required = false)
    private List<IReporteObserver> observers = new java.util.ArrayList<>();

    /**
     * Crea un nuevo reporte sobre un documento.
     * @param dto Datos del reporte
     * @return Reporte creado
     */
    @Override
    public ReporteDocumentoDTO crearReporte(CrearReporteDocumentoDTO dto) {
        Document doc = documentRepository.findById(dto.getDocumentoId())
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        User usuario = userRepository.findById(dto.getUsuarioReportanteId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        ReporteDocumento reporte = ReporteDocumento.builder()
                .documento(doc)
                .usuarioReportante(usuario)
                .motivo(dto.getMotivo())
                .fechaReporte(LocalDateTime.now())
                .estado(ReporteDocumento.EstadoReporte.PENDIENTE)
                .build();
        reporteRepository.save(reporte);
        observers.forEach(obs -> obs.onReporteCreado(reporte));
        return toDTO(reporte);
    }

    /**
     * Lista los reportes pendientes de revisión.
     * @return Lista de reportes pendientes
     */
    @Override
    public List<ReporteDocumentoDTO> listarReportesPendientes() {
        return reporteRepository.findByEstado(ReporteDocumento.EstadoReporte.PENDIENTE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Busca reportes por documento.
     * @param documentoId ID del documento
     * @return Lista de reportes encontrados
     */
    @Override
    public List<ReporteDocumentoDTO> buscarReportesPorDocumento(Long documentoId) {
        return reporteRepository.findByDocumentoId(documentoId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Acepta un reporte (solo para moderadores).
     * @param reporteId ID del reporte
     * @param moderadorId ID del moderador
     * @param comentarioModerador Comentario opcional del moderador
     */
    @Override
    public void aceptarReporte(Long reporteId, Long moderadorId, String comentarioModerador) {
        ReporteDocumento reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        User moderador = userRepository.findById(moderadorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));
        if (moderador.getRole() != Role.MODERADOR) {
            throw new PermisoDenegadoException("Solo un moderador puede aceptar reportes");
        }
        reporte.setEstado(ReporteDocumento.EstadoReporte.ACEPTADO);
        reporte.setComentarioModerador(comentarioModerador);
        reporteRepository.save(reporte);
        observers.forEach(obs -> obs.onReporteAceptado(reporte, moderadorId));
        // Eliminar el documento
        documentRepository.deleteById(reporte.getDocumento().getId());
        observers.forEach(obs -> obs.onDocumentoEliminadoPorReporte(reporte, moderadorId));
    }

    /**
     * Rechaza un reporte (solo para moderadores).
     * @param reporteId ID del reporte
     * @param moderadorId ID del moderador
     * @param comentarioModerador Comentario opcional del moderador
     */
    @Override
    public void rechazarReporte(Long reporteId, Long moderadorId, String comentarioModerador) {
        ReporteDocumento reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        User moderador = userRepository.findById(moderadorId)
                .orElseThrow(() -> new RuntimeException("Moderador no encontrado"));
        if (moderador.getRole() != Role.MODERADOR) {
            throw new PermisoDenegadoException("Solo un moderador puede rechazar reportes");
        }
        reporte.setEstado(ReporteDocumento.EstadoReporte.RECHAZADO);
        reporte.setComentarioModerador(comentarioModerador);
        reporteRepository.save(reporte);
        observers.forEach(obs -> obs.onReporteRechazado(reporte, moderadorId));
    }

    private ReporteDocumentoDTO toDTO(ReporteDocumento r) {
        return ReporteDocumentoDTO.builder()
                .id(r.getId())
                .documentoId(r.getDocumento().getId())
                .documentoTitulo(r.getDocumento().getTitulo())
                .usuarioReportanteId(r.getUsuarioReportante().getId())
                .usuarioReportanteCorreo(r.getUsuarioReportante().getEmail())
                .motivo(r.getMotivo())
                .fechaReporte(r.getFechaReporte())
                .estado(r.getEstado())
                .comentarioModerador(r.getComentarioModerador())
                .build();
    }
} 