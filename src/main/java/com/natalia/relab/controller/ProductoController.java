package com.natalia.relab.controller;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.service.ProductoService;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/productos")
    public List<Producto> listarTodos() {
        List<Producto> todosProductos = productoService.listarTodos();
        return todosProductos;
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<Producto> ListarPorId(@PathVariable long id) throws ProductoNoEncontradoException {
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos")
    public void agregarProductos(@RequestBody Producto producto) {
       productoService.agregar(producto);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@RequestBody Producto producto, @PathVariable long id) throws ProductoNoEncontradoException {
        Producto nuevoProducto = productoService.modificar(id, producto);
        return ResponseEntity.ok(nuevoProducto);
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable long id) throws ProductoNoEncontradoException {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
