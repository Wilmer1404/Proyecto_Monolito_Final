package com.utplist.proyecto.controller;

import com.utplist.proyecto.service.IFeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión y consulta de feature flags (banderas de funcionalidad).
 * Permite consultar y habilitar flags desde la API.
 */
@Tag(name = "Feature Flags", description = "Consulta de flags de características activas")
@RestController
@RequestMapping("/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {
    private final IFeatureFlagService featureFlagService;

    /**
     * Consulta si una feature flag está habilitada.
     * @param nombre Nombre de la feature flag
     * @return true si está habilitada, false si no
     */
    @Operation(summary = "Consultar si una feature flag está habilitada")
    @GetMapping("/{nombre}")
    public ResponseEntity<Boolean> isEnabled(
            @Parameter(description = "Nombre de la feature flag", required = true)
            @PathVariable String nombre) {
        return ResponseEntity.ok(featureFlagService.isEnabled(nombre));
    }

    /**
     * Habilita una feature flag.
     * @param nombre Nombre de la feature flag
     * @return Mensaje de éxito
     */
    @Operation(summary = "Habilitar una feature flag")
    @PostMapping("/{nombre}/habilitar")
    public ResponseEntity<String> habilitar(
            @Parameter(description = "Nombre de la feature flag", required = true)
            @PathVariable String nombre) {
        featureFlagService.habilitar(nombre);
        return ResponseEntity.ok("Feature flag '" + nombre + "' habilitada correctamente");
    }
} 