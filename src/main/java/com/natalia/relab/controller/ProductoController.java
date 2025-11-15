package com.natalia.relab.controller;

import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.dto.ProductoUpdateDto;
import com.natalia.relab.service.CategoriaService;
import com.natalia.relab.service.ProductoService;
import com.natalia.relab.service.UsuarioService;
import exception.CategoriaNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/productos")
    public ResponseEntity<?> listarTodos(
            @RequestParam(value="nombre", required = false) String nombre,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        // Filtrado por nombre --> Coincidencias parciales y sin distinguir mayúsculas y minúsculas
        if (nombre != null && !nombre.isEmpty()) {
            List<ProductoOutDto> productos = productoService.buscarPorNombreParcial(nombre);
            return ResponseEntity.ok(productos);
        }

        // Filtrado por activo
        if (activo != null) {
            List<ProductoOutDto> productos = productoService.buscarActivos(activo);
            return ResponseEntity.ok(productos);
        }

        // Filtrado por categoría (id de la categoría a la que pertenece el producto)
        if (categoriaId != null) {
            // Se verifica que la categoría exista
            categoriaService.buscarPorId(categoriaId); // Esto lanza la excepción de Categoría no Encontrada si no existe.

            List<ProductoOutDto> productos = productoService.buscarPorCategoriaId(categoriaId);
            return ResponseEntity.ok(productos);
        }

        // Filtrado por usuario (id del usuario al que pertenece el producto)
        if (usuarioId != null) {
            // Se verifica que el usuario exista
            usuarioService.buscarPorId(usuarioId); // Esto lanza la excepción de Usuario no Encontrado si no existe.

            List<ProductoOutDto> productos = productoService.buscarPorUsuarioId(usuarioId);
            return ResponseEntity.ok(productos);
        }


        // Todos los productos
        List<ProductoOutDto> todosProductos = productoService.listarTodos();
        return ResponseEntity.ok(todosProductos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> listarPorId(@PathVariable long id) throws ProductoNoEncontradoException {
        ProductoOutDto dto = productoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoOutDto> agregarProductos(@Valid @RequestBody ProductoInDto productoInDto)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        ProductoOutDto nuevoProducto = productoService.agregar(productoInDto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> actualizarProducto(@Valid @RequestBody ProductoUpdateDto productoUpdateDto, @PathVariable long id) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
        ProductoOutDto actualizado = productoService.modificar(id, productoUpdateDto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable long id) throws ProductoNoEncontradoException {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

// --- Me llevo excepciones a GlobalExceptionHandler
}
