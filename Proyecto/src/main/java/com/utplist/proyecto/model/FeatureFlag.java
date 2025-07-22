package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una bandera de funcionalidad (feature flag) en el sistema.
 * Permite habilitar o deshabilitar características específicas de la aplicación.
 */
@Entity
@Table(name = "feature_flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlag {

    /** Identificador único de la feature flag */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único de la feature flag */
    @Column(nullable = false, unique = true)
    private String nombre;

    /** Indica si la feature flag está habilitada */
    @Column(nullable = false)
    private Boolean habilitado;
} 