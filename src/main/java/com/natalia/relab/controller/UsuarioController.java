package com.natalia.relab.controller;

import com.natalia.relab.dto.UsuarioMobileOutDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.service.UsuarioService;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class); // Logger para la clase UsuarioController

    // Endpoint para obtener el perfil del usuario logueado usando JWT
    @GetMapping("/usuarios/me")
    public ResponseEntity<UsuarioMobileOutDto> miPerfil(Authentication authentication) throws UsuarioNoEncontradoException {
        log.info("GET /usuarios/me solicitado");
        Long usuarioId = (Long) authentication.getPrincipal(); // Se obtiene el userId del token JWT

        UsuarioMobileOutDto dto = usuarioService.obtenerPerfil(usuarioId);

        log.info("Perfil del usuario {} obtenido correctamente", usuarioId);
        return ResponseEntity.ok(dto);
    }

    // Endpoint para verificar si un nickname ya existe
    @GetMapping("/usuarios/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("nickname") String nickname) {
        boolean exists = usuarioService.nicknameExiste(nickname);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarTodos(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "tipoUsuario", required = false) String tipoUsuario,
            @RequestParam(value = "cuentaActiva", required = false) Boolean cuentaActiva)
            throws UsuarioNoEncontradoException {

        log.info("GET /usuarios - filtros: nickname={}, tipoUsuario={}, cuentaActiva={}", nickname, tipoUsuario, cuentaActiva);

        List<UsuarioOutDto> usuarios = usuarioService.listarConFiltros(nickname, password, tipoUsuario, cuentaActiva);

        log.info("Resultado: {} usuarios encontrados", usuarios.size());

        if (usuarios.size() == 1) {
            return ResponseEntity.ok(usuarios.getFirst()); // Devuelvo solo el primer usuario si en la lista solo hay uno.
            // Sin esto con el filtro de Login me devolvía un array.
        }
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> agregarUsuario(@Valid @RequestBody UsuarioInDto usuarioInDto) {
        log.info("POST /usuarios - creando usuario con nickname {}", usuarioInDto.getNickname());
        UsuarioOutDto nuevoUsuario = usuarioService.agregar(usuarioInDto);
        log.info("Usuario creado con id {}", nuevoUsuario.getId());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // PUT CON SEGURIDAD JWT
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> editarUsuario(
            @PathVariable long id,
            @RequestBody UsuarioUpdateDto dto,
            Authentication authentication) throws UsuarioNoEncontradoException {

        log.info("PUT /usuarios/{} solicitado", id);
        Long usuarioIdToken = (Long) authentication.getPrincipal(); // Se obtiene el userId del token JWT

        if (!usuarioIdToken.equals(id)) {
            log.warn("Usuario {} intentó actualizar el perfil de otro usuario {}", usuarioIdToken, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Solo el usuario puede actualizar su propio perfil
        }

        UsuarioOutDto nuevoUsuario = usuarioService.modificar(id, dto);
        log.info("Usuario {} actualizado correctamente", id);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Delete de un usuario con seguridad JWT
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable long id,
            Authentication authentication) throws UsuarioNoEncontradoException {

        log.info("DELETE /usuarios/{} solicitado", id);
        Long usuarioIdToken = (Long) authentication.getPrincipal(); // Se obtiene el userId del token JWT

        // Seguridad: Solo el usuario puede eliminar su propio perfil
        if (!usuarioIdToken.equals(id)) {
            log.warn("Usuario {} intentó eliminar el perfil de otro usuario {}", usuarioIdToken, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.eliminar(id);
        log.info("Usuario {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // Delete de una CUENTA con seguridad JWT (o sea, eliminar usuario y datos relacionados en resto de tablas)
    @DeleteMapping("/usuarios/{id}/cuenta")
    public ResponseEntity<Void> eliminarCuenta(
            @PathVariable long id,
            Authentication authentication) throws UsuarioNoEncontradoException {

        log.info("DELETE /usuarios/{}/cuenta solicitado", id);
        Long usuarioIdToken = (Long) authentication.getPrincipal(); // Se obtiene el userId del token JWT

        // Seguridad: Solo el usuario puede eliminar su propia cuenta
        if (!usuarioIdToken.equals(id)) {
            log.warn("Usuario {} intentó eliminar la cuenta de otro usuario {}", usuarioIdToken, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        usuarioService.eliminarCuenta(id);
        log.info("Cuenta del usuario {} eliminada correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}


