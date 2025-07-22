package com.utplist.proyecto.config;

import com.utplist.proyecto.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad para la generación, validación y extracción de información de tokens JWT.
 */
@Component
public class JwtUtil {
    private final String jwtSecret = "F3B8492E98E34F2DA7B9CE6B82FC1D99";
    private final long jwtExpirationMs = 86400000; // 1 día
    private Key key;

    /**
     * Inicializa la clave secreta para firmar los tokens JWT.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario dado.
     * @param user Usuario para el cual se genera el token
     * @return Token JWT generado
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si un token JWT es válido y no ha expirado.
     * @param token Token JWT a validar
     * @return true si es válido, false si no
     */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario (email) de un token JWT.
     * @param token Token JWT
     * @return Email del usuario
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
} 