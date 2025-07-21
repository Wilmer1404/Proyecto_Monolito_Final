package com.utplist.proyecto.controller;

import com.utplist.proyecto.dto.CrearReporteDocumentoDTO;
import com.utplist.proyecto.dto.ReporteDocumentoDTO;
import com.utplist.proyecto.service.IReporteDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Reportes de Documentos", description = "Gestión de reportes de documentos")
@RestController
@RequestMapping("/reportes-documento")
@RequiredArgsConstructor
public class ReporteDocumentoController {
    private final IReporteDocumentoService reporteService;

    @Operation(summary = "Reportar un documento")
    @PostMapping
    public ResponseEntity<ReporteDocumentoDTO> reportar(@RequestBody CrearReporteDocumentoDTO dto) {
        return ResponseEntity.ok(reporteService.crearReporte(dto));
    }

    @Operation(summary = "Listar reportes pendientes (solo moderador)")
    @GetMapping("/pendientes")
    public ResponseEntity<List<ReporteDocumentoDTO>> listarPendientes(
            @Parameter(description = "ID del moderador (header X-User-Id)", required = true)
            @RequestHeader("X-User-Id") Long moderadorId
    ) {
        // La validación de rol se hace en el servicio
        return ResponseEntity.ok(reporteService.listarReportesPendientes());
    }

    @Operation(summary = "Buscar reportes por documento")
    @GetMapping("/por-documento/{documentoId}")
    public ResponseEntity<List<ReporteDocumentoDTO>> buscarPorDocumento(@PathVariable Long documentoId) {
        return ResponseEntity.ok(reporteService.buscarReportesPorDocumento(documentoId));
    }

    @Operation(summary = "Aceptar reporte (solo moderador)")
    @PostMapping("/{reporteId}/aceptar")
    public ResponseEntity<Void> aceptarReporte(
            @PathVariable Long reporteId,
            @Parameter(description = "ID del moderador (header X-User-Id)", required = true)
            @RequestHeader("X-User-Id") Long moderadorId,
            @RequestParam(required = false) String comentario
    ) {
        reporteService.aceptarReporte(reporteId, moderadorId, comentario);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Rechazar reporte (solo moderador)")
    @PostMapping("/{reporteId}/rechazar")
    public ResponseEntity<Void> rechazarReporte(
            @PathVariable Long reporteId,
            @Parameter(description = "ID del moderador (header X-User-Id)", required = true)
            @RequestHeader("X-User-Id") Long moderadorId,
            @RequestParam(required = false) String comentario
    ) {
        reporteService.rechazarReporte(reporteId, moderadorId, comentario);
        return ResponseEntity.ok().build();
    }
} 