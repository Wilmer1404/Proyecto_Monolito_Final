package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Invitacion;
import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.repository.NotificacionRepository;
import com.utplist.proyecto.service.IInvitacionObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Implementación del observer para notificar a los usuarios cuando son invitados a un documento.
 * Crea y guarda una notificación en la base de datos.
 */
@Configuration
public class NotificacionInvitacionObserverImpl implements IInvitacionObserver {

    @Autowired
    private NotificacionRepository notificacionRepository;

    /**
     * Método que se ejecuta cuando un usuario es invitado a un documento.
     * @param invitacion Invitación creada
     */
    @Override
    public void onUsuarioInvitado(Invitacion invitacion) {
        String mensaje = String.format("Has sido invitado como %s al documento '%s'.",
                invitacion.getRol().name(), invitacion.getDocumento().getTitulo());

        Notificacion noti = Notificacion.builder()
                .correoDestino(invitacion.getCorreoInvitado())
                .mensaje(mensaje)
                .fechaCreacion(LocalDateTime.now())
                .leida(false)
                .build();

        notificacionRepository.save(noti);
    }
}
