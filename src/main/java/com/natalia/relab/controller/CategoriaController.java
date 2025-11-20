package com.natalia.relab.controller;


import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaUpdateDto;
import com.natalia.relab.service.CategoriaService;
import exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    @GetMapping("/categorias")
    public ResponseEntity<?> listarTodas(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "activa", required = false) Boolean activa,
            // @DateTimeFormat indica a Spring cómo parsear fechas desde la URL
            // ISO.DATE obliga al formato estándar yyyy-MM-dd
            @RequestParam(value = "fechaCreacion", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCreacion,
            @RequestParam(value = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<CategoriaOutDto> categorias = categoriaService.listarConFiltros(nombre, activa, fechaCreacion, desde, hasta);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> listarPorId(@PathVariable long id) throws CategoriaNoEncontradaException {
        CategoriaOutDto dto = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(dto);

    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaOutDto> agregarCategorias(@Valid @RequestBody CategoriaInDto categoriaInDto) {
        CategoriaOutDto nuevaCategoria = categoriaService.agregar(categoriaInDto);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> actualizarCategoria(@Valid @RequestBody CategoriaUpdateDto categoriaUpdateDto, @PathVariable long id) throws CategoriaNoEncontradaException {
        CategoriaOutDto actualizada = categoriaService.modificar(id, categoriaUpdateDto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable long id) throws CategoriaNoEncontradaException {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}

