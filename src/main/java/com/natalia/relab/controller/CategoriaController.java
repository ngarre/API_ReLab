package com.natalia.relab.controller;


import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.dto.CategoriaUpdateDto;
import com.natalia.relab.service.CategoriaService;
import exception.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CategoriaController {

    private static final Logger log = LoggerFactory.getLogger(CategoriaController.class);

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

        log.info("GET /categorías - filtros: nombre='{}', activa={}, fechaCreacion={}, desde={}, hasta={}",
                nombre, activa, fechaCreacion, desde, hasta);

        List<CategoriaOutDto> categorias = categoriaService.listarConFiltros(nombre, activa, fechaCreacion, desde, hasta);
        log.info("Se devolvieron {} categorias", categorias.size());

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> listarPorId(@PathVariable long id) throws CategoriaNoEncontradaException {
        log.info("GET /categorias/{} - Solicitando categoría por ID", id);
        CategoriaOutDto dto = categoriaService.buscarPorId(id);
        log.info("GET /categorias/{} - Categoría encontrada correctamente", id);
        return ResponseEntity.ok(dto);

    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaOutDto> agregarCategorias(@Valid @RequestBody CategoriaInDto categoriaInDto) {
        log.info("POST /categorias - Creación de nueva categoría solicitada");
        CategoriaOutDto nuevaCategoria = categoriaService.agregar(categoriaInDto);
        log.info("POST /categorias - Nueva categoría creada con ID {}", nuevaCategoria.getId());
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> actualizarCategoria(@Valid @RequestBody CategoriaUpdateDto categoriaUpdateDto, @PathVariable long id) throws CategoriaNoEncontradaException {
        log.info("PUT /categorias/{} - Actualización de categoría solicitada", id);
        CategoriaOutDto actualizada = categoriaService.modificar(id, categoriaUpdateDto);
        log.info("PUT /categorias/{} - Categoría actualizada correctamente", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable long id) throws CategoriaNoEncontradaException {
        log.warn("DELETE /categorias/{} - Eliminación solicitada", id);
        categoriaService.eliminar(id);
        log.info("Categoría {} eliminada correctamente", id);
        return ResponseEntity.noContent().build();
    }

    // --- Me llevo excepciones a GlobalExceptionHandler
}

