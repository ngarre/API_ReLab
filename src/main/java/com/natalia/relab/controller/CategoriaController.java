package com.natalia.relab.controller;


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
    public ResponseEntity<List<Categoria>> listarTodas() {
        List<Categoria> todasCategorias = categoriaService.listarTodas();
        return ResponseEntity.ok(todasCategorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<Categoria> listarPorId(@PathVariable long id) throws CategoriaNoEncontradaException {
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);

    }

    @PostMapping("/categorias")
    public ResponseEntity<Categoria> agregarCategorias(@RequestBody Categoria categoria) {
        Categoria nuevaCategoria = categoriaService.agregar(categoria);
        return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@RequestBody Categoria categoria, @PathVariable long id) throws CategoriaNoEncontradaException {
        Categoria nuevaCategoria = categoriaService.modificar(id, categoria);
        return ResponseEntity.ok(nuevaCategoria);
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

