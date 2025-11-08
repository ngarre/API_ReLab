package com.natalia.relab.controller;

import exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //(basePackages = "com.natalia.relab.controller") Me permite poner en todas las clases anotación
// para no repetir en cada controller los bloques de código de los "handler". Pongo esa línea de basePackages porque
// de lo contrario es necesario tener este archivo en la carpeta controller.  Con esto le digo a Spring que aplique mis
// handlers a los controllers que estén en este paquete y subpaquetes.
public class GlobalExceptionHandler {

    // --- VALIDACIONES @Valid ---
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Creo un mapa donde guardaré los errores de validación.
        // La clave será el nombre del campo (por ejemplo "email")
        // y el valor será el mensaje de error definido en la anotación (por ejemplo "Debe tener formato de email válido").
        Map<String, String> errors = new HashMap<>();

        // Recorro todos los errores de campo capturados por la validación (@Valid).
        // Por cada error, obtengo el nombre del campo y su mensaje de validación,
        // y lo añado al mapa 'errors'.
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Creo un objeto ErrorResponse con el listado de errores.
        // El metodo 'validationError' es un constructor estático que prepara una respuesta 400 (Bad Request)
        // con el mapa de errores de validación.
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);

        // Devuelvo la respuesta con el estado HTTP 400 y el cuerpo con los detalles de los errores.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // --- NO ENCONTRADO (404) ---
    @ExceptionHandler({
            UsuarioNoEncontradoException.class,
            ProductoNoEncontradoException.class,
            AlquilerNoEncontradoException.class,
            CompraventaNoEncontradaException.class,
            CategoriaNoEncontradaException.class
    })

    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        String message = switch (ex.getClass().getSimpleName()) {
            case "UsuarioNoEncontradoException" -> "El usuario no existe";
            case "ProductoNoEncontradoException" -> "El producto no existe";
            case "AlquilerNoEncontradoException" -> "El alquiler no existe";
            case "CompraventaNoEncontradaException" -> "La compraventa no existe";
            case "CategoriaNoEncontradaException" -> "La categoria no existe";
            default -> "El recurso no fue encontrado";
        };

        ErrorResponse errorResponse = ErrorResponse.notFound(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // --- DATOS DUPLICADOS (400) ---
    @ExceptionHandler({NicknameYaExisteException.class, NombreYaExisteException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateData(RuntimeException ex) {
        String title;
        String message;

        if (ex instanceof NicknameYaExisteException) {
            title = "nickname-duplicado";
            message = "El nickname ya está en uso";
        } else { // NombreYaExisteException
            title = "nombre-duplicado";
            message = "El nombre ya está en uso";
        }

        ErrorResponse errorResponse = ErrorResponse.generalError(HttpStatus.BAD_REQUEST.value(), title, message); // el valor de bad request es 400.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // --- OTROS ERRORES INESPERADOS (500) ---
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralError(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "error-interno", "Error interno del servidor");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
