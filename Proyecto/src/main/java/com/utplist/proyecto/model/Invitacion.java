package com.utplist.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Relación entre un documento y un invitado con permisos.
 */
@Entity
@Table(name = "invitaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "correo_invitado", nullable = false)
    private String correoInvitado;

    @Enumerated(EnumType.STRING)
    private RollInvitado rol;

    private Boolean aceptada = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id")
    @JsonIgnore
    private Document documento;
} 