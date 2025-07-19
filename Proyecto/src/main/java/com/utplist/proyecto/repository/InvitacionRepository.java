package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Invitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    List<Invitacion> findByDocumentoId(Long documentoId);
    Optional<Invitacion> findByDocumentoIdAndCorreoInvitado(Long documentoId, String correoInvitado);
    List<Invitacion> findByCorreoInvitadoAndAceptadaTrue(String correoInvitado);
} 