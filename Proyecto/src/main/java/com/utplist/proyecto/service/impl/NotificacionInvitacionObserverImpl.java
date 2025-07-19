package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Invitacion;
import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.repository.NotificacionRepository;
import com.utplist.proyecto.service.IInvitacionObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class NotificacionInvitacionObserverImpl implements IInvitacionObserver {

    @Autowired
    private NotificacionRepository notificacionRepository;

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
