package com.natalia.relab.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();


        // Evita que ModelMapper sobrescriba propiedades con null. --> Esto me daba problemas con operaciones PUT (Por ejemplo, en usuario no mandaba el campo saldo al actualizar y en la BBDD ese campo se actualizaba a Null).
        // Solo mapeará propiedades cuya fuente NO sea null.
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }

    // Configuración de CORS para permitir solicitudes desde el frontend en localhost:5173
    // - Necesario para mi web en React
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET","POST","PUT","DELETE");
            }
        };
    }
}
