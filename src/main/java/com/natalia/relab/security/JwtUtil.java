package com.natalia.relab.security;



import com.natalia.relab.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta para firmar el token
    // (en producción se saca a application.properties)
    // Se genera clave aleatoria al arrancar la app, por lo que los tokens no son válidos tras reiniciar la app
    // La clave está en memoria dentro de la aplicación Java, no se expone en ningún endpoint ni se guarda en la base de datos, por lo que es segura
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // El token dura 24 horas
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;


    // Genera un token JWT a partir de un usuario, incluyendo su nickname como subject y otros datos como claims
    public String generateToken(Usuario usuario) {

        return Jwts.builder() // La librería JJWT crea el header automáticamente aunque no lo veamos explícitamente
                .setSubject(usuario.getNickname())
                .claim("userId", usuario.getId()) // Un claim es un dato adicional que queremos incluir en el token
                .claim("admin", usuario.isAdmin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key) // Firma el token con la clave secreta
                .compact();
    }

    // Validar un token JWT y devuelve sus claims (datos incluidos en el token)
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Configura la clave secreta para validar la firma del token
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
