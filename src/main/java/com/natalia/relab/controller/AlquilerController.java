package com.natalia.relab.controller;

import com.natalia.relab.dto.*;
import com.natalia.relab.service.AlquilerService;
import exception.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class AlquilerController {

    private static final Logger log = LoggerFactory.getLogger(AlquilerController.class);

    @Autowired
    private AlquilerService alquilerService;

    @GetMapping("/alquileres")
    public ResponseEntity<List<AlquilerOutDto>> verTodos(
            @RequestParam(value="arrendadorId", required = false) Long arrendadorId,
            @RequestParam(value="arrendatarioId", required = false) Long arrendatarioId,
            @RequestParam(value="productoId", required = false) Long productoId)
            throws UsuarioNoEncontradoException, ProductoNoEncontradoException {

        log.info("GET /alquileres - Parámetros: arrendadorId={}, arrendatarioId={}, productoId={}",
                arrendadorId, arrendatarioId, productoId);

        List<AlquilerOutDto> alquileres = alquilerService.listarConFiltros(arrendadorId, arrendatarioId, productoId);

        log.info("Resultado: {} alquileres encontrados", alquileres.size());
        return ResponseEntity.ok(alquileres);

    }

    @GetMapping("/alquileres/{id}")
    public ResponseEntity<AlquilerOutDto> listarPorId(@PathVariable long id) throws AlquilerNoEncontradoException {
        log.info("GET /alquileres/{} solicitado", id);
        AlquilerOutDto dto = alquilerService.buscarPorId(id);
        log.info("Alquiler con id {} encontrado", id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/alquileres")
    public ResponseEntity<AlquilerOutDto> agregarAlquiler(@Valid @RequestBody AlquilerInDto alquilerInDto)
        throws UsuarioNoEncontradoException, ProductoNoEncontradoException {
        log.info("POST /alquileres - creando nuevo alquiler");
        AlquilerOutDto nuevoAlquiler = alquilerService.agregar(alquilerInDto);
        log.info("Alquiler creado con id {}", nuevoAlquiler.getId());
        return new ResponseEntity<>(nuevoAlquiler, HttpStatus.CREATED);
    }

    @PutMapping("/alquileres/{id}")
    public ResponseEntity<AlquilerOutDto> actualizarAlquiler(
            @Valid
            @RequestBody AlquilerUpdateDto alquilerUpdateDto,
            @PathVariable long id) throws
            AlquilerNoEncontradoException {
        log.info("PUT /alquileres/{} - actualizando alquiler", id);
        AlquilerOutDto actualizado = alquilerService.modificar(id, alquilerUpdateDto);
        log.info("Alquiler con id {} actualizado correctamente", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/alquileres/{id}")
    public ResponseEntity<Void>  eliminarAlquiler(@PathVariable long id) throws AlquilerNoEncontradoException {
        log.warn("DELETE /alquileres/{} - eliminando", id);
        alquilerService.eliminar(id);
        log.info("Alquiler con id {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}
