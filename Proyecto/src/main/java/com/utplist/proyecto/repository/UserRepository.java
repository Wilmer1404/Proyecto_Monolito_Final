package com.utplist.proyecto.repository;

import com.utplist.proyecto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * Permite operaciones CRUD y consultas personalizadas sobre usuarios.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca un usuario por su correo electrónico.
     * @param email Correo electrónico
     * @return Usuario encontrado (opcional)
     */
    Optional<User> findByEmail(String email);
    /**
     * Verifica si existe un usuario con el correo electrónico dado.
     * @param email Correo electrónico
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
} 