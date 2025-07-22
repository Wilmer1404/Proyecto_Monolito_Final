package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.model.RollInvitado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de documentos colaborativos.
 * Define operaciones para crear, editar, eliminar, buscar, invitar, suscribir y gestionar solicitudes sobre documentos.
 */
public interface IDocumentService {
    /**
     * Crea un nuevo documento y lo asocia a un autor.
     * @param dto Datos del documento
     * @param correo Correo del autor
     * @return DTO del documento creado
     */
    DocumentResponseDTO crearDocumento(CreateDocumentDTO dto, String correo);
    /**
     * Obtiene los documentos de un autor con paginación.
     * @param correo Correo del autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos
     */
    Page<DocumentResponseDTO> obtenerDocumentosPorAutor(String correo, Pageable pageable);
    /**
     * Obtiene un documento por su ID.
     * @param id ID del documento
     * @return DTO del documento encontrado
     */
    DocumentResponseDTO obtenerPorId(Long id);
    /**
     * Elimina un documento si el usuario es el autor o superadministrador.
     * @param userId ID del usuario
     * @param id ID del documento
     */
    void eliminarDocumento(Long userId, Long id);
    /**
     * Obtiene los documentos compartidos con un usuario invitado.
     * @param correoInvitado Correo del invitado
     * @param pageable Parámetros de paginación
     * @return Página de documentos compartidos
     */
    Page<DocumentResponseDTO> obtenerDocumentosCompartidos(String correoInvitado, Pageable pageable);
    /**
     * Realiza una búsqueda avanzada de documentos.
     * @param titulo Filtro por título
     * @param categoria Filtro por categoría
     * @param autor Filtro por autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos filtrados
     */
    Page<DocumentResponseDTO> buscar(String titulo, String categoria, String autor, Pageable pageable);
    /**
     * Invita a un usuario a colaborar en un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     * @param rol Rol asignado
     */
    void invitarUsuario(Long documentoId, String correoInvitado, RollInvitado rol);
    /**
     * Acepta una invitación a un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     */
    void aceptarInvitacion(Long documentoId, String correoInvitado);
    /**
     * Cambia el rol de un invitado en un documento.
     * @param documentoId ID del documento
     * @param correoInvitado Correo del invitado
     * @param nuevoRol Nuevo rol a asignar
     * @param autorCorreo Correo del autor
     */
    void cambiarRolDeInvitado(Long documentoId, String correoInvitado, RollInvitado nuevoRol, String autorCorreo);
    /**
     * Edita un documento si el usuario es el autor.
     * @param userId ID del usuario
     * @param id ID del documento
     * @param dto Datos a editar
     */
    void editarDocumento(Long userId, Long id, EditarDocumentoDTO dto);
    /**
     * Lista las invitaciones de un documento.
     * @param idDocumento ID del documento
     * @return Lista de invitaciones
     */
    List<InvitacionDTO> listarInvitaciones(Long idDocumento);
    /**
     * Obtiene las invitaciones aceptadas de un usuario.
     * @param correoInvitado Correo del invitado
     * @return Lista de invitaciones aceptadas
     */
    List<InvitacionDTO> obtenerInvitacionesPorUsuario(String correoInvitado);
    /**
     * Solicita la edición de un documento público.
     * @param correoSolicitante Correo del solicitante
     * @param documentoId ID del documento
     */
    void solicitarEdicion(String correoSolicitante, Long documentoId);
    /**
     * Responde una solicitud de edición.
     * @param solicitudId ID de la solicitud
     * @param aceptar true para aceptar, false para rechazar
     * @param autorCorreo Correo del autor
     */
    void responderSolicitudEdicion(Long solicitudId, boolean aceptar, String autorCorreo);
    /**
     * Lista las solicitudes de edición recibidas por un autor.
     * @param correoAutor Correo del autor
     * @return Lista de solicitudes recibidas
     */
    List<SolicitudEdicionDTO> solicitudesPorAutor(String correoAutor);
    /**
     * Lista las solicitudes de edición enviadas por un usuario.
     * @param correoUsuario Correo del usuario
     * @return Lista de solicitudes enviadas
     */
    List<SolicitudEdicionDTO> solicitudesPorUsuario(String correoUsuario);
    /**
     * Suscribe a un usuario a un documento público.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     */
    void suscribirseDocumentoPublico(String correoUsuario, Long documentoId);
    /**
     * Cancela la suscripción de un usuario a un documento.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     */
    void cancelarSuscripcion(String correoUsuario, Long documentoId);
    /**
     * Lista los documentos a los que un usuario está suscrito.
     * @param correoUsuario Correo del usuario
     * @return Lista de suscripciones
     */
    List<SuscripcionDTO> documentosSuscritos(String correoUsuario);
} 