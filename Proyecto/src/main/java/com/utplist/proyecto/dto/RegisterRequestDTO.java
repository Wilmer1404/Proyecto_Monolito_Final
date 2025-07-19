package com.utplist.proyecto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "DTO para registro de usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    @Schema(description = "Email del nuevo usuario", example = "usuario@utpl.edu.ec", required = true)
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Formato de correo inválido")
    private String email;
    @Schema(description = "Contraseña del nuevo usuario", example = "123456", required = true)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
} 