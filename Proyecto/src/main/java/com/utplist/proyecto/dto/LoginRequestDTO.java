package com.utplist.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "DTO para login de usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {
    @Schema(description = "Email del usuario", example = "usuario@utpl.edu.ec", required = true)
    @Email
    private String email;
    @Schema(description = "Contraseña del usuario", example = "123456", required = true)
    @NotBlank
    private String password;
} 