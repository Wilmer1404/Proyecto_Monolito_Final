package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.model.Evento;
import com.utplist.proyecto.repository.EventoRepository;
import com.utplist.proyecto.service.IEventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements IEventoService {
    private final EventoRepository repository;
    @Override
    public void guardarEvento(Evento evento) {
        repository.save(evento);
    }
    @Override
    public List<Evento> listarEventos() {
        return repository.findAll();
    }
} 