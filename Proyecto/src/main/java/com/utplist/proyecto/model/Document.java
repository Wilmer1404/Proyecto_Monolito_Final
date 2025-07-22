package com.utplist.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un documento colaborativo en el sistema.
 * Incluye información sobre el autor, contenido, fechas, estado y relaciones con invitaciones.
 */
@Entity
@Table(name = "documentos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {

    /** Identificador único del documento */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título obligatorio, máximo 255 caracteres. */
    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 255)
    private String titulo;

    /** Correo del autor (se mantiene por compatibilidad / consultas rápidas). */
    @NotBlank @Email
    @Column(name = "autor_correo", nullable = false)
    private String autorCorreo;

    /**
     * Relación con User.
     *  - FK: autor_id → users.id
     *  - LAZY para no cargar el usuario en cada consulta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private User autor;

    /** Fecha de creación del documento */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    /** Fecha de última actualización del documento */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /** Contenido del documento */
    private String contenido;
    /** Categoría del documento (opcional, máximo 100 caracteres) */
    @Size(max = 100)
    private String categoria;
    /** Indica si el documento es público */
    private Boolean publico;

    /** Indica si se puede editar el documento */
    @Column(name = "editable")
    private Boolean editable = Boolean.TRUE;

    /** Invitaciones asociadas a este documento */
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitacion> invitados = new ArrayList<>();
}
