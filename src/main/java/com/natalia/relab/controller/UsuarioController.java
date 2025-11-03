package com.natalia.relab.controller;

import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import exception.ErrorResponse;
import exception.UsuarioNoEncontradoException;
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
            @RequestParam(value = "nickname", required = false) String nickname)
            throws UsuarioNoEncontradoException {

        if (nickname != null && !nickname.isEmpty()) {
            UsuarioOutDto usuario = usuarioService.buscarPorNickname(nickname);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.notFound().build(); // Genera una respuesta HTTP 404
            }
        } else {
            List<UsuarioOutDto> todosUsuarios = usuarioService.listarTodos();
            return ResponseEntity.ok(todosUsuarios); // Devuelve un 200 OK con la respuesta del usuario
        }
    }


    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> ListarPorId(@PathVariable long id) throws UsuarioNoEncontradoException {
        UsuarioOutDto dto = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> agregarUsuario(@RequestBody UsuarioInDto usuarioInDto) {
        UsuarioOutDto nuevoUsuario = usuarioService.agregar(usuarioInDto);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> editarUsuario(@PathVariable long id, @RequestBody UsuarioUpdateDto usuarioUpdateDto) throws UsuarioNoEncontradoException {
        UsuarioOutDto nuevoUsuario = usuarioService.modificar(id, usuarioUpdateDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable long id) throws UsuarioNoEncontradoException {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(UsuarioNoEncontradoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, "no-encontrado", "El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}


