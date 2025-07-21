package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.model.RollInvitado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IDocumentService {
    DocumentResponseDTO crearDocumento(CreateDocumentDTO dto, String correo);
    Page<DocumentResponseDTO> obtenerDocumentosPorAutor(String correo, Pageable pageable);
    DocumentResponseDTO obtenerPorId(Long id);
    void eliminarDocumento(Long userId, Long id);
    Page<DocumentResponseDTO> obtenerDocumentosCompartidos(String correoInvitado, Pageable pageable);
    Page<DocumentResponseDTO> buscar(String titulo, String categoria, String autor, Pageable pageable);
    void invitarUsuario(Long documentoId, String correoInvitado, RollInvitado rol);
    void aceptarInvitacion(Long documentoId, String correoInvitado);
    void cambiarRolDeInvitado(Long documentoId, String correoInvitado, RollInvitado nuevoRol, String autorCorreo);
    void editarDocumento(Long userId, Long id, EditarDocumentoDTO dto);
    List<InvitacionDTO> listarInvitaciones(Long idDocumento);
    List<InvitacionDTO> obtenerInvitacionesPorUsuario(String correoInvitado);
    void solicitarEdicion(String correoSolicitante, Long documentoId);
    void responderSolicitudEdicion(Long solicitudId, boolean aceptar, String autorCorreo);
    List<SolicitudEdicionDTO> solicitudesPorAutor(String correoAutor);
    List<SolicitudEdicionDTO> solicitudesPorUsuario(String correoUsuario);
    void suscribirseDocumentoPublico(String correoUsuario, Long documentoId);
    void cancelarSuscripcion(String correoUsuario, Long documentoId);
    List<SuscripcionDTO> documentosSuscritos(String correoUsuario);
} 