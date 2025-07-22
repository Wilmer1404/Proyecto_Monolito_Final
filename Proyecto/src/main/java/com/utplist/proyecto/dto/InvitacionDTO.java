package com.utplist.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO que representa una invitación a colaborar en un documento.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitacionDTO {
    /** Correo electrónico del invitado */
    @NotBlank(message = "El correo no puede estar vacío.")
    @Email(message = "Formato de correo inválido.")
    private String correoInvitado;
    /** Rol asignado al invitado (EDITOR o VISUALIZADOR) */
    @NotBlank(message = "El rol no puede estar vacío.")
    @Pattern(regexp = "EDITOR|VISUALIZADOR", message = "Rol debe ser EDITOR o VISUALIZADOR.")
    private String rol;
    /** Indica si la invitación fue aceptada */
    private boolean aceptada;
} 