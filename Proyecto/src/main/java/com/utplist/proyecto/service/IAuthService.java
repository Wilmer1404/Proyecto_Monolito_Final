package com.utplist.proyecto.service;

import com.utplist.proyecto.dto.LoginRequestDTO;
import com.utplist.proyecto.dto.LoginResponseDTO;
import com.utplist.proyecto.dto.RegisterRequestDTO;
import java.util.Map;

public interface IAuthService {
    void register(RegisterRequestDTO request);
    void registerAdmin(RegisterRequestDTO request);
    void registerModerador(RegisterRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
    Map<String, Object> validateToken(String token);
} 