package com.utplist.proyecto.dto;

import lombok.*;

/**
 * DTO que representa un evento para ser enviado a través de Kafka.
 * Incluye información sobre el evento, usuario y momento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KafkaEventDTO {
    /** Tipo de evento */
    private String event;
    /** Correo electrónico del usuario relacionado al evento */
    private String email;
    /** Rol del usuario */
    private String role;
    /** Momento en que ocurrió el evento (formato texto) */
    private String timestamp;
} 