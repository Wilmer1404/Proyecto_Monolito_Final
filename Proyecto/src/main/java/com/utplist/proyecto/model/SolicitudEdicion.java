package com.utplist.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad que representa una solicitud de edición.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEdicion {

    /** Identificador único de la solicitud */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Documento al que se solicita la edición */
    @ManyToOne(optional = false)
    private Document documento;

    /** Correo electrónico del solicitante */
    @NotBlank
    @Email
    private String correoSolicitante;

    /** Estado actual de la solicitud */
    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    /**
     * Enumeración de los posibles estados de una solicitud de edición.
     */
    public enum EstadoSolicitud {
        PENDIENTE,
        ACEPTADA,
        RECHAZADA
    }
} 