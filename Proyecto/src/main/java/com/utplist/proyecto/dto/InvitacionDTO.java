package com.utplist.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitacionDTO {
    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Formato de correo inválido.")
    private String correoInvitado;
    @NotBlank(message = "El rol no puede estar vacío.")
    @Pattern(regexp = "EDITOR|VISUALIZADOR", message = "Rol debe ser EDITOR o VISUALIZADOR.")
    private String rol;
    private boolean aceptada;
} 