package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.SolicitudEdicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudEdicionRepository extends JpaRepository<SolicitudEdicion, Long> {
    List<SolicitudEdicion> findByDocumentoAutorCorreo(String correoAutor);
    List<SolicitudEdicion> findByCorreoSolicitante(String correo);
    Optional<SolicitudEdicion> findByDocumentoIdAndCorreoSolicitante(Long documentoId, String correo);
    boolean existsByCorreoSolicitanteAndDocumentoId(String correoSolicitante, Long documentoId);
} 