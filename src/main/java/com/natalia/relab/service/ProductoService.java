package com.natalia.relab.service;

import com.natalia.relab.model.Producto;
import com.natalia.relab.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public void agregar(Producto producto) {
        productoRepository.save(producto);
    }

    public List<Producto> listarTodos() {
        List<Producto> todosProductos = productoRepository.findAll();
        return todosProductos;
    }

}
