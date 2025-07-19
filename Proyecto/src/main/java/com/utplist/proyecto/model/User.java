package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa un usuario en el sistema.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Identificador único del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo electrónico del usuario */
    @Column(unique = true, nullable = false)
    private String email;

    /** Contraseña encriptada */
    @Column(nullable = false)
    private String password;

    /** Rol asignado al usuario */
    @Enumerated(EnumType.STRING)
    private Role role;
} 