package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
} 