package com.utplist.proyecto.controller;

import com.utplist.proyecto.model.Notificacion;
import com.utplist.proyecto.service.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final INotificacionService notificacionService;

    @GetMapping("/{correo}")
    public ResponseEntity<List<Notificacion>> obtenerTodas(@PathVariable String correo) {
        return ResponseEntity.ok(notificacionService.obtenerTodas(correo));
    }

    @GetMapping("/{correo}/no-leidas")
    public ResponseEntity<List<Notificacion>> obtenerNoLeidas(@PathVariable String correo) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(correo));
    }

    @PostMapping("/{id}/marcar-leida")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Long id) {
        notificacionService.marcarComoLeida(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{correo}/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodas(@PathVariable String correo) {
        notificacionService.marcarTodasComoLeidas(correo);
        return ResponseEntity.ok().build();
    }
}
