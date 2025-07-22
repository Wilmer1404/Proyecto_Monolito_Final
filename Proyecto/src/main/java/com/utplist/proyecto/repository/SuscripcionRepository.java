package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Suscripcion.
 * Permite operaciones CRUD y consultas personalizadas sobre suscripciones de usuarios a documentos.
 */
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    /**
     * Verifica si existe una suscripción para un usuario y documento.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     * @return true si existe, false si no
     */
    boolean existsByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
    /**
     * Busca una suscripción específica por usuario y documento.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     * @return Suscripción encontrada (opcional)
     */
    Optional<Suscripcion> findByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
    /**
     * Busca todas las suscripciones de un usuario.
     * @param correoUsuario Correo del usuario
     * @return Lista de suscripciones
     */
    List<Suscripcion> findByCorreoUsuario(String correoUsuario);
    /**
     * Elimina una suscripción por usuario y documento.
     * @param correoUsuario Correo del usuario
     * @param documentoId ID del documento
     */
    void deleteByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
} 