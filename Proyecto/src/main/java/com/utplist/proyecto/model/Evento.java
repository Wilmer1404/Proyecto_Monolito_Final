package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa un evento registrado en el sistema.
 * Incluye información sobre el tipo de evento, usuario, rol y fecha.
 */
@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento {

    /** Identificador único del evento */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Descripción del evento realizado */
    private String evento;
    /** Correo electrónico del usuario relacionado al evento */
    private String correo;
    /** Rol del usuario en el evento */
    private String rol;
    /** Fecha y hora en que ocurrió el evento */
    private LocalDateTime fecha;
} 