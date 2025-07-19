package com.utplist.proyecto.dto;

import com.utplist.proyecto.model.SolicitudEdicion;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEdicionDTO {
    private Long id;
    private Long documentoId;
    private String correoSolicitante;
    private SolicitudEdicion.EstadoSolicitud estado;
} 