package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Notificacion;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de notificaciones de usuario.
 * Define operaciones para obtener, marcar y listar notificaciones.
 */
public interface INotificacionService {
    /**
     * Obtiene todas las notificaciones de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones
     */
    List<Notificacion> obtenerTodas(String correo);
    /**
     * Obtiene las notificaciones no leídas de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notificacion> obtenerNoLeidas(String correo);
    /**
     * Marca una notificación como leída.
     * @param id ID de la notificación
     */
    void marcarComoLeida(Long id);
    /**
     * Marca todas las notificaciones de un usuario como leídas.
     * @param correo Correo del usuario
     */
    void marcarTodasComoLeidas(String correo);
}
