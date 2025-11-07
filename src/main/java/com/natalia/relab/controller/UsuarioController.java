package com.natalia.relab.controller;

import com.natalia.relab.dto.UsuarioInDto;
import com.natalia.relab.dto.UsuarioOutDto;
import com.natalia.relab.dto.UsuarioUpdateDto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.UsuarioRepository;
import com.natalia.relab.service.UsuarioService;
import exception.ErrorResponse;
import exception.NicknameYaExisteException;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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


    // --- EXCEPCIONES PERSONALIZADAS ---

    // El usuario no existe
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(UsuarioNoEncontradoException uex) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    // Manejo la excepción de que un nickname ya esté en uso
    @ExceptionHandler(NicknameYaExisteException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(NicknameYaExisteException nye) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "nickname-duplicado", "El nickname ya está en uso");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Para gestionar errores de validación en usuario (Bean Validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException mane) {
        // Creo un mapa donde guardaré los errores de validación.
        // La clave será el nombre del campo (por ejemplo "email")
        // y el valor será el mensaje de error definido en la anotación (por ejemplo "Debe tener formato de email válido").
        Map<String, String> errors = new HashMap<>();

        // Recorro todos los errores de campo capturados por la validación (@Valid).
        // Por cada error, obtengo el nombre del campo y su mensaje de validación,
        // y lo añado al mapa 'errors'.
        mane.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Creo un objeto ErrorResponse con el listado de errores.
        // El metodo 'validationError' es un constructor estático que prepara una respuesta 400 (Bad Request)
        // con el mapa de errores de validación.
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);

        // Devuelvo la respuesta con el estado HTTP 400 y el cuerpo con los detalles de los errores.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}


