package com.utplist.proyecto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Documento colaborativo.
 */
@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Título obligatorio, máximo 255 caracteres. */
    @NotBlank(message = "El título no puede estar vacío.")
    @Size(max = 255)
    private String titulo;

    /** Correo del autor (se mantiene por compatibilidad / consultas rápidas). */
    @NotBlank
    @Email
    @Column(name = "autor_correo", nullable = false)
    private String autorCorreo;

    /**
     * Relación con User.
     * - FK: autor_id → users.id
     * - LAZY para no cargar el usuario en cada consulta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private User autor;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    private String contenido;
    @Size(max = 100)
    private String categoria;
    private Boolean publico;

    /** Indica si se puede editar. */
    @Column(name = "editable")
    private Boolean editable = Boolean.TRUE;

    /** Ruta del archivo adjunto (Word o PDF) */
    @Column(name = "ruta_archivo")
    private String rutaArchivo;

    /** Invitaciones asociadas. */
    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitacion> invitados = new ArrayList<>();
}
