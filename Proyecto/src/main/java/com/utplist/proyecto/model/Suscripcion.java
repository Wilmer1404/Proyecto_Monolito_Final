package com.utplist.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad que representa la suscripción de un usuario a un documento.
 */
@Entity
@Table(name = "suscripciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"documento_id", "correo_usuario"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "correo_usuario", nullable = false)
    private String correoUsuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id")
    private Document documento;
} 