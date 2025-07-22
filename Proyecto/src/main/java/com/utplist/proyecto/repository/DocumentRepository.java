package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad Document.
 * Permite operaciones CRUD y consultas personalizadas sobre documentos.
 */
public interface DocumentRepository
        extends JpaRepository<Document, Long>,
        JpaSpecificationExecutor<Document> {

    /**
     * Busca documentos por el correo del autor con paginación.
     * @param autorCorreo Correo del autor
     * @param pageable Parámetros de paginación
     * @return Página de documentos
     */
    Page<Document> findByAutorCorreo(String autorCorreo, Pageable pageable);

    @Query("""
           SELECT d FROM Document d
             JOIN Invitacion i ON i.documento.id = d.id
           WHERE i.correoInvitado = :correo
             AND i.aceptada       = true
           """)
    /**
     * Busca documentos compartidos con un usuario invitado y aceptados.
     * @param correo Correo del invitado
     * @param pageable Parámetros de paginación
     * @return Página de documentos compartidos
     */
    Page<Document> findCompartidosCon(@Param("correo") String correo, Pageable pageable);
} 