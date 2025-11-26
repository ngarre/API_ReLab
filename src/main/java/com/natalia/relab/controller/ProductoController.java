package com.natalia.relab.controller;

import com.natalia.relab.dto.ProductoInDto;
import com.natalia.relab.dto.ProductoOutDto;
import com.natalia.relab.dto.ProductoUpdateDto;
import com.natalia.relab.model.Producto;
import com.natalia.relab.service.ProductoService;
import exception.CategoriaNoEncontradaException;
import exception.ImagenNoEncontradaException;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @GetMapping("/productos")
    public ResponseEntity<?> listarTodos(
            @RequestParam(value="nombre", required = false) String nombre,
            @RequestParam(value = "activo", required = false) Boolean activo,
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        log.info("GET /productos - filtros: nombre={}, activo={}, categoriaId={}, usuarioId={}",
                nombre, activo, categoriaId, usuarioId);

        List<ProductoOutDto> productos = productoService.listarConFiltrado(nombre, activo, categoriaId, usuarioId);

        log.info("GET /productos - encontrados {} productos", productos.size());
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> listarPorId(@PathVariable long id) throws ProductoNoEncontradoException {

        log.info("GET /productos/{} - solicitando producto por ID", id);

        ProductoOutDto dto = productoService.buscarPorId(id);

        log.info("GET /productos/{} - producto encontrado correctamente", id);
        return ResponseEntity.ok(dto);
    }

// Versión que NO permite la SUBIDA DE IMÁGENES
//    @PostMapping("/productos")
//    public ResponseEntity<ProductoOutDto> agregarProductos(@Valid @RequestBody ProductoInDto productoInDto)
//            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {
//
//            ProductoOutDto nuevoProducto = productoService.agregar(productoInDto);
//            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
//    }

    // Versión que PERMITE SUBIDA DE IMÁGENES
    @PostMapping("/productos")
    public ResponseEntity<ProductoOutDto> agregarProducto(@Valid @RequestBody ProductoInDto productoInDto)
            throws CategoriaNoEncontradaException, UsuarioNoEncontradoException {

        log.info("POST /productos - creando nuevo producto. Nombre={}, UsuarioId={}, CategoriaId={}",
                productoInDto.getNombre(),
                productoInDto.getUsuarioId(),
                productoInDto.getCategoriaId());

        // Llamo al servicio para agregar el producto, pasando el DTO que incluye la imagen
        ProductoOutDto nuevoProducto = productoService.agregarConImagen(productoInDto);

        log.info("POST /productos - producto creado con ID {}", nuevoProducto.getId());
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED); // Retorno el nuevo producto creado con el código de estado 201 (CREATED)
    }


    // Versión que NO permite ACTUALIZAR IMAGEN
//    @PutMapping("/productos/{id}")
//    public ResponseEntity<ProductoOutDto> actualizarProducto(@Valid @RequestBody ProductoUpdateDto productoUpdateDto, @PathVariable long id) throws ProductoNoEncontradoException, CategoriaNoEncontradaException {
//        ProductoOutDto actualizado = productoService.modificar(id, productoUpdateDto);
//        return ResponseEntity.ok(actualizado);
//    }

    // Versión que PERMITE ACTUALIZACIÓN DE IMÁGENES
    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoOutDto> actualizarProducto(@Valid @RequestBody ProductoUpdateDto productoUpdateDto, @PathVariable long id)
            throws ProductoNoEncontradoException, CategoriaNoEncontradaException {

        log.info("PUT /productos/{} - actualización solicitada", id);

        // Llamo al servicio para actualizar el producto, pasando el ID y el DTO con los datos actualizados
        ProductoOutDto productoActualizado = productoService.actualizarConImagen(id, productoUpdateDto);

        log.info("PUT /productos/{} - producto actualizado correctamente", id);
        return ResponseEntity.ok(productoActualizado); // Devuelvo el producto actualizado
    }


    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable long id) throws ProductoNoEncontradoException {

        log.warn("DELETE /productos/{} - eliminación solicitada", id);

        productoService.eliminar(id);

        log.warn("DELETE /productos/{} - producto eliminado", id);
        return ResponseEntity.noContent().build();
    }

    //  Endpoint específico para servir la imagen como archivo binario:
    @GetMapping("/productos/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) throws ProductoNoEncontradoException, ImagenNoEncontradaException {

        log.info("GET /productos/{}/imagen - solicitando imagen", id);

        Producto producto = productoService.buscarPorIdEntidad(id);

        if (producto.getImagen() == null) {
            throw new ImagenNoEncontradaException();
        }

        log.info("GET /productos/{}/imagen - imagen servida correctamente", id);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(producto.getImagen());
    }

    // --- Me llevo excepciones a GlobalExceptionHandler

}
