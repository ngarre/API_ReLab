package com.natalia.relab.controller;

import com.natalia.relab.dto.*;
import com.natalia.relab.service.AlquilerService;
import com.natalia.relab.service.ProductoService;
import com.natalia.relab.service.UsuarioService;
import exception.*;
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
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProductoService productoService;

    @GetMapping("/alquileres")
    public ResponseEntity<List<AlquilerOutDto>> verTodos(
            @RequestParam(value="arrendadorId", required = false) Long arrendadorId,
            @RequestParam(value="arrendatarioId", required = false) Long arrendatarioId,
            @RequestParam(value="productoId", required = false) Long productoId)
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        if (arrendadorId !=null){
            // Se verifica que el usuario arrendador exista
            usuarioService.buscarPorId(arrendadorId);

            List<AlquilerOutDto> alquileres = alquilerService.buscarPorArrendadorId(arrendadorId);
            return ResponseEntity.ok(alquileres);
        }

        if (arrendatarioId !=null){
            // Se verifica que el usuario arrendatario exista
            usuarioService.buscarPorId(arrendatarioId);

            List<AlquilerOutDto> alquileres = alquilerService.buscarPorArrendatarioId(arrendatarioId);
            return ResponseEntity.ok(alquileres);
        }

        if (productoId !=null){
            // Se verifica que el producto exista
            productoService.buscarPorId(productoId);

            List<AlquilerOutDto> alquileres = alquilerService.buscarPorProductoId(productoId);
            return ResponseEntity.ok(alquileres);
        }


        List<AlquilerOutDto> todosAlquileres = alquilerService.listarTodos();
        return ResponseEntity.ok(todosAlquileres);
    }

    @GetMapping("/alquileres/{id}")
    public ResponseEntity<AlquilerOutDto> listarPorId(@PathVariable long id) throws AlquilerNoEncontradoException {
        AlquilerOutDto dto = alquilerService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/alquileres")
    public ResponseEntity<AlquilerOutDto> agregarAlquiler(@Valid @RequestBody AlquilerInDto alquilerInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        AlquilerOutDto nuevoAlquiler = alquilerService.agregar(alquilerInDto);
        return new ResponseEntity<>(nuevoAlquiler, HttpStatus.CREATED);
    }

    @PutMapping("/alquileres/{id}")
    public ResponseEntity<AlquilerOutDto> actualizarAlquiler(
            @Valid
            @RequestBody AlquilerUpdateDto alquilerUpdateDto,
            @PathVariable long id) throws
            AlquilerNoEncontradoException {

        AlquilerOutDto actualizado = alquilerService.modificar(id, alquilerUpdateDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/alquileres/{id}")
    public ResponseEntity<Void>  eliminarAlquiler(@PathVariable long id) throws AlquilerNoEncontradoException {
        alquilerService.eliminar(id);
        return ResponseEntity.noContent().build();
    }


    // --- EXCEPCIONES PERSONALIZADAS ---

    // Alquiler no existe
    @ExceptionHandler(AlquilerNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(AlquilerNoEncontradoException aex) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El registro de alquiler no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // El usuario no existe
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(UsuarioNoEncontradoException uex) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // El producto no existe
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(ProductoNoEncontradoException pex) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El producto no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Para gestionar errores de validación en alquiler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException mane) {
        Map<String, String> errors = new HashMap<>();
        mane.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
