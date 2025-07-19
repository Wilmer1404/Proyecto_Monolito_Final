package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface DocumentRepository
        extends JpaRepository<Document, Long>,
        JpaSpecificationExecutor<Document> {

    Page<Document> findByAutorCorreo(String autorCorreo, Pageable pageable);

    @Query("""
           SELECT d FROM Document d
             JOIN Invitacion i ON i.documento.id = d.id
           WHERE i.correoInvitado = :correo
             AND i.aceptada       = true
           """)
    Page<Document> findCompartidosCon(@Param("correo") String correo, Pageable pageable);
} 