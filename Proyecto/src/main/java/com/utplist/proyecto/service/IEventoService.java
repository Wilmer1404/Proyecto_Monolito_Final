package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Evento;
import java.util.List;

public interface IEventoService {
    void guardarEvento(Evento evento);
    List<Evento> listarEventos();
} 