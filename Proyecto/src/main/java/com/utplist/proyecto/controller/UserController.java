package com.utplist.proyecto.controller;

import com.utplist.proyecto.dto.CambiarRolDTO;
import com.utplist.proyecto.dto.UserDTO;
import com.utplist.proyecto.model.Role;
import com.utplist.proyecto.model.User;
import com.utplist.proyecto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMINISTRADOR')")
    public List<UserDTO> listarUsuarios() {
        return userRepository.findAll().stream()
                .map(u -> new UserDTO(u.getId(), u.getEmail(), u.getRole().name()))
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SUPERADMINISTRADOR')")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody CambiarRolDTO dto) {
        User user = userRepository.findById(id).orElseThrow();
        try {
            Role nuevoRol = Role.valueOf(dto.getNuevoRol());
            user.setRole(nuevoRol);
            userRepository.save(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Rol no válido");
        }
    }
} 