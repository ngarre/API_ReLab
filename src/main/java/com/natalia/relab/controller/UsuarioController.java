package com.natalia.relab.controller;

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
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class); // Logger para la clase UsuarioController

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

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> ListarPorId(@PathVariable long id) throws UsuarioNoEncontradoException {
        log.info("GET /usuarios/{} solicitado", id);
        UsuarioOutDto dto = usuarioService.buscarPorId(id);
        log.info("Usuario con id {} encontrado", id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> agregarUsuario(@Valid @RequestBody UsuarioInDto usuarioInDto) {
        log.info("POST /usuarios - creando usuario con nickname {}", usuarioInDto.getNickname());
        UsuarioOutDto nuevoUsuario = usuarioService.agregar(usuarioInDto);
        log.info("Usuario creado con id {}", nuevoUsuario.getId());
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> editarUsuario(@Valid @PathVariable long id, @RequestBody UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        log.info("PUT /usuarios/{} - actualización solicitada", id);
        UsuarioOutDto nuevoUsuario = usuarioService.modificar(id, usuarioUpdateDto);
        log.info("Usuario {} actualizado correctamente", id);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable long id) throws UsuarioNoEncontradoException {
        log.warn("DELETE /usuarios/{} solicitado", id); // DELETE → mejor WARN
        usuarioService.eliminar(id);
        log.info("Usuario {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}


