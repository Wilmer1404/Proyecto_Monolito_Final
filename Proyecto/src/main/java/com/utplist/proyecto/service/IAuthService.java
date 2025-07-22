package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.LoginRequestDTO;
import com.utplist.proyecto.dto.LoginResponseDTO;
import com.utplist.proyecto.dto.RegisterRequestDTO;
import java.util.Map;

/**
 * Interfaz de servicio para la autenticación y gestión de usuarios.
 * Define operaciones para registrar, autenticar y validar usuarios y tokens.
 */
public interface IAuthService {
    /**
     * Registra un nuevo usuario con rol USUARIO.
     * @param request Datos del usuario
     */
    void register(RegisterRequestDTO request);
    /**
     * Registra un nuevo usuario con rol ADMINISTRADOR.
     * @param request Datos del administrador
     */
    void registerAdmin(RegisterRequestDTO request);
    /**
     * Registra un nuevo usuario con rol MODERADOR.
     * @param request Datos del moderador
     */
    void registerModerador(RegisterRequestDTO request);
    /**
     * Inicia sesión y retorna un JWT si las credenciales son válidas.
     * @param request Datos de inicio de sesión
     * @return DTO con el token y rol
     */
    LoginResponseDTO login(LoginRequestDTO request);
    /**
     * Valida un token JWT y retorna información del usuario si es válido.
     * @param token Token JWT
     * @return Mapa con información del usuario
     */
    Map<String, Object> validateToken(String token);
} 