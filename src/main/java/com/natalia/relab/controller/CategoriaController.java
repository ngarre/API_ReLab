package com.natalia.relab.controller;


import com.natalia.relab.dto.CategoriaInDto;
import com.natalia.relab.dto.CategoriaOutDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.service.CategoriaService;
import exception.CategoriaNoEncontradaException;
import exception.ErrorResponse;
import exception.ProductoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaOutDto>> listarTodas() {
        List<CategoriaOutDto> todasCategorias = categoriaService.listarTodas();
        return ResponseEntity.ok(todasCategorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> listarPorId(@PathVariable long id) throws CategoriaNoEncontradaException {
        CategoriaOutDto dto = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(dto);

    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaOutDto> agregarCategorias(@RequestBody CategoriaInDto categoriaInDto) {
        CategoriaOutDto nuevaCategoria = categoriaService.agregar(categoriaInDto);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> actualizarCategoria(@RequestBody CategoriaInDto categoriaInDto, @PathVariable long id) throws CategoriaNoEncontradaException {
        CategoriaOutDto actualizada = categoriaService.modificar(id, categoriaInDto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable long id) throws CategoriaNoEncontradaException {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(CategoriaNoEncontradaException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, "no-encontrada", "La categoria no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}

