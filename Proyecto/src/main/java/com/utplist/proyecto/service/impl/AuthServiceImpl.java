package com.utplist.proyecto.service.impl;

import com.utplist.proyecto.dto.*;
import com.utplist.proyecto.model.Role;
import com.utplist.proyecto.model.User;
import com.utplist.proyecto.repository.UserRepository;
import com.utplist.proyecto.config.JwtUtil;
import com.utplist.proyecto.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Implementación del servicio de autenticación y gestión de usuarios.
 * Permite registrar usuarios, administradores, moderadores, iniciar sesión y validar tokens JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario con rol USUARIO.
     * @param request Datos del usuario
     */
    @Override
    public void register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USUARIO)
                .build();
        userRepository.save(user);
    }
    /**
     * Inicia sesión y retorna un JWT si las credenciales son válidas.
     * @param request Datos de inicio de sesión
     * @return DTO con el token y rol
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user);
        return new LoginResponseDTO(token, user.getRole().name());
    }
    /**
     * Registra un nuevo administrador con rol ADMINISTRADOR.
     * @param request Datos del administrador
     */
    @Override
    public void registerAdmin(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMINISTRADOR)
                .build();
        userRepository.save(user);
    }

    /**
     * Registra un nuevo moderador con rol MODERADOR.
     * @param request Datos del moderador
     */
    @Override
    public void registerModerador(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.MODERADOR)
                .build();
        userRepository.save(user);
    }

    /**
     * Valida un token JWT y retorna información del usuario si es válido.
     * @param token Token JWT
     * @return Mapa con información del usuario
     */
    @Override
    public Map<String, Object> validateToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }
        String email = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Map.of(
                "valid", true,
                "email", user.getEmail(),
                "role", user.getRole().name()
        );
    }
} 