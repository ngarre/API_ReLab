package com.natalia.relab.controller;

import com.natalia.relab.dto.AuthResponse;
import com.natalia.relab.dto.LoginRequest;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        logger.info("Intentando login con nickname: {}", request.getNickname());

        // Validar usuario + password usando findByNicknameAndPassword
        Usuario usuario = usuarioRepository
                .findByNicknameAndPassword(request.getNickname(), request.getPassword())
                .orElse(null);

        if (usuario == null) {
            // Credenciales incorrectas
            logger.info("Usuario no encontrado para ese nickname y contraseña");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Generar JWT solo con el ID del usuario
        String token = jwtUtil.generateToken(usuario);

        // Devolver token en DTO
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

// En este controlador, el endpoint /auth/login recibe un LoginRequest (DTO de entrada) con nickname y password,
// valida las credenciales usando el repositorio de usuarios, y si son correctas, genera un JWT con el ID del usuario y
// lo devuelve en un AuthResponse (DTO de salida). Si las credenciales son incorrectas, devuelve un 401 Unauthorized.
