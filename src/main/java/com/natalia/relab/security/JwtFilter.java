package com.natalia.relab.security;



import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter { // Que extienda OncePerRequestFilter hace que se ejecute una vez por cada petición HTTP

    @Autowired
    private JwtUtil jwtUtil; // Inyectamos JwtUtil para validar el token, verificar firma y extraer claims (userId)

    // Este metodo se ejecuta en TODAS las peticiones antes de llegar al controller
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, // Objeto que representa la petición HTTP
            HttpServletResponse response, // Objeto que representa la respuesta HTTP
            FilterChain filterChain // Objeto que representa la cadena de filtros (para seguir con la ejecución de la petición)
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization"); // Se mira si la petición trae un header Authorization y si no existe es null

        if (authHeader != null && authHeader.startsWith("Bearer ")) { // Si el header existe y empieza con "Bearer " (formato típico de un JWT)
            try {
                String token = authHeader.substring(7); // Se extrae el token quitando "Bearer " (los primeros 7 caracteres)
                Claims claims = jwtUtil.validateToken(token); // Se valida el token usando JwtUtil, si el token no es válido (firma incorrecta o expirado) se lanza una excepción y se responde con 401 Unauthorized

                Long userId = claims.get("userId", Long.class); // Si el token es válido, se extrae el userId del token (que se guardó como claim al generar el token)


                // Se crea un objeto de autenticación de Spring Security con el userId (sin password ni roles)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId, null, List.of()
                        );

                // Se le dice a Spring Security que el usuario está autenticado y que la petición pertenece a ese userId
                // Permite que en los controllers se pueda obtener el userId con @AuthenticationPrincipal Long userId
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
                // Si el token no es válido, se responde con 401 Unauthorized y se detiene la ejecución de la petición
            }
        }

        filterChain.doFilter(request, response); // Continúa la petición (si el token es válido o si no hay token, se sigue con la ejecución normal, llegando al controller correspondiente)
    }
}

// En esta clase, se intercepta cada petición HTTP que llega a mi API (antes de que se ejecute cualquier controller)
// y se hace lo siguiente:
//   1. Mira si la petición trae un header Authorization
//   2. Si hay un JWT (Bearer xxx.yyy.zzz), se valida el token usando JwtUtil (firma + expiración)
//   3. Si el token es válido, se extrae el userId del token
//   4. Le dice a Spring Security que el usuario está autenticado y que la petición pertenece a ese userId
//   5. Si el token no es válido, se responde con 401 Unauthorized


