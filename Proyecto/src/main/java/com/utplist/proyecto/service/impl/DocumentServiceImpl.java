package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.exception.*;
import com.utplist.proyecto.model.*;
import com.utplist.proyecto.observer.InvitacionNotifier;
import com.utplist.proyecto.repository.*;
import com.utplist.proyecto.service.IDocumentService;
import com.utplist.proyecto.service.IFeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static com.utplist.proyecto.repository.DocumentSpecifications.*;

/**
 * Implementación del servicio de documentos.
 * Gestiona la lógica de negocio para la creación, edición, eliminación, invitaciones, solicitudes y suscripciones de documentos.
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements IDocumentService {
    private final DocumentRepository repository;
    private final InvitacionRepository invitacionRepository;
    private final SolicitudEdicionRepository solicitudEdicionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final IFeatureFlagService featureFlagService;
    private final InvitacionNotifier invitacionNotifier; // Agrega esto en el constructor con @RequiredArgsConstructor
    private final UserRepository userRepository;

    private static final String FLAG_SUSCRIPCIONES = "suscripciones";
    private static final String FLAG_SOLICITUDES_EDICION = "solicitudes_edicion";
    private static final String FLAG_INVITACIONES = "invitaciones";
    private String hola;

    private boolean esAutorONivelSuper(Long userId, String autorCorreo) {
        User u = userRepository.findById(userId)
                .orElseThrow();
        return u.getRole() == Role.SUPERADMINISTRADOR || u.getEmail().equals(autorCorreo);
    }
    private boolean esAutor(Long userId, String autorCorreo) {
        User u = userRepository.findById(userId)
                .orElseThrow();
        return u.getEmail().equals(autorCorreo);
    }

    /**
     * Crea un nuevo documento y lo asocia a un autor.
     * @param dto Datos del documento
     * @param correo Correo del autor
     * @return DTO del documento creado
     */
    @Override
    public DocumentResponseDTO crearDocumento(CreateDocumentDTO dto, String correo) {
        User autor = userRepository.findByEmail(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Document doc = Document.builder()
                .titulo(dto.getTitulo())
                .autorCorreo(correo)
                .autor(autor)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .contenido("")
                .categoria("")
                .publico(Optional.ofNullable(dto.getPublico()).orElse(Boolean.FALSE))
                .editable(Boolean.TRUE)
                .build();
        Document saved = repository.save(doc);
        return toDTO(saved);
            


    }
    //hola
    /**
     * Obtiene los documentos de un autor con paginación.
     * @param correo Correo del autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos
     */
    @Override
    public Page<DocumentResponseDTO> obtenerDocumentosPorAutor(String correo, Pageable pageable) {
        return repository.findByAutorCorreo(correo, pageable).map(this::toDTO);
    }
    /**
     * Obtiene un documento por su ID.
     * @param id ID del documento
     * @return DTO del documento encontrado
     */
    @Override
    public DocumentResponseDTO obtenerPorId(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        return toDTO(doc);
    }
    /**
     * Elimina un documento si el usuario es el autor o superadministrador.
     * @param userId ID del usuario
     * @param id ID del documento
     */
    @Override
    public void eliminarDocumento(Long userId, Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        if (!esAutorONivelSuper(userId, doc.getAutorCorreo())) {
            throw new PermisoDenegadoException("No tienes permiso para eliminar este documento");
        }
        repository.deleteById(id);
    }
    /**
     * Obtiene los documentos compartidos con un usuario invitado.
     * @param correoInvitado Correo del invitado
     * @param pageable Parámetros de paginación
     * @return Página de documentos compartidos
     */
    @Override
    public Page<DocumentResponseDTO> obtenerDocumentosCompartidos(String correoInvitado, Pageable pageable) {
        return repository.findCompartidosCon(correoInvitado, pageable).map(this::toDTO);
    }
    /**
     * Realiza una búsqueda avanzada de documentos.
     * @param titulo Filtro por título
     * @param categoria Filtro por categoría
     * @param autor Filtro por autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos filtrados
     */
    @Override
    public Page<DocumentResponseDTO> buscar(String titulo, String categoria, String autor, Pageable pageable) {
        Specification<Document> spec = Specification.allOf(
                tituloLike(titulo),
                categoriaEq(categoria),
                autorEq(autor)
        );
        return repository.findAll(spec, pageable).map(this::toDTO);
    }
    /**
     * Invita a un usuario a colaborar en un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     * @param rol Rol asignado
     */
    @Override
    public void invitarUsuario(Long documentoId, String correoInvitado, RollInvitado rol) {
        if (!featureFlagService.isEnabled(FLAG_INVITACIONES)) {
            throw new PermisoDenegadoException("Invitaciones deshabilitadas");
        }
        Document doc = repository.findById(documentoId).orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        Invitacion inv = invitacionRepository.findByDocumentoIdAndCorreoInvitado(documentoId, correoInvitado)
                .orElse(Invitacion.builder().documento(doc).correoInvitado(correoInvitado).build());
        if (Boolean.TRUE.equals(inv.getAceptada())) {
            throw new InvitacionDuplicadaException(correoInvitado, documentoId);
        }
        inv.setRol(rol);
        inv.setAceptada(false);

        invitacionRepository.save(inv);
        invitacionNotifier.notificarInvitacion(inv);
    }
    /**
     * Acepta una invitación a un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     */
    @Override
    public void aceptarInvitacion(Long documentoId, String correoInvitado) {
        Invitacion inv = invitacionRepository.findByDocumentoIdAndCorreoInvitado(documentoId, correoInvitado)
                .orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        inv.setAceptada(true);
        invitacionRepository.save(inv);
    }
    /**
     * Cambia el rol de un invitado en un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     * @param nuevoRol Nuevo rol a asignar
     * @param autorCorreo Correo del autor
     */
    @Override
    public void cambiarRolDeInvitado(Long documentoId, String correoInvitado, RollInvitado nuevoRol, String autorCorreo) {
        Document doc = repository.findById(documentoId).orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        if (!doc.getAutorCorreo().equals(autorCorreo)) {
            throw new PermisoDenegadoException("Solo el autor puede cambiar roles");
        }
        Invitacion inv = invitacionRepository.findByDocumentoIdAndCorreoInvitado(documentoId, correoInvitado)
                .orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        if (!Boolean.TRUE.equals(inv.getAceptada())) {
            throw new PermisoDenegadoException("Invitación no aceptada");
        }
        inv.setRol(nuevoRol);
        invitacionRepository.save(inv);
    }
    /**
     * Edita un documento si el usuario es el autor.
     * @param userId ID del usuario
     * @param id ID del documento
     * @param dto Datos a editar
     */
    @Override
    public void editarDocumento(Long userId, Long id, EditarDocumentoDTO dto) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        if (!esAutor(userId, doc.getAutorCorreo())) {
            throw new PermisoDenegadoException("Solo el autor puede editar este documento");
        }
        Optional.ofNullable(dto.getTitulo()).ifPresent(doc::setTitulo);
        Optional.ofNullable(dto.getContenido()).ifPresent(doc::setContenido);
        Optional.ofNullable(dto.getCategoria()).ifPresent(doc::setCategoria);
        doc.setFechaActualizacion(LocalDateTime.now());
        repository.save(doc);
    }
    /**
     * Lista las invitaciones de un documento.
     * @param idDocumento ID del documento
     * @return Lista de invitaciones
     */
    @Override
    public List<InvitacionDTO> listarInvitaciones(Long idDocumento) {
        return invitacionRepository.findByDocumentoId(idDocumento).stream()
                .map(i -> new InvitacionDTO(i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()))
                .collect(Collectors.toList());
    }
    /**
     * Obtiene las invitaciones aceptadas de un usuario.
     * @param correoInvitado Correo del invitado
     * @return Lista de invitaciones aceptadas
     */
    @Override
    public List<InvitacionDTO> obtenerInvitacionesPorUsuario(String correoInvitado) {
        return invitacionRepository.findByCorreoInvitadoAndAceptadaTrue(correoInvitado).stream()
                .map(i -> new InvitacionDTO(i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()))
                .collect(Collectors.toList());
    }
    /**
     * Solicita la edición de un documento público.
     * @param correoSolicitante Correo del solicitante
     * @param documentoId ID del documento
     */
    @Override
    public void solicitarEdicion(String correoSolicitante, Long documentoId) {
        if (!featureFlagService.isEnabled(FLAG_SOLICITUDES_EDICION)) {
            throw new PermisoDenegadoException("Solicitudes de edición deshabilitadas");
        }
        Document doc = repository.findById(documentoId).orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        if (!Boolean.TRUE.equals(doc.getPublico()) || !Boolean.TRUE.equals(doc.getEditable())) {
            throw new PermisoDenegadoException("Documento no permite solicitudes");
        }
        if (solicitudEdicionRepository.existsByCorreoSolicitanteAndDocumentoId(correoSolicitante, documentoId)) {
            throw new PermisoDenegadoException("Ya enviaste una solicitud");
        }
        SolicitudEdicion sol = SolicitudEdicion.builder()
                .documento(doc)
                .correoSolicitante(correoSolicitante)
                .estado(SolicitudEdicion.EstadoSolicitud.PENDIENTE)
                .build();
        solicitudEdicionRepository.save(sol);
    }
    /**
     * Responde una solicitud de edición.
     * @param solicitudId ID de la solicitud
     * @param aceptar true para aceptar, false para rechazar
     * @param autorCorreo Correo del autor
     */
    @Override
    public void responderSolicitudEdicion(Long solicitudId, boolean aceptar, String autorCorreo) {
        SolicitudEdicion sol = solicitudEdicionRepository.findById(solicitudId)
                .orElseThrow(() -> new DocumentoNoEncontradoException(solicitudId));
        Document doc = sol.getDocumento();
        if (!doc.getAutorCorreo().equals(autorCorreo)) {
            throw new PermisoDenegadoException("Solo el autor puede responder solicitudes");
        }
        if (aceptar) {
            sol.setEstado(SolicitudEdicion.EstadoSolicitud.ACEPTADA);
        } else {
            sol.setEstado(SolicitudEdicion.EstadoSolicitud.RECHAZADA);
        }
        solicitudEdicionRepository.save(sol);
    }
    /**
     * Lista las solicitudes de edición recibidas por un autor.
     * @param correoAutor Correo del autor
     * @return Lista de solicitudes recibidas
     */
    @Override
    public List<SolicitudEdicionDTO> solicitudesPorAutor(String correoAutor) {
        return solicitudEdicionRepository.findByDocumentoAutorCorreo(correoAutor).stream()
                .map(this::toSolicitudDTO)
                .collect(Collectors.toList());
    }
    /**
     * Lista las solicitudes de edición enviadas por un usuario.
     * @param correoUsuario Correo del usuario
     * @return Lista de solicitudes enviadas
     */
    @Override
    public List<SolicitudEdicionDTO> solicitudesPorUsuario(String correoUsuario) {
        return solicitudEdicionRepository.findByCorreoSolicitante(correoUsuario).stream()
                .map(this::toSolicitudDTO)
                .collect(Collectors.toList());
    }
    /**
     * Suscribe a un usuario a un documento público.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     */
    @Override
    public void suscribirseDocumentoPublico(String correoUsuario, Long documentoId) {
        if (!featureFlagService.isEnabled(FLAG_SUSCRIPCIONES)) {
            throw new PermisoDenegadoException("Suscripciones deshabilitadas");
        }
        Document doc = repository.findById(documentoId).orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        if (!Boolean.TRUE.equals(doc.getPublico())) {
            throw new PermisoDenegadoException("Documento no es público");
        }
        if (suscripcionRepository.existsByCorreoUsuarioAndDocumentoId(correoUsuario, documentoId)) {
            throw new PermisoDenegadoException("Ya estás suscrito a este documento");
        }
        Suscripcion sus = Suscripcion.builder()
                .correoUsuario(correoUsuario)
                .documento(doc)
                .build();
        suscripcionRepository.save(sus);
    }
    /**
     * Cancela la suscripción de un usuario a un documento.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     */
    @Override
    public void cancelarSuscripcion(String correoUsuario, Long documentoId) {
        suscripcionRepository.deleteByCorreoUsuarioAndDocumentoId(correoUsuario, documentoId);
    }
    /**
     * Lista los documentos a los que un usuario está suscrito.
     * @param correoUsuario Correo del usuario
     * @return Lista de suscripciones
     */
    @Override
    public List<SuscripcionDTO> documentosSuscritos(String correoUsuario) {
        return suscripcionRepository.findByCorreoUsuario(correoUsuario).stream()
                .map(s -> SuscripcionDTO.builder()
                        .documentoId(s.getDocumento().getId())
                        .titulo(s.getDocumento().getTitulo())
                        .categoria(s.getDocumento().getCategoria())
                        .publico(s.getDocumento().getPublico())
                        .build())
                .collect(Collectors.toList());
    }
    private DocumentResponseDTO toDTO(Document doc) {
        return DocumentResponseDTO.builder()
                .id(doc.getId())
                .titulo(doc.getTitulo())
                .autorCorreo(doc.getAutorCorreo())
                .fechaCreacion(doc.getFechaCreacion())
                .fechaActualizacion(doc.getFechaActualizacion())
                .publico(doc.getPublico())
                .build();
    }
    private SolicitudEdicionDTO toSolicitudDTO(SolicitudEdicion sol) {
        return SolicitudEdicionDTO.builder()
                .id(sol.getId())
                .documentoId(sol.getDocumento().getId())
                .correoSolicitante(sol.getCorreoSolicitante())
                .estado(sol.getEstado())
                .build();
    }
}