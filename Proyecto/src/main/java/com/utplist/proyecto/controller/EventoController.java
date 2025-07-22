package com.utplist.proyecto.controller;

import com.utplist.proyecto.model.Evento;
import com.utplist.proyecto.service.IEventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Eventos", description = "Operaciones sobre eventos del sistema")
@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {
    private final IEventoService service;

    @Operation(summary = "Listar todos los eventos registrados")
    @GetMapping
    public List<Evento> listar() {
        return service.listarEventos();
    }
} 