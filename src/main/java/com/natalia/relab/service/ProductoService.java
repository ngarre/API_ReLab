package com.natalia.relab.service;

import com.natalia.relab.model.Producto;
import com.natalia.relab.model.Usuario;
import com.natalia.relab.repository.ProductoRepository;
import exception.ProductoNoEncontradoException;
import exception.UsuarioNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Producto agregar(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listarTodos() {
        List<Producto> todosProductos = productoRepository.findAll();
        return todosProductos;
    }

    public Producto buscarPorId(long id) throws ProductoNoEncontradoException {
        return productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
    }

    public Producto modificar(long id, Producto producto) throws ProductoNoEncontradoException {
        Producto productoAnterior = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);

        productoAnterior.setNombre(producto.getNombre());
        productoAnterior.setDescripcion(producto.getDescripcion());
        productoAnterior.setPrecio(producto.getPrecio());
        productoAnterior.setFechaActualizacion(producto.getFechaActualizacion());
        productoAnterior.setActivo(producto.isActivo());

        return productoRepository.save(productoAnterior);
    }

    public void eliminar(long id) throws ProductoNoEncontradoException {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(ProductoNoEncontradoException::new);
        productoRepository.delete(producto);
    }

}
