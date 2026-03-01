package com.natalia.relab.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // SecurityFilterChain es una interfaz que define un filtro de seguridad para las solicitudes HTTP.

        /*
          La seguridad con JWT se aplica únicamente a las operaciones sensibles sobre usuarios.
          El token se envía manualmente en las peticiones PUT y DELETE de usuario mediante el header Authorization.
          El resto de recursos son públicos por simplicidad del proyecto.
        */


        http.csrf(csrf -> csrf.disable()) // No se usa protección CSRF porque JWT no necesita cookies ni formularios
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Spring no guarda sesión; cada request debe llevar su token
                )
                .authorizeHttpRequests(auth -> auth // Configuración de qué rutas son públicas y cuáles requieren autenticación

                        // Para el POST de NUEVOS USUARIOS no activo seguridad
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // Pero abro el GET de usuarios al endpoint que controla si existe el nickname
                        // devolverá true e false.
                        .requestMatchers(HttpMethod.GET, "/usuarios/check-nickname").permitAll()

                        /*
                              RESTO DE USUARIOS, PUT Y DELETE están cerrados y requieren
                              seguridad por token.  Allí la app de android trabaja con los tokens.
                         */



                        // y a continuación todos los permitidos...
                        .requestMatchers(
                                "/auth/**",
                                "/ping",
                                "/productos/**",
                                "/reviews/**",
                                "/servicios/**",
                                "/alquileres/**",
                                "/compraventas/**").permitAll()


                        .anyRequest().authenticated() // Cualquier otra ruta no listada antes requiere autenticación
                )
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);
                // Se inserta mi JwtFilter antes del filtro estándar de Spring Security que maneja la autenticación por username y password.

        return http.build(); // Se construye y devuelve la configuración de seguridad.
    }
}

// En esta clase:
// 1. Se desactiva CSRF y la sesión de Spring Security, porque no se usan cookies ni sesiones (la autenticación es con JWT)
// 2. Se definen permisos de acceso por endpoint: todos los recursos son públicos excepto PUT y DELETE de usuarios,
//    que requieren autenticación con JWT, es decir se define que rutas requieren token.
// 3. Se inyecta el filtro JwtFilter para que se valide el token en cada petición antes de llegar al controller.
//    Si el token es válido, se le dice a Spring Security que el usuario está autenticado y que la petición pertenece a
//    ese userId. Si el token no es válido, se responde con 401 Unauthorized.  Y si no hay token, se sigue con
//    la ejecución normal (recursos públicos).

