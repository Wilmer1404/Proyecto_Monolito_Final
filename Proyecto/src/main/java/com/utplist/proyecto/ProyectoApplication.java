package com.utplist.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * Punto de entrada para iniciar el backend del proyecto.
 */
@SpringBootApplication
public class ProyectoApplication {
    /**
     * Método principal que inicia la aplicación Spring Boot.
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }
} 