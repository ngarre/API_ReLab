package com.natalia.relab.controller;

import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.model.Categoria;
import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.service.CategoriaService;
import com.natalia.relab.service.ProductoService;
import exception.CategoriaNoEncontradaException;
import exception.ErrorResponse;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoOutDto>> listarTodos() {
        List<ProductoOutDto> todosProductos = productoService.listarTodos();
        return ResponseEntity.ok(todosProductos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> listarPorId(@PathVariable long id) throws ProductoNoEncontradoException {
        ProductoOutDto dto = productoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoOutDto> agregarProductos(@RequestBody ProductoInDto productoInDto)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        ProductoOutDto nuevoProducto = productoService.agregar(productoInDto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> actualizarProducto(@RequestBody ProductoInDto productoInDto, @PathVariable long id) throws ProductoNoEncontradoException, CategoriaNoEncontradaException, UsuarioNoEncontradoException {
        ProductoOutDto actualizado = productoService.modificar(id, productoInDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable long id) throws ProductoNoEncontradoException {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleExcpetion(ProductoNoEncontradoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, "no-encontrado", "El producto no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
