package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    boolean existsByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
    Optional<Suscripcion> findByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
    List<Suscripcion> findByCorreoUsuario(String correoUsuario);
    void deleteByCorreoUsuarioAndDocumentoId(String correoUsuario, Long documentoId);
} 