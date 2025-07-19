package com.utplist.proyecto.controller;

import com.utplist.proyecto.service.IFeatureFlagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Feature Flags", description = "Consulta de flags de características activas")
@RestController
@RequestMapping("/feature-flags")
@RequiredArgsConstructor
public class FeatureFlagController {
    private final IFeatureFlagService featureFlagService;

    @Operation(summary = "Consultar si una feature flag está habilitada")
    @GetMapping("/{nombre}")
    public ResponseEntity<Boolean> isEnabled(
            @Parameter(description = "Nombre de la feature flag", required = true)
            @PathVariable String nombre) {
        return ResponseEntity.ok(featureFlagService.isEnabled(nombre));
    }

    @Operation(summary = "Habilitar una feature flag")
    @PostMapping("/{nombre}/habilitar")
    public ResponseEntity<String> habilitar(
            @Parameter(description = "Nombre de la feature flag", required = true)
            @PathVariable String nombre) {
        featureFlagService.habilitar(nombre);
        return ResponseEntity.ok("Feature flag '" + nombre + "' habilitada correctamente");
    }
} 