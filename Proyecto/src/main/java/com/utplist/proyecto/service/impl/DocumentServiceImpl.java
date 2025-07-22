package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.exception.*;
import com.utplist.proyecto.model.*;
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
import org.springframework.security.core.Authentication;
import com.utplist.proyecto.repository.ReporteDocumentoRepository;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements IDocumentService {
    private final DocumentRepository repository;
    private final InvitacionRepository invitacionRepository;
    private final SolicitudEdicionRepository solicitudEdicionRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final IFeatureFlagService featureFlagService;
    private final UserRepository userRepository;
    private final ReporteDocumentoRepository reporteDocumentoRepository;

    private static final String FLAG_SUSCRIPCIONES = "suscripciones";
    private static final String FLAG_SOLICITUDES_EDICION = "solicitudes_edicion";
    private static final String FLAG_INVITACIONES = "invitaciones";
    private String hola;
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

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
    @Override
    public DocumentResponseDTO crearDocumentoConArchivo(String titulo, Boolean publico, MultipartFile archivo, String correo) {
        User autor = userRepository.findByEmail(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String rutaArchivo = null;
        if (archivo != null && !archivo.isEmpty()) {
            try {
                String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                String directorio = "/home/samir/Descargas/Proyecto_Monolito_Final-feature/archivos_documentos/";
                Path directorioPath = Paths.get(directorio);
                if (!Files.exists(directorioPath)) {
                    Files.createDirectories(directorioPath);
                }
                Path archivoPath = directorioPath.resolve(nombreArchivo);
                archivo.transferTo(archivoPath);
                rutaArchivo = archivoPath.toString();
                logger.info("Archivo guardado en: {}", rutaArchivo);
            } catch (IOException e) {
                logger.error("Error al guardar el archivo", e);
                throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
            }
        }
        Document doc = Document.builder()
                .titulo(titulo)
                .publico(publico)
                .autorCorreo(correo)
                .autor(autor)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .contenido("")
                .categoria("")
                .editable(Boolean.TRUE)
                .rutaArchivo(rutaArchivo)
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
    public DocumentResponseDTO obtenerPorIdConPermiso(Long id, Authentication auth) {
        Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
        String email = auth.getName();
        boolean esSuperadmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMINISTRADOR"));
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        boolean esModerador = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_MODERADOR"));
        boolean esAutor = doc.getAutorCorreo().equals(email);
        boolean compartido = invitacionRepository.findByDocumentoIdAndCorreoInvitado(id, email).isPresent();
        boolean reportado = !reporteDocumentoRepository.findByDocumentoId(id).isEmpty();
        // Lógica de permisos
        if (esSuperadmin || esAutor || compartido || (reportado && (esModerador || esAdmin))) {
            return toDTO(doc);
        }
        throw new PermisoDenegadoException("No tienes permiso para ver este documento");
    }

    @Override
    public List<DocumentResponseDTO> obtenerDocumentosReportados() {
        List<Long> idsReportados = reporteDocumentoRepository.findAll().stream()
            .map(r -> r.getDocumento().getId())
            .distinct()
            .collect(Collectors.toList());
        List<Document> reportados = repository.findAllById(idsReportados);
        return reportados.stream().map(this::toDTO).collect(Collectors.toList());
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
        return repository.findCompartidosCon(correoInvitado, pageable).map(doc -> {
            DocumentResponseDTO dto = toDTO(doc);
            // Buscar la invitación aceptada para este documento y el usuario invitado
            Optional<Invitacion> miInv = invitacionRepository.findByDocumentoIdAndCorreoInvitado(doc.getId(), correoInvitado);
            String rolInvitacion = miInv.isPresent() && Boolean.TRUE.equals(miInv.get().getAceptada()) ? miInv.get().getRol().name() : null;
            dto.setRolInvitacion(rolInvitacion);
            return dto;
        });
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
    public DocumentResponseDTO editarDocumentoConArchivo(Long id, String titulo, Boolean publico, MultipartFile archivo, String correo, Authentication auth) {
        try {
            Document doc = repository.findById(id).orElseThrow(() -> new DocumentoNoEncontradoException(id));
            boolean esAutor = doc.getAutorCorreo().equals(correo);
            boolean esEditor = false;
            if (!esAutor) {
                Optional<Invitacion> invitacionOpt = invitacionRepository.findByDocumentoIdAndCorreoInvitado(id, correo);
                if (invitacionOpt.isPresent() && Boolean.TRUE.equals(invitacionOpt.get().getAceptada())) {
                    esEditor = invitacionOpt.get().getRol() == RollInvitado.EDITOR;
                    if (invitacionOpt.get().getRol() == RollInvitado.VISUALIZADOR) {
                        throw new PermisoDenegadoException("No tienes permisos para editar este documento");
                    }
                } else {
                    throw new PermisoDenegadoException("No tienes permisos para editar este documento");
                }
            }
            if (titulo != null) doc.setTitulo(titulo);
            if (publico != null) doc.setPublico(publico);
            if (archivo != null && !archivo.isEmpty()) {
                try {
                    String nombreArchivo = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
                    String directorio = "/home/samir/Descargas/Proyecto_Monolito_Final-feature/archivos_documentos/";
                    Path directorioPath = Paths.get(directorio);
                    if (!Files.exists(directorioPath)) {
                        Files.createDirectories(directorioPath);
                    }
                    Path archivoPath = directorioPath.resolve(nombreArchivo);
                    archivo.transferTo(archivoPath);
                    doc.setRutaArchivo(archivoPath.toString());
                    logger.info("Archivo actualizado en: {}", archivoPath);
                } catch (IOException e) {
                    logger.error("Error al actualizar el archivo", e);
                    throw new RuntimeException("Error al actualizar el archivo: " + e.getMessage(), e);
                }
            }
            doc.setFechaActualizacion(LocalDateTime.now());
            repository.save(doc);
            return toDTO(doc);
        } catch (Exception e) {
            logger.error("Error en editarDocumentoConArchivo: " + e.getMessage(), e);
            throw new RuntimeException("Error al editar el documento: " + e.getMessage(), e);
        }
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
                .map(i -> new InvitacionDTO(i.getDocumento().getId(), i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()))
                .collect(Collectors.toList());
    }
    @Override
    public Page<InvitacionDTO> obtenerInvitacionesPorUsuario(String correoInvitado, Pageable pageable) {
        return invitacionRepository.findByCorreoInvitado(correoInvitado, pageable)
                .map(i -> new InvitacionDTO(i.getDocumento().getId(), i.getCorreoInvitado(), i.getRol().name(), i.getAceptada()));
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
    public Page<SuscripcionDTO> documentosSuscritos(String correoUsuario, Pageable pageable) {
        return suscripcionRepository.findByCorreoUsuario(correoUsuario, pageable)
                .map(s -> SuscripcionDTO.builder()
                        .documentoId(s.getDocumento().getId())
                        .titulo(s.getDocumento().getTitulo())
                        .build());
    }
    private DocumentResponseDTO toDTO(Document doc) {
        return DocumentResponseDTO.builder()
                .id(doc.getId())
                .titulo(doc.getTitulo())
                .autorCorreo(doc.getAutorCorreo())
                .fechaCreacion(doc.getFechaCreacion())
                .fechaActualizacion(doc.getFechaActualizacion())
                .publico(doc.getPublico())
                .rutaArchivo(doc.getRutaArchivo())
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