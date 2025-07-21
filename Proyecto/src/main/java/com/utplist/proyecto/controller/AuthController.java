package com.utplist.proyecto.controller;

import com.utplist.proyecto.dto.LoginRequestDTO;
import com.utplist.proyecto.dto.LoginResponseDTO;
import com.utplist.proyecto.dto.RegisterRequestDTO;
import com.utplist.proyecto.config.JwtUtil;
import com.utplist.proyecto.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticación", description = "Operaciones de registro, login y validación de usuarios")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Registrar un nuevo usuario")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PreAuthorize("hasRole('SUPERADMINISTRADOR')")
    @Operation(summary = "Registrar un nuevo administrador")
    @PostMapping("/register/admin")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody RegisterRequestDTO request) {
        authService.registerAdmin(request);
        return ResponseEntity.ok("Admin registered successfully");
    }

    @Operation(summary = "Iniciar sesión y obtener JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PreAuthorize("hasRole('SUPERADMINISTRADOR')")
    @Operation(summary = "Registrar un nuevo Moderador")
    @PostMapping("/register/moderator")
    public ResponseEntity<String> registerModerador(@Valid @RequestBody RegisterRequestDTO request) {
        authService.registerModerador(request);
        return ResponseEntity.ok("Moderator registered successfully");
    }

    @Operation(summary = "Validar un token JWT")
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @Parameter(description = "Token JWT en el header Authorization", required = true)
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token not provided");
        }
        String token = authHeader.replace("Bearer ", "");
        try {
            return ResponseEntity.ok(authService.validateToken(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
} 