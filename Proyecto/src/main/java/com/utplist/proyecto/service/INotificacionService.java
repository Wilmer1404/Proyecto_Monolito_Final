package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Notificacion;
import java.util.List;

public interface INotificacionService {
    List<Notificacion> obtenerTodas(String correo);
    List<Notificacion> obtenerNoLeidas(String correo);
    void marcarComoLeida(Long id);
    void marcarTodasComoLeidas(String correo);
}
