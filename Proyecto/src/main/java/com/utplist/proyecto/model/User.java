package com.utplist.proyecto.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un usuario en el sistema.
 * Incluye información sobre el correo, contraseña y rol asignado.
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

    /** Correo electrónico único del usuario */
    @Column(unique = true, nullable = false)
    private String email;

    /** Contraseña encriptada del usuario */
    @Column(nullable = false)
    private String password;

    /** Rol asignado al usuario */
    @Enumerated(EnumType.STRING)
    private Role role;
} 