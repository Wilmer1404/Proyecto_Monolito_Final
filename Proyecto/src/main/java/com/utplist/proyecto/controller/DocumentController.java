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
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestPart;

@Tag(name = "Documentos", description = "Operaciones sobre documentos colaborativos")
@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentController {
    private final IDocumentService documentService;
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @PreAuthorize("hasAnyRole('USUARIO', 'ADMINISTRADOR', 'SUPERADMINISTRADOR')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentResponseDTO> crear(
            @RequestPart("titulo") String titulo,
            @RequestPart("publico") String publicoStr,
            @RequestPart(value = "archivo", required = false) org.springframework.web.multipart.MultipartFile archivo,
            HttpServletRequest request
    ) {
        Boolean publico = Boolean.parseBoolean(publicoStr);
        logger.info("Intentando crear documento: titulo={}, publico={}", titulo, publico);
        String correo = request.getHeader("X-Autor-Correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        DocumentResponseDTO doc = documentService.crearDocumentoConArchivo(titulo, publico, archivo, correo);
        return ResponseEntity.ok(doc);
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

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> porId(@PathVariable Long id, Authentication auth) {
        // Lógica: solo autor, compartido, reportado (si es moderador/admin), o superadmin
        // Si no, lanzar PermisoDenegadoException
        return ResponseEntity.ok(documentService.obtenerPorIdConPermiso(id, auth));
    }

    @PreAuthorize("hasAnyRole('SUPERADMINISTRADOR', 'USUARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
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

    @PreAuthorize("hasAnyRole('SUPERADMINISTRADOR', 'USUARIO')")
    @PostMapping("/{id}/invitar")
    public ResponseEntity<Void> invitar(
            @PathVariable Long id,
            @RequestParam String correoInvitado,
            @RequestParam RollInvitado rol
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
    public ResponseEntity<Page<InvitacionDTO>> invitacionesPorUsuario(
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correo,
            @Parameter(hidden = true)
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentService.obtenerInvitacionesPorUsuario(correo, pageable));
    }

    @PreAuthorize("hasAnyRole('SUPERADMINISTRADOR', 'USUARIO')")
    @PatchMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> editarDocumento(
            @PathVariable Long id,
            @RequestPart(value = "titulo", required = false) String titulo,
            @RequestPart(value = "publico", required = false) String publicoStr,
            @RequestPart(value = "archivo", required = false) org.springframework.web.multipart.MultipartFile archivo,
            HttpServletRequest request,
            Authentication auth
    ) {
        String correo = request.getHeader("X-Autor-Correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().body("Falta el header X-Autor-Correo");
        }
        try {
            Boolean publico = publicoStr != null ? Boolean.parseBoolean(publicoStr) : null;
            logger.info("Entrando a editarDocumentoConArchivo: id={}, titulo={}, publico={}, correo={}, archivo={}", id, titulo, publico, correo, archivo != null ? archivo.getOriginalFilename() : "null");
            DocumentResponseDTO doc = documentService.editarDocumentoConArchivo(id, titulo, publico, archivo, correo, auth);
            return ResponseEntity.ok(doc);
        } catch (Exception e) {
            logger.error("Error editando documento (PATCH /documentos/{}): {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error interno del servidor: " + e.getMessage());
        }
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
    public ResponseEntity<Page<SuscripcionDTO>> listarSuscripciones(
            @Parameter(description = "Correo del usuario", required = true) @RequestParam String correo,
            @Parameter(hidden = true)
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentService.documentosSuscritos(correo, pageable));
    }

    @PreAuthorize("hasRole('MODERADOR')")
    @GetMapping("/reportados")
    public List<DocumentResponseDTO> documentosReportados() {
        return documentService.obtenerDocumentosReportados();
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable Long id) throws IOException {
        DocumentResponseDTO doc = documentService.obtenerPorId(id);
        if (doc.getRutaArchivo() == null) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(doc.getRutaArchivo());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "application/octet-stream";
        if (doc.getRutaArchivo().endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (doc.getRutaArchivo().endsWith(".doc") || doc.getRutaArchivo().endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                .body(resource);
    }
} 