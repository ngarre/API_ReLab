package com.natalia.relab.controller;

import com.natalia.relab.dto.*;
import com.natalia.relab.service.AlquilerService;
import exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @GetMapping("/alquileres")
    public ResponseEntity<List<AlquilerOutDto>> verTodos(
            @RequestParam(value="arrendadorId", required = false) Long arrendadorId,
            @RequestParam(value="arrendatarioId", required = false) Long arrendatarioId,
            @RequestParam(value="productoId", required = false) Long productoId)
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        List<AlquilerOutDto> alquileres = alquilerService.listarConFiltros(arrendadorId, arrendatarioId, productoId);
        return ResponseEntity.ok(alquileres);

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

    // --- Me llevo excepciones a GlobalExceptionHandler
}
