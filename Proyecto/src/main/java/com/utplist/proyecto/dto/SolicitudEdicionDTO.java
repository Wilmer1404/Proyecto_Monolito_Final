package com.utplist.proyecto.dto;

import com.utplist.proyecto.model.SolicitudEdicion;
import lombok.*;

/**
 * DTO que representa una solicitud de edición sobre un documento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEdicionDTO {
    /** ID único de la solicitud */
    private Long id;
    /** ID del documento al que se solicita la edición */
    private Long documentoId;
    /** Correo electrónico del solicitante */
    private String correoSolicitante;
    /** Estado actual de la solicitud */
    private SolicitudEdicion.EstadoSolicitud estado;
} 