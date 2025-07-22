package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.repository.NotificacionRepository;
import com.utplist.proyecto.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio para la gestión de notificaciones de usuario.
 * Permite obtener, marcar y listar notificaciones en la base de datos.
 */
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final NotificacionRepository notificacionRepository;

    /**
     * Obtiene todas las notificaciones de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones
     */
    @Override
    public List<Notificacion> obtenerTodas(String correo) {
        return notificacionRepository.findByCorreoDestinoOrderByFechaCreacionDesc(correo);
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones no leídas
     */
    @Override
    public List<Notificacion> obtenerNoLeidas(String correo) {
        return notificacionRepository.findByCorreoDestinoAndLeidaFalse(correo);
    }

    /**
     * Marca una notificación como leída.
     * @param id ID de la notificación
     */
    @Override
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(noti -> {
            noti.setLeida(true);
            notificacionRepository.save(noti);
        });
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     * @param correo Correo del usuario
     */
    @Override
    public void marcarTodasComoLeidas(String correo) {
        List<Notificacion> pendientes = notificacionRepository.findByCorreoDestinoAndLeidaFalse(correo);
        pendientes.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(pendientes);
    }
}

