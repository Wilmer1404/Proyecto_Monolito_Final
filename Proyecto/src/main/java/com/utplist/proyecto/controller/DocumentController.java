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

/**
 * Controlador REST para la gestión de documentos colaborativos.
 * Permite crear, listar, buscar, editar, eliminar documentos, gestionar invitaciones, solicitudes de edición y suscripciones.
 */
@Tag(name = "Documentos", description = "Operaciones sobre documentos colaborativos")
@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentController {
    private final IDocumentService documentService;

    /**
     * Crea un nuevo documento colaborativo y lo asocia al autor indicado en el header X-Autor-Correo.
     * @param dto Datos del documento a crear
     * @param request Petición HTTP con el header X-Autor-Correo
     * @return Documento creado
     */
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

    /**
     * Lista los documentos de un autor específico.
     * @param correo Correo del autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos
     */
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

    /**
     * Realiza una búsqueda avanzada de documentos por título, categoría o autor.
     * @param titulo Filtro por título
     * @param categoria Filtro por categoría
     * @param autor Filtro por autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos filtrados
     */
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

    /**
     * Obtiene un documento por su ID.
     * @param id ID del documento
     * @return Documento encontrado
     */
    @Operation(summary = "Obtener documento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> porId(@Parameter(description = "ID del documento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(documentService.obtenerPorId(id));
    }

    /**
     * Elimina un documento por su ID, validando el usuario.
     * @param id ID del documento
     * @param userId ID del usuario que solicita la eliminación
     * @return Respuesta vacía si se elimina correctamente
     */
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

    /**
     * Lista los documentos compartidos con un usuario invitado.
     * @param correoInvitado Correo del invitado
     * @param pageable Parámetros de paginación
     * @return Página de documentos compartidos
     */
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

    /**
     * Invita a un usuario a colaborar en un documento.
     * @param id ID del documento
     * @param correoInvitado Correo del invitado
     * @param rol Rol asignado al invitado (EDITOR o VISUALIZADOR)
     * @return Respuesta vacía si la invitación es exitosa
     */
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

    /**
     * Acepta una invitación a un documento.
     * @param id ID del documento
     * @param correoInvitado Correo del invitado
     * @return Respuesta vacía si se acepta correctamente
     */
    @Operation(summary = "Aceptar invitación")
    @PostMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptar(
            @Parameter(description = "ID del documento", required = true) @PathVariable Long id,
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correoInvitado
    ) {
        documentService.aceptarInvitacion(id, correoInvitado);
        return ResponseEntity.ok().build();
    }

    /**
     * Lista las invitaciones de un documento.
     * @param id ID del documento
     * @return Lista de invitaciones
     */
    @Operation(summary = "Listar invitaciones de un documento")
    @GetMapping("/{id}/invitaciones")
    public ResponseEntity<List<InvitacionDTO>> verInvitaciones(@Parameter(description = "ID del documento", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(documentService.listarInvitaciones(id));
    }

    /**
     * Lista las invitaciones aceptadas de un usuario.
     * @param correo Correo del invitado
     * @return Lista de invitaciones aceptadas
     */
    @Operation(summary = "Listar invitaciones aceptadas de un usuario")
    @GetMapping("/invitaciones")
    public ResponseEntity<List<InvitacionDTO>> invitacionesPorUsuario(
            @Parameter(description = "Correo del invitado", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.obtenerInvitacionesPorUsuario(correo));
    }

    /**
     * Edita un documento existente.
     * @param id ID del documento
     * @param dto Datos a editar
     * @param userId ID del usuario que edita
     * @return Respuesta vacía si se edita correctamente
     */
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

    /**
     * Crea una solicitud de edición para un documento público.
     * @param dto Datos de la solicitud
     * @param correo Correo del usuario solicitante
     * @return Respuesta vacía si se crea correctamente
     */
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

    /**
     * Responde una solicitud de edición (aceptar o rechazar).
     * @param id ID de la solicitud
     * @param aceptar true para aceptar, false para rechazar
     * @param autorCorreo Correo del autor que responde
     * @return Respuesta vacía si se responde correctamente
     */
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

    /**
     * Lista las solicitudes de edición recibidas por un autor.
     * @param correo Correo del autor
     * @return Lista de solicitudes recibidas
     */
    @Operation(summary = "Listar solicitudes de edición recibidas")
    @GetMapping("/solicitudes/recibidas")
    public ResponseEntity<List<SolicitudEdicionDTO>> solicitudesRecibidas(
            @Parameter(description = "Correo del autor", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.solicitudesPorAutor(correo));
    }

    /**
     * Lista las solicitudes de edición enviadas por un usuario.
     * @param correo Correo del usuario
     * @return Lista de solicitudes enviadas
     */
    @Operation(summary = "Listar solicitudes de edición enviadas")
    @GetMapping("/solicitudes/enviadas")
    public ResponseEntity<List<SolicitudEdicionDTO>> solicitudesEnviadas(
            @Parameter(description = "Correo del usuario", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.solicitudesPorUsuario(correo));
    }

    /**
     * Suscribe a un usuario a un documento público.
     * @param id ID del documento
     * @param correoUsuario Correo del usuario
     * @return Respuesta vacía si se suscribe correctamente
     */
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

    /**
     * Cancela la suscripción de un usuario a un documento.
     * @param id ID del documento
     * @param correoUsuario Correo del usuario
     * @return Respuesta vacía si se cancela correctamente
     */
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

    /**
     * Lista los documentos a los que un usuario está suscrito.
     * @param correo Correo del usuario
     * @return Lista de suscripciones
     */
    @Operation(summary = "Listar documentos suscritos")
    @GetMapping("/suscripciones")
    public ResponseEntity<List<SuscripcionDTO>> listarSuscripciones(
            @Parameter(description = "Correo del usuario", required = true) @RequestParam String correo
    ) {
        return ResponseEntity.ok(documentService.documentosSuscritos(correo));
    }
} 