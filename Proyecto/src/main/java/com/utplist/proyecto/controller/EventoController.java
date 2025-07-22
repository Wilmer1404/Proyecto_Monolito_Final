package com.utplist.proyecto.controller;

import com.utplist.proyecto.model.Evento;
import com.utplist.proyecto.service.IEventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestión de eventos del sistema.
 * Permite listar todos los eventos registrados.
 */
@Tag(name = "Eventos", description = "Operaciones sobre eventos del sistema")
@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final IEventoService service;

    /**
     * Lista todos los eventos registrados en el sistema.
     * @return Lista de eventos
     */
    @Operation(summary = "Listar todos los eventos registrados")
    @GetMapping
    public List<Evento> listar() {
        return service.listarEventos();
    }
} 