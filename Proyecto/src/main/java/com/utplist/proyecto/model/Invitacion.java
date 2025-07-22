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

    /** Identificador único de la invitación */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo electrónico del invitado */
    @NotBlank
    @Email
    @Column(name = "correo_invitado", nullable = false)
    private String correoInvitado;

    /** Rol asignado al invitado (EDITOR o VISUALIZADOR) */
    @Enumerated(EnumType.STRING)
    private RollInvitado rol;

    /** Indica si la invitación fue aceptada */
    private Boolean aceptada = Boolean.FALSE;

    /** Documento al que pertenece la invitación */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_id")
    @JsonIgnore
    private Document documento;
} 