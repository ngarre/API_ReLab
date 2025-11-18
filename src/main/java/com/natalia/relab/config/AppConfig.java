package com.natalia.relab.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();


        // Evita que ModelMapper sobrescriba propiedades con null. --> Esto me daba problemas con operaciones PUT (Por ejemplo, en usuario no mandaba el campo saldo al actualizar y en la BBDD ese campo se actualizaba a Null).
        // Solo mapeará propiedades cuya fuente NO sea null.
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        return mapper;
    }

}
