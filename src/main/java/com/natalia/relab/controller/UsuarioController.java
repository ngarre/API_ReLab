package com.natalia.relab.controller;

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

    @GetMapping("/usuarios")
    public ResponseEntity<?> listarTodos(
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "tipoUsuario", required = false) String tipoUsuario,
            @RequestParam(value = "cuentaActiva", required = false) Boolean cuentaActiva)
            throws UsuarioNoEncontradoException {

        List<UsuarioOutDto> usuarios = usuarioService.listarConFiltros(nickname, password, tipoUsuario, cuentaActiva);
        if (usuarios.size() == 1) {
            return ResponseEntity.ok(usuarios.getFirst()); // Devuelvo solo el primer usuario si en la lista solo hay uno.
            // Sin esto con el filtro de Login me devolvía un array.
        }
        return ResponseEntity.ok(usuarios);
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


