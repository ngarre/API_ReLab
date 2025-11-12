package com.natalia.relab.controller;


import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaUpdateDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.service.CategoriaService;
import exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta)
            throws CategoriaNoEncontradaException {

        // Filtrado por nombre --> Coincidencias parciales y sin distinguir mayúsculas y minúsculas
        if (nombre != null && !nombre.isEmpty()) {
            List<CategoriaOutDto> categorias = categoriaService.buscarPorNombreParcial(nombre);
            return ResponseEntity.ok(categorias);
        }

        // Filtrado por activo
        if (activa != null) {
            List<CategoriaOutDto> categorias = categoriaService.buscarActivas(activa);
            return ResponseEntity.ok(categorias);
        }

        // Filtrado por fecha exacta o rango ("Desde-hasta" o "desde-hasta fecha actual")
        if (fechaCreacion != null || desde != null || hasta != null) {
            List<CategoriaOutDto> categorias = categoriaService.buscarPorFecha(fechaCreacion, desde, hasta);
            return ResponseEntity.ok(categorias);
        }

        // Todas las categorias
        List<CategoriaOutDto> todasCategorias = categoriaService.listarTodas();
        return ResponseEntity.ok(todasCategorias);
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

