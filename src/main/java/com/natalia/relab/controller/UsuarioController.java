package com.natalia.relab.controller;

import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.service.UsuarioService;
import exception.ErrorResponse;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarTodos(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "tipoUsuario", required = false) String tipoUsuario,
            @RequestParam(value = "cuentaActiva", required = false) Boolean cuentaActiva)
            throws UsuarioNoEncontradoException {

        // Login: nickname + password
        if (nickname != null && !nickname.isEmpty() && password != null && !password.isEmpty()) {
            UsuarioOutDto usuario = usuarioService.login(nickname, password);
            return ResponseEntity.ok(usuario);
        }

        // Filtrado solo por nickname
        if (nickname != null && !nickname.isEmpty()) {
            UsuarioOutDto usuario = usuarioService.buscarPorNickname(nickname);
            return ResponseEntity.ok(usuario);
        }

        // Filtrado solo por tipoUsuario
        if (tipoUsuario != null && !tipoUsuario.isEmpty()) {
            List<UsuarioOutDto> usuarios = usuarioService.buscarPorTipoUsuario(tipoUsuario);
            return ResponseEntity.ok(usuarios);
        }

        // Filtrado por cuentaActiva
        if  (cuentaActiva != null) {
            List<UsuarioOutDto> usuarios = usuarioService.filtrarPorCuentaActiva(cuentaActiva);
            return ResponseEntity.ok(usuarios);
        }

        // Todos los usuarios
        List<UsuarioOutDto> todosUsuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(todosUsuarios);
    }


    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> ListarPorId(@PathVariable long id) throws UsuarioNoEncontradoException {
        UsuarioOutDto dto = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> agregarUsuario(@Valid @RequestBody UsuarioInDto usuarioInDto) {
        UsuarioOutDto nuevoUsuario = usuarioService.agregar(usuarioInDto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> editarUsuario(@Valid @PathVariable long id, @RequestBody UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        UsuarioOutDto nuevoUsuario = usuarioService.modificar(id, usuarioUpdateDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable long id) throws UsuarioNoEncontradoException {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}


