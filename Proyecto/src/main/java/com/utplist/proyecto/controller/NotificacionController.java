package com.utplist.proyecto.controller;

import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones de usuario.
 * Permite obtener, marcar como leídas y listar notificaciones.
 */
@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final INotificacionService notificacionService;

    /**
     * Obtiene todas las notificaciones de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones
     */
    @GetMapping("/{correo}")
    public ResponseEntity<List<Notificacion>> obtenerTodas(@PathVariable String correo) {
        return ResponseEntity.ok(notificacionService.obtenerTodas(correo));
    }

    /**
     * Obtiene las notificaciones no leídas de un usuario.
     * @param correo Correo del usuario
     * @return Lista de notificaciones no leídas
     */
    @GetMapping("/{correo}/no-leidas")
    public ResponseEntity<List<Notificacion>> obtenerNoLeidas(@PathVariable String correo) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(correo));
    }

    /**
     * Marca una notificación como leída.
     * @param id ID de la notificación
     * @return Respuesta vacía si se marca correctamente
     */
    @PostMapping("/{id}/marcar-leida")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas.
     * @param correo Correo del usuario
     * @return Respuesta vacía si se marcan correctamente
     */
    @PostMapping("/{correo}/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodas(@PathVariable String correo) {
        notificacionService.marcarTodasComoLeidas(correo);
        return ResponseEntity.ok().build();
    }
}
