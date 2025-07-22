package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Notificacion.
 * Permite operaciones CRUD y consultas personalizadas sobre notificaciones.
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    /**
     * Busca notificaciones no leídas para un usuario.
     * @param correo Correo del usuario destinatario
     * @return Lista de notificaciones no leídas
     */
    List<Notificacion> findByCorreoDestinoAndLeidaFalse(String correo);
    /**
     * Busca todas las notificaciones de un usuario ordenadas por fecha de creación descendente.
     * @param correo Correo del usuario destinatario
     * @return Lista de notificaciones ordenadas
     */
    List<Notificacion> findByCorreoDestinoOrderByFechaCreacionDesc(String correo);
}
