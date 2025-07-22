package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.repository.NotificacionRepository;
import com.utplist.proyecto.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> obtenerTodas(String correo) {
        return notificacionRepository.findByCorreoDestinoOrderByFechaCreacionDesc(correo);
    }

    @Override
    public List<Notificacion> obtenerNoLeidas(String correo) {
        return notificacionRepository.findByCorreoDestinoAndLeidaFalse(correo);
    }

    @Override
    public void marcarComoLeida(Long id) {
        notificacionRepository.findById(id).ifPresent(noti -> {
            noti.setLeida(true);
            notificacionRepository.save(noti);
        });
    }

    @Override
    public void marcarTodasComoLeidas(String correo) {
        List<Notificacion> pendientes = notificacionRepository.findByCorreoDestinoAndLeidaFalse(correo);
        pendientes.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(pendientes);
    }
}

