package com.utplist.proyecto.service;

import com.utplist.proyecto.model.Evento;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de eventos del sistema.
 * Define operaciones para guardar y listar eventos.
 */
public interface IEventoService {
    /**
     * Guarda un evento en el sistema.
     * @param evento Evento a guardar
     */
    void guardarEvento(Evento evento);
    /**
     * Lista todos los eventos registrados en el sistema.
     * @return Lista de eventos
     */
    List<Evento> listarEventos();
} 