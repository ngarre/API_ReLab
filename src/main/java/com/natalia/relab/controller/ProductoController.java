package com.natalia.relab.controller;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/productos")
    public void agregarProductos(@RequestBody Producto producto) {
       productoService.agregar(producto);
    }

}
