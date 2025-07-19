package com.utplist.proyecto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proyecto Monolítico - UTPL")
                        .description("API REST unificada para autenticación, documentos, eventos y más.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo UTPL")
                                .email("soporte@utpl.edu.ec")
                                .url("https://utpl.edu.ec"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
} 