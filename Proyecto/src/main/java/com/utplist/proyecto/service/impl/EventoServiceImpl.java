package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Evento;
import com.utplist.proyecto.repository.EventoRepository;
import com.utplist.proyecto.service.IEventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementación del servicio de eventos.
 * Permite guardar y listar eventos del sistema.
 */
@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements IEventoService {
    private final EventoRepository repository;
    /**
     * Guarda un evento en el sistema.
     * @param evento Evento a guardar
     */
    @Override
    public void guardarEvento(Evento evento) {
        repository.save(evento);
    }
    /**
     * Lista todos los eventos registrados.
     * @return Lista de eventos
     */
    @Override
    public List<Evento> listarEventos() {
        return repository.findAll();
    }
} 