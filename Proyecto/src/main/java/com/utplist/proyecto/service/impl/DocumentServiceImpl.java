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
    @Override
    public Page<DocumentResponseDTO> obtenerDocumentosPorAutor(String correo, Pageable pageable) {
        return repository.findByAutorCorreo(correo, pageable).map(this::toDTO);
    }
    @Override
    public DocumentResponseDTO obtenerPorId(Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        return toDTO(doc);
    }
    @Override
    public void eliminarDocumento(Long userId, Long id) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        if (!esAutorONivelSuper(userId, doc.getAutorCorreo())) {
            throw new PermisoDenegadoException("No tienes permiso para eliminar este documento");
        }
        repository.deleteById(id);
    }
    @Override
    public Page<DocumentResponseDTO> obtenerDocumentosCompartidos(String correoInvitado, Pageable pageable) {
        return repository.findCompartidosCon(correoInvitado, pageable).map(this::toDTO);
    }
    @Override
    public Page<DocumentResponseDTO> buscar(String titulo, String categoria, String autor, Pageable pageable) {
        Specification<Document> spec = Specification.allOf(
                tituloLike(titulo),
                categoriaEq(categoria),
                autorEq(autor)
        );
        return repository.findAll(spec, pageable).map(this::toDTO);
    }
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
    @Override
    public void aceptarInvitacion(Long documentoId, String correoInvitado) {
        Invitacion inv = invitacionRepository.findByDocumentoIdAndCorreoInvitado(documentoId, correoInvitado)
                .orElseThrow(() -> new DocumentoNoEncontradoException(documentoId));
        inv.setAceptada(true);
        invitacionRepository.save(inv);
    }
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
    @Override
    public List<InvitacionDTO> listarInvitaciones(Long idDocumento) {
        return invitacionRepository.findByDocumentoId(idDocumento).stream()
                .map(i -> new InvitacionDTO(i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()))
                .collect(Collectors.toList());
    }
    @Override
    public List<InvitacionDTO> obtenerInvitacionesPorUsuario(String correoInvitado) {
        return invitacionRepository.findByCorreoInvitadoAndAceptadaTrue(correoInvitado).stream()
                .map(i -> new InvitacionDTO(i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()))
                .collect(Collectors.toList());
    }
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
    @Override
    public List<SolicitudEdicionDTO> solicitudesPorAutor(String correoAutor) {
        return solicitudEdicionRepository.findByDocumentoAutorCorreo(correoAutor).stream()
                .map(this::toSolicitudDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<SolicitudEdicionDTO> solicitudesPorUsuario(String correoUsuario) {
        return solicitudEdicionRepository.findByCorreoSolicitante(correoUsuario).stream()
                .map(this::toSolicitudDTO)
                .collect(Collectors.toList());
    }
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
    @Override
    public void cancelarSuscripcion(String correoUsuario, Long documentoId) {
        suscripcionRepository.deleteByCorreoUsuarioAndDocumentoId(correoUsuario, documentoId);
    }
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