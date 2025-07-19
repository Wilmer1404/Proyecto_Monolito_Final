package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByCorreoDestinoAndLeidaFalse(String correo);
    List<Notificacion> findByCorreoDestinoOrderByFechaCreacionDesc(String correo);
}
