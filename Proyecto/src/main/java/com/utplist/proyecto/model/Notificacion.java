package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación enviada a un usuario.
 * Incluye información sobre el destinatario, mensaje, fecha y estado de lectura.
 */
@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {
    /** Identificador único de la notificación */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo electrónico del destinatario */
    private String correoDestino;
    /** Mensaje de la notificación */
    private String mensaje;
    /** Fecha de creación de la notificación */
    private LocalDateTime fechaCreacion;
    /** Indica si la notificación ha sido leída */
    private Boolean leida;
}
