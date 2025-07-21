package com.utplist.proyecto.controller;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.model.RollInvitado;
import com.utplist.proyecto.service.IDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Documentos", description = "Operaciones sobre documentos colaborativos")
@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentController {
    private final IDocumentService documentService;

    @Operation(summary = "Crear un nuevo documento", description = "Crea un documento colaborativo y lo asocia al autor indicado en el header X-Autor-Correo.")
    @PostMapping
    public ResponseEntity<DocumentResponseDTO> crear(
            @Valid @RequestBody CreateDocumentDTO dto,
            @Parameter(description = "Correo del autor (header X-Autor-Correo)", required = true)
            HttpServletRequest request
    ) {
        String correo = request.getHeader("X-Autor-Correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        DocumentResponseDTO creado = documentService.crearDocumento(dto, correo);
        return ResponseEntity.ok(creado);
    }

    @Operation(summary = "Listar documentos del autor")
    @GetMapping
    public ResponseEntity<Page<DocumentResponseDTO>> porAutor(
            @Parameter(description = "Correo del autor", required = true) @RequestParam String correo,
            @Parameter(hidden = true)
            @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                documentService.obtenerDocumentosPorAutor(correo, pageable)
        );
    }

    @Operation(summary = "Búsqueda avanzada de documentos")
    @GetMapping("/buscar")
    public ResponseEntity<Page<DocumentResponseDTO>> buscar(
            @Parameter(description = "Filtro por título") @RequestParam(required = false) String titulo,
            @Parameter(description = "Filtro por categoría") @RequestParam(required = false) String categoria,
            @Parameter(description = "Filtro por autor") @RequestParam(required = false) String autor,
            @Parameter(hidden = true)
            @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                documentService.buscar(titulo, categoria, autor, pageable)
        );
    }

    @Operation(summary = "Obtener documento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> porId(@Parameter(description = "ID del documento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(documentService.obtenerPorId(id));
    }

    @Operation(summary = "Eliminar documento")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "ID del usuario (header X-User-Id)", required = true)
            @RequestHeader("X-User-Id") Long userId
    ) {
        documentService.eliminarDocumento(userId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar documentos compartidos")
    @GetMapping("/compartidos")
    public ResponseEntity<Page<DocumentResponseDTO>> compartidos(
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correoInvitado,
            @Parameter(hidden = true)
            @PageableDefault(sort = "fechaCreacion", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
                documentService.obtenerDocumentosCompartidos(correoInvitado, pageable)
        );
    }

    @Operation(summary = "Invitar usuario a documento")
    @PostMapping("/{id}/invitar")
    public ResponseEntity<Void> invitar(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correoInvitado,
            @Parameter(description = "Rol del invitado (EDITOR o VISUALIZADOR)", required = true) @RequestParam RollInvitado rol
    ) {
        documentService.invitarUsuario(id, correoInvitado, rol);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Aceptar invitación")
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptar(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correoInvitado
    ) {
        documentService.aceptarInvitacion(id, correoInvitado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar invitaciones de un documento")
    @GetMapping("/{id}/invitaciones")
    public ResponseEntity<List<InvitacionDTO>> verInvitaciones(@Parameter(description = "ID del documento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(documentService.listarInvitaciones(id));
    }

    @Operation(summary = "Listar invitaciones aceptadas de un usuario")
    @GetMapping("/invitaciones")
    public ResponseEntity<List<InvitacionDTO>> invitacionesPorUsuario(
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.obtenerInvitacionesPorUsuario(correo));
    }

    @Operation(summary = "Editar documento")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> editar(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Valid @RequestBody EditarDocumentoDTO dto,
            @Parameter(description = "ID del usuario (header X-User-Id)", required = true)
            @RequestHeader("X-User-Id") Long userId
    ) {
        documentService.editarDocumento(userId, id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Crear solicitud de edición")
    @PostMapping("/solicitudes")
    public ResponseEntity<Void> crearSolicitud(
            @Valid @RequestBody CreateSolicitudDTO dto,
            @Parameter(description = "Correo del usuario solicitante (header X-Correo-Invitado)", required = true)
            @RequestHeader("X-Correo-Invitado") String correo
    ) {
        documentService.solicitarEdicion(correo, dto.getDocumentoId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Responder solicitud de edición")
    @PatchMapping("/solicitudes/{id}")
    public ResponseEntity<Void> responderSolicitud(
            @Parameter(description = "ID de la solicitud", required = true) @PathVariable Long id,
            @Parameter(description = "Aceptar o rechazar la solicitud", required = true) @RequestParam boolean aceptar,
            @Parameter(description = "Correo del autor (header X-Autor-Correo)", required = true)
            @RequestHeader("X-Autor-Correo") String autorCorreo
    ) {
        documentService.responderSolicitudEdicion(id, aceptar, autorCorreo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar solicitudes de edición recibidas")
    @GetMapping("/solicitudes/recibidas")
    public ResponseEntity<List<SolicitudEdicionDTO>> solicitudesRecibidas(
            @Parameter(description = "Correo del autor", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.solicitudesPorAutor(correo));
    }

    @Operation(summary = "Listar solicitudes de edición enviadas")
    @GetMapping("/solicitudes/enviadas")
    public ResponseEntity<List<SolicitudEdicionDTO>> solicitudesEnviadas(
            @Parameter(description = "Correo del usuario", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.solicitudesPorUsuario(correo));
    }

    @Operation(summary = "Suscribirse a documento público")
    @PostMapping("/{id}/suscribirse")
    public ResponseEntity<Void> suscribirse(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "Correo del usuario (header X-Correo-Invitado)", required = true)
            @RequestHeader("X-Correo-Invitado") String correoUsuario
    ) {
        documentService.suscribirseDocumentoPublico(correoUsuario, id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar suscripción a documento")
    @DeleteMapping("/{id}/suscribirse")
    public ResponseEntity<Void> cancelarSuscripcion(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "Correo del usuario (header X-Correo-Invitado)", required = true)
            @RequestHeader("X-Correo-Invitado") String correoUsuario
    ) {
        documentService.cancelarSuscripcion(correoUsuario, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar documentos suscritos")
    @GetMapping("/suscripciones")
    public ResponseEntity<List<SuscripcionDTO>> listarSuscripciones(
            @Parameter(description = "Correo del usuario", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.documentosSuscritos(correo));
    }
} 