package com.utplist.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO de respuesta para el inicio de sesión.
 * Contiene el token JWT y el rol del usuario autenticado.
 */
@Data
@AllArgsConstructor
public class LoginResponseDTO {
    /** Token JWT generado para el usuario */
    private String token;
    /** Rol del usuario autenticado */
    private String role;
} 